package com.swu.myapplication.ui.todos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.swu.myapplication.data.model.TodoCategory
import com.swu.myapplication.data.repository.TodoCategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TodoCategoryViewModel(private val repository: TodoCategoryRepository) : ViewModel() {
    
    private val _allCategories = MutableStateFlow<List<TodoCategory>>(emptyList())
    val allCategories: StateFlow<List<TodoCategory>> = _allCategories
    
    init {
        refreshCategories()
    }
    
    fun initDefaultCategories() {
        viewModelScope.launch {
            repository.initDefaultCategories()
            refreshCategories()
        }
    }
    
    fun refreshCategories() {
        viewModelScope.launch {
            _allCategories.value = repository.getAllCategories().first()
        }
    }
    
    fun addCategory(category: TodoCategory) {
        viewModelScope.launch {
            repository.insert(category)
            refreshCategories()
        }
    }
    
    fun updateCategory(category: TodoCategory) {
        viewModelScope.launch {
            repository.update(category)
            refreshCategories()
        }
    }
    
    fun deleteCategory(category: TodoCategory) {
        viewModelScope.launch {
            repository.delete(category)
            refreshCategories()
        }
    }
    
    class Factory(private val repository: TodoCategoryRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TodoCategoryViewModel::class.java)) {
                return TodoCategoryViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
} 