package com.swu.myapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_categories")
data class TodoCategory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var name: String,
    var color: Int = 0xFF4285F4.toInt(),  // 默认蓝色
    var todoCount: Int = 0,
    val creationTime: Long = System.currentTimeMillis()
) 