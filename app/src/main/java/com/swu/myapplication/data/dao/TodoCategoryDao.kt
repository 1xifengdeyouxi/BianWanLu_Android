package com.swu.myapplication.data.dao

import androidx.room.*
import com.swu.myapplication.data.model.TodoCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoCategoryDao {
    @Query("SELECT * FROM todo_categories ORDER BY creationTime ASC")
    fun getAllCategories(): Flow<List<TodoCategory>>

    @Query("SELECT * FROM todo_categories WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId: Long): TodoCategory?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: TodoCategory): Long

    @Update
    suspend fun update(category: TodoCategory)

    @Delete
    suspend fun delete(category: TodoCategory)

    @Query("UPDATE todo_categories SET todoCount = todoCount + 1 WHERE id = :categoryId")
    suspend fun incrementTodoCount(categoryId: Long)

    @Query("UPDATE todo_categories SET todoCount = todoCount - 1 WHERE id = :categoryId AND todoCount > 0")
    suspend fun decrementTodoCount(categoryId: Long)
} 