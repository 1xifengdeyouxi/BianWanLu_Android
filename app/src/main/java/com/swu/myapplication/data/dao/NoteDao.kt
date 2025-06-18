package com.swu.myapplication.data.dao

import androidx.room.*
import com.swu.myapplication.data.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY sortOrder ASC, modifiedTime DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE notebookId = :notebookId ORDER BY sortOrder ASC, modifiedTime DESC")
    fun getNotesByNotebookId(notebookId: Long): Flow<List<Note>>

    @Insert
    suspend fun insert(note: Note): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: Long): Note?

    @Query("SELECT * FROM notes WHERE title LIKE :query OR content LIKE :query ORDER BY sortOrder ASC, modifiedTime DESC")
    fun searchNotes(query: String): Flow<List<Note>>

    @Query("UPDATE notes SET sortOrder = :sortOrder WHERE id = :noteId")
    suspend fun updateNoteSortOrder(noteId: Long, sortOrder: Int)

    @Query("SELECT MAX(sortOrder) FROM notes WHERE notebookId = :notebookId")
    suspend fun getMaxSortOrder(notebookId: Long): Int?

    @Query("UPDATE notes SET sortOrder = sortOrder + 1 WHERE notebookId = :notebookId AND sortOrder >= :sortOrder")
    suspend fun incrementSortOrderFrom(notebookId: Long, sortOrder: Int)

    @Query("SELECT COUNT(*) FROM notes WHERE notebookId = :notebookId")
    suspend fun getNoteCountInNotebook(notebookId: Long): Int
} 