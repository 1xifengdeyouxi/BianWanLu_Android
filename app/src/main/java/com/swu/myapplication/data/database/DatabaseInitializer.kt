package com.swu.myapplication.data.database

import android.content.Context
import android.icu.util.UniversalTimeScale.toLong
import com.swu.myapplication.data.model.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/*
object DatabaseInitializer {
    fun insertSampleData(context: Context) {
        val database = AppDatabase.getDatabase(context)
        val noteDao = database.noteDao()
        val currentTime = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(Date())

        val sampleNotes = listOf(
            Note(
                title = "未来发展方向",
                content = "刚刚考虑一下是否要考研 地方撒范德萨范德萨范德萨范德萨范德萨啊范德萨范德萨，总感觉不够用...",

            ),
            Note(
                title = "大二下课程",
                content = "2025/3/6 数据采集与预处理 范德萨范德萨...",
                createdAt = currentTime,
                updatedAt = currentTime
            ),
            Note(
                title = "密码小本本",
                content = "Gitee:12138147258wm. 范德萨范德萨..",
                createdAt = currentTime,
                updatedAt = currentTime
            ),
            Note(
                title = "软件设计大赛",
                content = "2024/6/20 大数据技能大赛 所以的在读啊vcxz范德萨3-5人",
                createdAt = currentTime,
                updatedAt = currentTime
            ),
            Note(
                title = "软件设计大赛",
                content = "2024/6/20 大数据技能大赛 所以的在读3-5人撒发v出现在范德萨fds",
                createdAt = currentTime,
                updatedAt = currentTime
            ),
            Note(
                title = "软件设计大赛",
                content = "2024/6/20 大数据技能大赛 所以的在读3-5人放大撒反对萨芬的萨芬的撒fd",
                createdAt = currentTime,
                updatedAt = currentTime
            ),
            Note(
                title = "软件设计大赛",
                content = "2024/6/20 大数据技能大赛 所以的在读3-5人积分抵扣撒泼就发的骚i就附近的撒飞机滴哦撒分基地扫否决掉啊分基地扫飞机迪斯科i哦啊附近的靠沙拉" +
                        "发动机送i啊发的决赛哦",
                createdAt = currentTime,
                updatedAt = currentTime
            ),
            Note(
                title = "软件设计大赛",
                content = "2024/6/20 大数据技能大赛 所以的在读3-5人 范德萨发单号i阿富汗Jodi会计师案件佛的撒就开了分店就撒开了；附近的撒考虑加分的考生了附近的开始啦附近的开始啦飞机打瞌睡附近的开始啦",
                createdAt = currentTime,
                updatedAt = currentTime
            )
        )

        CoroutineScope(Dispatchers.IO).launch {
            sampleNotes.forEach { note ->
                noteDao.insert(note)
            }
        }
    }
}
*/