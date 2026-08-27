package com.example.lockdown.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "unlock_challenges")
data class UnlockChallenge(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val state: ChallengeState,
    val startElapsedRealtime: Long,   // SystemClock.elapsedRealtime() al crear el reto
    val endElapsedRealtime: Long,     // start + duración aleatoria de 8-12h
    val totalSteps: Int,
    val completedSteps: Int = 0,
    val createdAtWallClock: Long = System.currentTimeMillis() // solo para mostrar fecha al usuario
)