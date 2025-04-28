package com.swu.myapplication.data.repository

import com.swu.myapplication.data.dao.TodoCategoryDao
import com.swu.myapplication.data.model.TodoCategory
import kotlinx.coroutines.flow.Flow

class TodoCategoryRepository(private val todoCategoryDao: TodoCategoryDao) {
    fun getAllCategories(): Flow<List<TodoCategory>> = todoCategoryDao.getAllCategories()
    
    suspend fun getCategoryById(categoryId: Long): TodoCategory? = 
        todoCategoryDao.getCategoryById(categoryId)
    
    suspend fun insert(category: TodoCategory): Long = todoCategoryDao.insert(category)
    
    suspend fun update(category: TodoCategory) = todoCategoryDao.update(category)
    
    suspend fun delete(category: TodoCategory) = todoCategoryDao.delete(category)
    
    suspend fun incrementTodoCount(categoryId: Long) = 
        todoCategoryDao.incrementTodoCount(categoryId)
    
    suspend fun decrementTodoCount(categoryId: Long) = 
        todoCategoryDao.decrementTodoCount(categoryId)
    
    suspend fun initDefaultCategories() {
        val categoriesCount = todoCategoryDao.getAllCategories().hashCode()
        if (categoriesCount <= 0) {
            // 添加默认类别
            todoCategoryDao.insert(TodoCategory(name = "工作", color = 0xFF4285F4.toInt()))
            todoCategoryDao.insert(TodoCategory(name = "个人", color = 0xFF0F9D58.toInt()))
            todoCategoryDao.insert(TodoCategory(name = "购物", color = 0xFFF4B400.toInt()))
            todoCategoryDao.insert(TodoCategory(name = "健康", color = 0xFFDB4437.toInt()))
        }
    }
} 