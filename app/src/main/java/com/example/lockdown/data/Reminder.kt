package com.example.lockdown.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val triggerAtMillis: Long,
    val soundUri: String?,     // null = sonido de alarma por defecto del sistema
    val isActive: Boolean = true
)