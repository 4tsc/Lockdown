package com.example.lockdown.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders WHERE isActive = 1 ORDER BY triggerAtMillis")
    fun observeActive(): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE isActive = 1")
    suspend fun getAllActive(): List<Reminder>

    @Query("SELECT * FROM reminders WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): Reminder?

    @Insert
    suspend fun insert(reminder: Reminder): Long

    @Delete
    suspend fun delete(reminder: Reminder)
}