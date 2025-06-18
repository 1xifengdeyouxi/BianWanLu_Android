package com.swu.myapplication.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "notes",
    foreignKeys = [
        ForeignKey(
            entity = Notebook::class,
            parentColumns = ["id"],
            childColumns = ["notebookId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val notebookId: Long,
    val isTodo: Boolean = false,
    val isCompleted: Boolean = false,
    val createdTime: Long = System.currentTimeMillis(),
    val modifiedTime: Long = System.currentTimeMillis(),
    val sortOrder: Int = 0  // 添加排序字段，用于拖拽排序
) {
    fun copyWithModification(
        title: String = this.title,
        content: String = this.content,
        modifiedTime: Long = System.currentTimeMillis()
    ) = this.copy(
        title = title,
        content = content,
        modifiedTime = modifiedTime
    )

    fun copyWithSortOrder(sortOrder: Int) = this.copy(sortOrder = sortOrder)
    companion object {
        //-1代表的是，默认是创建笔记本
        const val INVALID_ID = -1L
    }
}