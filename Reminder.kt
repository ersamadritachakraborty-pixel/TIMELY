package com.samadrita.timely.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Core data model for a single reminder.
 * timeInMillis stores the exact epoch time the reminder should fire.
 */
@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val note: String,
    val timeInMillis: Long,
    val isCompleted: Boolean = false,
    val priority: Priority = Priority.NORMAL
)

enum class Priority {
    LOW, NORMAL, HIGH
}
