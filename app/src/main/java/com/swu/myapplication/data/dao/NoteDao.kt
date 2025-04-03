package com.swu.myapplication.data.dao

import androidx.room.*
import com.swu.myapplication.data.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY created_at DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE notebookId = :notebookId ORDER BY created_at DESC")
    fun getNotesByNotebook(notebookId: Long): Flow<List<Note>>

    @Insert
    suspend fun insert(note: Note): Long

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Query("SELECT COUNT(*) FROM notes")
    suspend fun getNoteCount(): Int

    @Query("SELECT COUNT(*) FROM notes WHERE notebookId = :notebookId")
    suspend fun getNoteCountByNotebook(notebookId: Long): Int
} 