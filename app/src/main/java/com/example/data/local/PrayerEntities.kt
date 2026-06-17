package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "prayer_logs",
    indices = [Index(value = ["date", "prayerName"], unique = true)]
)
data class PrayerLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String, // "yyyy-MM-dd"
    val prayerName: String, // "Subuh", "Dzuhur", "Ashar", "Maghrib", "Isya"
    val timestamp: Long,
    val isCompleted: Boolean = true,
    val isOnTime: Boolean = true
)

@Entity(tableName = "prayer_settings")
data class PrayerSetting(
    @PrimaryKey val prayerName: String, // "Subuh", "Dzuhur", "Ashar", "Maghrib", "Isya"
    val isReminderEnabled: Boolean = true,
    val notificationType: String = "ADZAN" // "ADZAN", "NOTIFIKASI", "SENYAP"
)
