package com.example.ui.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.PrayerLog
import com.example.data.local.PrayerSetting
import com.example.data.repository.PrayerRepository
import com.example.model.City
import com.example.model.Hadith
import com.example.model.INDONESIAN_CITIES
import com.example.model.CURATED_HADITHS
import com.example.util.CompassManager
import com.example.util.PrayerTimeCalculator
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(private val repository: PrayerRepository) : ViewModel() {

    private val calendar = Calendar.getInstance()
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private val _currentDate = MutableStateFlow(dateFormatter.format(calendar.time))
    val currentDate: StateFlow<String> = _currentDate.asStateFlow()

    // Location coordinates (Default to Jakarta, Indonesia)
    private val _latitude = MutableStateFlow(-6.2088)
    val latitude: StateFlow<Double> = _latitude.asStateFlow()

    private val _longitude = MutableStateFlow(106.8456)
    val longitude: StateFlow<Double> = _longitude.asStateFlow()

    private val _timezone = MutableStateFlow(7.0) // GMT +7 (WIB)
    val timezone: StateFlow<Double> = _timezone.asStateFlow()

    private val _currentCityName = MutableStateFlow("Jakarta")
    val currentCityName: StateFlow<String> = _currentCityName.asStateFlow()

    // Calculated Prayer Times
    private val _prayerTimes = MutableStateFlow<Map<String, String>>(emptyMap())
    val prayerTimes: StateFlow<Map<String, String>> = _prayerTimes.asStateFlow()

    // Reactive log items for current date
    val currentDateLogs: StateFlow<List<PrayerLog>> = _currentDate
        .flatMapLatest { date -> repository.getLogsForDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All settings
    val settingsState: StateFlow<List<PrayerSetting>> = repository.allSettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected Hadith
    private val _selectedHadith = MutableStateFlow(CURATED_HADITHS[Calendar.getInstance().get(Calendar.DAY_OF_MONTH) % CURATED_HADITHS.size])
    val selectedHadith: StateFlow<Hadith> = _selectedHadith.asStateFlow()

    // AI Explanation Status
    private val _aiExplanation = MutableStateFlow<String?>(null)
    val aiExplanation: StateFlow<String?> = _aiExplanation.asStateFlow()

    private val _aiLoading = MutableStateFlow(false)
    val aiLoading: StateFlow<Boolean> = _aiLoading.asStateFlow()

    // Device magnetic heading
    private val _compassHeading = MutableStateFlow(0f)
    val compassHeading: StateFlow<Float> = _compassHeading.asStateFlow()

    init {
        // Prepopulate settings if empty
        viewModelScope.launch {
            repository.initializeSettingsIfNeeded()
        }
        recalculatePrayerTimes()
    }

    fun recalculatePrayerTimes() {
        val dateParts = _currentDate.value.split("-")
        if (dateParts.size == 3) {
            val year = dateParts[0].toIntOrNull() ?: calendar.get(Calendar.YEAR)
            val month = dateParts[1].toIntOrNull() ?: (calendar.get(Calendar.MONTH) + 1)
            val day = dateParts[2].toIntOrNull() ?: calendar.get(Calendar.DAY_OF_MONTH)

            val calculated = PrayerTimeCalculator.calculatePrayerTimes(
                latitude = _latitude.value,
                longitude = _longitude.value,
                timezone = _timezone.value,
                year = year,
                month = month,
                day = day
            )
            _prayerTimes.value = calculated
        }
    }

    fun setDate(dateStr: String) {
        _currentDate.value = dateStr
        recalculatePrayerTimes()
    }

    fun selectPresetCity(city: City) {
        _latitude.value = city.latitude
        _longitude.value = city.longitude
        _timezone.value = city.timezone
        _currentCityName.value = city.name
        recalculatePrayerTimes()
    }

    @SuppressLint("MissingPermission")
    fun requestGpsLocation(context: Context) {
        val client = LocationServices.getFusedLocationProviderClient(context)
        try {
            client.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    _latitude.value = location.latitude
                    _longitude.value = location.longitude
                    
                    // Division of Indonesian time zones: WIB <=110, WITA <=130, WIT >130
                    val tz = if (location.longitude < 110.0) 7.0 
                             else if (location.longitude < 130.0) 8.0 
                             else 9.0
                    _timezone.value = tz
                    _currentCityName.value = "GPS (Lat: %.3f)".format(location.latitude)
                    recalculatePrayerTimes()
                }
            }
        } catch (e: SecurityException) {
            // No permission
        }
    }

    // Toggle Attendance Log (Absensi)
    fun togglePrayerAttendance(prayerName: String) {
        viewModelScope.launch {
            val existing = currentDateLogs.value.find { it.prayerName == prayerName }
            if (existing != null) {
                repository.deleteLogForPrayer(_currentDate.value, prayerName)
            } else {
                val newLog = PrayerLog(
                    date = _currentDate.value,
                    prayerName = prayerName,
                    timestamp = System.currentTimeMillis(),
                    isCompleted = true,
                    isOnTime = true
                )
                repository.insertLog(newLog)
            }
        }
    }

    // Settings Updates
    fun updateNotificationSetting(setting: PrayerSetting) {
        viewModelScope.launch {
            repository.saveSetting(setting)
        }
    }

    // Hadith Cycling
    fun nextHadith() {
        val currIdx = CURATED_HADITHS.indexOf(_selectedHadith.value)
        val nextIdx = (currIdx + 1) % CURATED_HADITHS.size
        _selectedHadith.value = CURATED_HADITHS[nextIdx]
        _aiExplanation.value = null // clear explanation when hadith cycles
    }

    // Gemini Inquiry
    fun askGeminiForTadabbur() {
        val h = _selectedHadith.value
        viewModelScope.launch {
            _aiLoading.value = true
            val explanation = com.example.data.api.GeminiClient.explainHadith(
                arabic = h.arabic,
                indonesian = h.indonesian,
                topic = h.topic
            )
            _aiExplanation.value = explanation
            _aiLoading.value = false
        }
    }

    class Factory(private val repository: PrayerRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
