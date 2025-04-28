package com.swu.myapplication.data.repository

import com.swu.myapplication.data.dao.TodoDao
import com.swu.myapplication.data.model.Todo
import kotlinx.coroutines.flow.Flow

class TodoRepository(private val todoDao: TodoDao) {
    fun getAllTodos(): Flow<List<Todo>> = todoDao.getAllTodos()
    
    fun getTodosByCategory(categoryId: Long): Flow<List<Todo>> = 
        todoDao.getTodosByCategory(categoryId)
    
    fun getTodosWithReminders(): Flow<List<Todo>> = todoDao.getTodosWithReminders()
    
    suspend fun getTodoById(id: Long): Todo? = todoDao.getTodoById(id)
    
    suspend fun insert(todo: Todo): Long = todoDao.insert(todo)
    
    suspend fun update(todo: Todo) = todoDao.update(todo)
    
    suspend fun delete(todo: Todo) = todoDao.delete(todo)
    
    suspend fun deleteTodos(todoIds: List<Long>) = todoDao.deleteTodos(todoIds)
    
    suspend fun updateCompletionStatus(todoId: Long, isCompleted: Boolean) =
        todoDao.updateCompletionStatus(todoId, isCompleted)

    /**
     * 更新待办事项
     */
    suspend fun updateTodo(todo: Todo) {
        todoDao.update(todo)
    }
} 