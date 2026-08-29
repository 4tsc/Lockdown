package com.example.lockdown.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockRuleDao {
    @Query("SELECT * FROM block_rules WHERE dayOfWeek = :dayOfWeek ORDER BY startMinute")
    fun observeForDay(dayOfWeek: Int): Flow<List<BlockRule>>

    @Query("SELECT * FROM block_rules ORDER BY dayOfWeek, startMinute")
    fun observeAll(): Flow<List<BlockRule>>

    @Query("SELECT * FROM block_rules WHERE isEnabled = 1")
    suspend fun getAllEnabled(): List<BlockRule>

    @Query("SELECT EXISTS(SELECT 1 FROM block_rules WHERE isEnabled = 1)")
    fun observeHasAnyEnabledRule(): kotlinx.coroutines.flow.Flow<Boolean>

    @Insert
    suspend fun insert(rule: BlockRule): Long

    @Update
    suspend fun update(rule: BlockRule)

    @Delete
    suspend fun delete(rule: BlockRule)
}