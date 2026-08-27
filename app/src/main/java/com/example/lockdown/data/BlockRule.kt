package com.example.lockdown.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// dayOfWeek usa java.time.DayOfWeek.value: 1 = Lunes ... 7 = Domingo
// startMinute / endMinute: minutos desde medianoche (0..1439)
// Si endMinute < startMinute, el rango cruza la medianoche (ej. 22:00 a 02:00)
@Entity(tableName = "block_rules")
data class BlockRule(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val packageName: String,
    val dayOfWeek: Int,
    val startMinute: Int,
    val endMinute: Int,
    val isEnabled: Boolean = true
)