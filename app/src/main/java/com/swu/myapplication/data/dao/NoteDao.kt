package com.swu.myapplication.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.swu.myapplication.data.model.Note

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY created_at DESC")
    fun getAllNotes(): LiveData<List<Note>>

    // 新增：按修改时间排序（新逻辑）
    @Query("SELECT * FROM notes ORDER BY updated_at DESC")
    fun getAllNotesByModifiedTime(): LiveData<List<Note>>  // 注意方法名和字段名

    @Query("SELECT * FROM notes WHERE notebook = :notebook ORDER BY created_at DESC")
    fun getNotesByNotebook(notebook: String): LiveData<List<Note>>

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' ORDER BY created_at DESC")
    fun searchNotes(query: String): LiveData<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note)

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Query("SELECT COUNT(*) FROM notes")
    fun getNoteCount(): LiveData<Int>
} 