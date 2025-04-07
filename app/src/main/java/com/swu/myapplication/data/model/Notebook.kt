package com.swu.myapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notebooks")
data class Notebook(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var name: String,
    var noteCount: Int = 0,
    var createdTime: Long = System.currentTimeMillis(),
    var modifiedTime: Long = System.currentTimeMillis()
){
    companion object {
        //-2代表是全部笔记本标签
        const val ALL_NOTEBOOK_ID = -2L
    }
}

