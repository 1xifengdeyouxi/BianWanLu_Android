package com.swu.myapplication.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "timers")
data class Timer(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val durationMinutes: Int,
    val atmosphereTitle: String? = null,
    val atmosphereImageUri: String? = null,
    val createdTime: Long = System.currentTimeMillis()
) : Serializable 