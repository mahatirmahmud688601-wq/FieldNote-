package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String = "",
    val frequency: String = "daily", // daily, weekdays, weekly
    val streak: Int = 0,
    val bestStreak: Int = 0,
    val completedDates: String = "", // Comma-separated YYYY-MM-DD
    val targetDaysPerWeek: Int = 7,
    val category: String = "Wellness",
    val color: String = "#8FA378",
    val createdAt: Long = System.currentTimeMillis()
)
