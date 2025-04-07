package com.swu.myapplication.data.dao

import androidx.room.*
import com.swu.myapplication.data.model.Notebook
import kotlinx.coroutines.flow.Flow

@Dao
interface NotebookDao {
    @Query("SELECT * FROM notebooks ORDER BY modifiedTime DESC")
    fun getAllNotebooks(): Flow<List<Notebook>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notebook: Notebook): Long

    @Update
    suspend fun update(notebook: Notebook)

    @Delete
    suspend fun delete(notebook: Notebook)

    @Query("SELECT * FROM notebooks WHERE id = :id")
    suspend fun getNotebookById(id: Long): Notebook?

    @Query("UPDATE notebooks SET noteCount = noteCount + 1 WHERE id = :notebookId")
    suspend fun incrementNoteCount(notebookId: Long)

    @Query("UPDATE notebooks SET noteCount = noteCount - 1 WHERE id = :notebookId")
    suspend fun decrementNoteCount(notebookId: Long)

    @Query("SELECT COUNT(*) FROM notebooks")
    suspend fun getNotebookCount(): Int
} 