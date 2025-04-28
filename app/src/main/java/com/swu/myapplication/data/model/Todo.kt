package com.swu.myapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todos")
data class Todo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var title: String,
    var content: String = "",
    var isCompleted: Boolean = false,
    var isStarred: Boolean = false,
    val creationTime: Long = System.currentTimeMillis(),
    var modificationTime: Long = System.currentTimeMillis(),
    var reminderDate: Long? = null,      // 日历提醒时间
    var reminderTime: Long? = null,      // 闹钟提醒时间
    var categoryId: Long = -1            // 待办分类ID
) 