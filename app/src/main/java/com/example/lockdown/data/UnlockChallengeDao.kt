package com.example.lockdown.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UnlockChallengeDao {
    @Query("SELECT * FROM unlock_challenges WHERE state = 'ACTIVE' LIMIT 1")
    suspend fun getActive(): UnlockChallenge?

    @Query("SELECT * FROM unlock_challenges WHERE state = 'ACTIVE' LIMIT 1")
    fun observeActive(): Flow<UnlockChallenge?>

    @Insert
    suspend fun insert(challenge: UnlockChallenge): Long

    @Query("UPDATE unlock_challenges SET state = :state WHERE id = :id")
    suspend fun setState(id: Long, state: ChallengeState)

    @Query("UPDATE unlock_challenges SET completedSteps = completedSteps + 1 WHERE id = :id")
    suspend fun incrementCompleted(id: Long)
}