package com.example.data.repository

import com.example.data.local.PrayerDao
import com.example.data.local.PrayerLog
import com.example.data.local.PrayerSetting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class PrayerRepository(private val prayerDao: PrayerDao) {

    val allLogs: Flow<List<PrayerLog>> = prayerDao.getAllLogs()
    val allSettings: Flow<List<PrayerSetting>> = prayerDao.getAllSettings()

    fun getLogsForDate(date: String): Flow<List<PrayerLog>> = prayerDao.getLogsForDate(date)

    suspend fun insertLog(log: PrayerLog) = withContext(Dispatchers.IO) {
        prayerDao.insertLog(log)
    }

    suspend fun deleteLogForPrayer(date: String, prayerName: String) = withContext(Dispatchers.IO) {
        prayerDao.deleteLogForPrayer(date, prayerName)
    }

    suspend fun saveSetting(setting: PrayerSetting) = withContext(Dispatchers.IO) {
        prayerDao.insertSetting(setting)
    }

    suspend fun initializeSettingsIfNeeded() = withContext(Dispatchers.IO) {
        val prayers = listOf("Subuh", "Dzuhur", "Ashar", "Maghrib", "Isya")
        val currentSettings = mutableListOf<PrayerSetting>()
        for (prayer in prayers) {
            val s = prayerDao.getSettingForPrayer(prayer)
            if (s == null) {
                currentSettings.add(PrayerSetting(prayerName = prayer, isReminderEnabled = true, notificationType = "ADZAN"))
            }
        }
        if (currentSettings.isNotEmpty()) {
            prayerDao.insertSettings(currentSettings)
        }
    }
}
