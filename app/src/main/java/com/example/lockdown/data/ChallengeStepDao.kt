package com.example.lockdown.data

import androidx.room.*

@Dao
interface ChallengeStepDao {
    @Query("SELECT * FROM challenge_steps WHERE challengeId = :challengeId ORDER BY stepIndex")
    suspend fun getStepsForChallenge(challengeId: Long): List<ChallengeStep>

    @Query("SELECT * FROM challenge_steps WHERE challengeId = :challengeId AND status = 'PENDING' ORDER BY stepIndex LIMIT 1")
    suspend fun getNextPending(challengeId: Long): ChallengeStep?

    @Insert
    suspend fun insertAll(steps: List<ChallengeStep>)

    @Query("UPDATE challenge_steps SET status = :status WHERE id = :id")
    suspend fun setStatus(id: Long, status: StepStatus)
}