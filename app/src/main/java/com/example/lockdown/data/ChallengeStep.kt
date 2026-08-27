package com.example.lockdown.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "challenge_steps",
    foreignKeys = [ForeignKey(
        entity = UnlockChallenge::class,
        parentColumns = ["id"],
        childColumns = ["challengeId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("challengeId")]
)
data class ChallengeStep(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val challengeId: Long,
    val stepIndex: Int,
    val scheduledElapsedRealtime: Long,          // cuándo debe sonar la notificación
    val responseDeadlineElapsedRealtime: Long,   // hasta cuándo puede responderla
    val status: StepStatus = StepStatus.PENDING
)