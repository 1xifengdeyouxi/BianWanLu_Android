package com.swu.myapplication.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.swu.myapplication.data.dao.NoteDao
import com.swu.myapplication.data.dao.NotebookDao
import com.swu.myapplication.data.dao.SegmentDao
import com.swu.myapplication.data.dao.TimerDao
import com.swu.myapplication.data.dao.TodoDao
import com.swu.myapplication.data.dao.TodoCategoryDao
import com.swu.myapplication.data.entity.Timer
import com.swu.myapplication.data.model.Note
import com.swu.myapplication.data.model.Notebook
import com.swu.myapplication.data.model.Segment
import com.swu.myapplication.data.model.Todo
import com.swu.myapplication.data.model.TodoCategory

@Database(
    entities = [Note::class, Segment::class, Notebook::class, Todo::class, TodoCategory::class, Timer::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun segmentDao(): SegmentDao
    abstract fun notebookDao(): NotebookDao
    abstract fun todoDao(): TodoDao
    abstract fun todoCategoryDao(): TodoCategoryDao
    abstract fun timerDao(): TimerDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "notes_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

@androidx.room.TypeConverters
class Converters {
    @androidx.room.TypeConverter
    fun fromTimestamp(value: Long?): java.util.Date? {
        return value?.let { java.util.Date(it) }
    }

    @androidx.room.TypeConverter
    fun dateToTimestamp(date: java.util.Date?): Long? {
        return date?.time
    }
} 