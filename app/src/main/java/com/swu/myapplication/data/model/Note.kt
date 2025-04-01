package com.swu.myapplication.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "title")
    val title: String,
    
    @ColumnInfo(name = "content")
    val content: String,      // 符部分内容（JSON格式）
    
    @ColumnInfo(name = "notebook")
    val notebook: String,     // 默认笔记本/其他分类
    
    @ColumnInfo(name = "created_at")
    val createdAt: String,   // 2025/3/31 21:16格式
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: String
) 