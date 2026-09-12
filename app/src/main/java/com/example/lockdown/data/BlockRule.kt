package com.example.lockdown.data

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "block_rules")
data class BlockRule(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val packageName: String,
    val dayOfWeek: Int,
    val startMinute: Int,
    val endMinute: Int,
    val isEnabled: Boolean = true
)