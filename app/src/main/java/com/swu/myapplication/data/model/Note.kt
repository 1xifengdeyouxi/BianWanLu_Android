package com.swu.myapplication.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "notes",
    foreignKeys = [
        ForeignKey(
            entity = Notebook::class,
            parentColumns = ["id"],
            childColumns = ["notebookId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "title")
    val title: String,
    
    @ColumnInfo(name = "content")
    val content: String,      // 符部分内容（JSON格式）
    
    @ColumnInfo(name = "created_at")
    val createdAt: String,   // 2025/3/31 21:16格式
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: String,
    
    @ColumnInfo(name = "notebookId")
    val notebookId: Long? = null
) 