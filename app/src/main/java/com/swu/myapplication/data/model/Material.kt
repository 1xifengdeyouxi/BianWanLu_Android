package com.swu.myapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "materials")
data class Material(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,           // 教材名称
    val author: String,         // 作者
    val publisher: String,      // 出版社
    val isbn: String,           // ISBN号
    val price: Double,          // 价格
    val courseCode: String,     // 课程代码
    val courseName: String,     // 课程名称
    val semester: String        // 学期
) 