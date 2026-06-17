package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PrayerDao {
    @Query("SELECT * FROM prayer_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<PrayerLog>>

    @Query("SELECT * FROM prayer_logs WHERE date = :date")
    fun getLogsForDate(date: String): Flow<List<PrayerLog>>

    @Query("SELECT * FROM prayer_logs WHERE date = :date")
    suspend fun getLogsForDateSync(date: String): List<PrayerLog>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: PrayerLog)

    @Delete
    suspend fun deleteLog(log: PrayerLog)

    @Query("DELETE FROM prayer_logs WHERE date = :date AND prayerName = :prayerName")
    suspend fun deleteLogForPrayer(date: String, prayerName: String)

    // Settings
    @Query("SELECT * FROM prayer_settings")
    fun getAllSettings(): Flow<List<PrayerSetting>>

    @Query("SELECT * FROM prayer_settings WHERE prayerName = :prayerName")
    suspend fun getSettingForPrayer(prayerName: String): PrayerSetting?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetting(setting: PrayerSetting)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: List<PrayerSetting>)
}
