package com.swu.myapplication.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "segments",
    foreignKeys = [
        ForeignKey(
            entity = Note::class,
            parentColumns = ["id"],
            childColumns = ["noteId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Segment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val noteId: Long,         // 外键关联notes表
    val character: String,    // 分词字符（A/W/E等）
    val order: Int            // 显示顺序
) 