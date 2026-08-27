package com.example.lockdown.data

import androidx.room.TypeConverter

enum class ChallengeState { ACTIVE, COMPLETED, FAILED }
enum class StepStatus { PENDING, RESPONDED, MISSED }

class Converters {
    @TypeConverter
    fun fromChallengeState(value: ChallengeState): String = value.name

    @TypeConverter
    fun toChallengeState(value: String): ChallengeState = ChallengeState.valueOf(value)

    @TypeConverter
    fun fromStepStatus(value: StepStatus): String = value.name

    @TypeConverter
    fun toStepStatus(value: String): StepStatus = StepStatus.valueOf(value)
}