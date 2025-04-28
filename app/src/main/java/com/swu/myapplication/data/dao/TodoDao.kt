package com.swu.myapplication.data.dao

import androidx.room.*
import com.swu.myapplication.data.model.Todo
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM todos ORDER BY isCompleted ASC, modificationTime DESC")
    fun getAllTodos(): Flow<List<Todo>>

    @Query("SELECT * FROM todos WHERE id = :todoId")
    suspend fun getTodoById(todoId: Long): Todo?

    @Query("SELECT * FROM todos WHERE categoryId = :categoryId ORDER BY isCompleted ASC, modificationTime DESC")
    fun getTodosByCategory(categoryId: Long): Flow<List<Todo>>

    @Query("SELECT * FROM todos WHERE reminderDate IS NOT NULL OR reminderTime IS NOT NULL ORDER BY reminderDate ASC, reminderTime ASC")
    fun getTodosWithReminders(): Flow<List<Todo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo: Todo): Long

    @Update
    suspend fun update(todo: Todo)

    @Delete
    suspend fun delete(todo: Todo)

    @Query("DELETE FROM todos WHERE id IN (:todoIds)")
    suspend fun deleteTodos(todoIds: List<Long>)

    @Query("UPDATE todos SET isCompleted = :isCompleted WHERE id = :todoId")
    suspend fun updateCompletionStatus(todoId: Long, isCompleted: Boolean)
} 