package com.swu.myapplication.ui.todos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.swu.myapplication.data.model.Todo
import com.swu.myapplication.data.repository.TodoCategoryRepository
import com.swu.myapplication.data.repository.TodoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TodoViewModel(
    private val todoRepository: TodoRepository,
    private val categoryRepository: TodoCategoryRepository
) : ViewModel() {
    
    // 当前选择的类别ID
    private val _currentCategoryId = MutableStateFlow<Long>(-1L) // -1 表示全部待办
    val currentCategoryId: StateFlow<Long> = _currentCategoryId
    
    // 待办列表
    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos: StateFlow<List<Todo>> = _todos
    
    // 带有提醒的待办
    private val _todosWithReminders = MutableStateFlow<List<Todo>>(emptyList())
    val todosWithReminders: StateFlow<List<Todo>> = _todosWithReminders
    
    init {
        refreshTodos()
        refreshTodosWithReminders()
    }
    
    fun setCurrentCategory(categoryId: Long) {
        if (_currentCategoryId.value != categoryId) {
            _currentCategoryId.value = categoryId
            refreshTodos()
        }
    }
    
    fun refreshTodos() {
        viewModelScope.launch {
            _todos.value = if (_currentCategoryId.value == -1L) {
                todoRepository.getAllTodos().first()
            } else {
                todoRepository.getTodosByCategory(_currentCategoryId.value).first()
            }
        }
    }
    
    fun refreshTodosWithReminders() {
        viewModelScope.launch {
            _todosWithReminders.value = todoRepository.getTodosWithReminders().first()
        }
    }
    
    fun toggleTodoCompletion(todoId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            todoRepository.updateCompletionStatus(todoId, isCompleted)
            refreshTodos()
        }
    }
    
    fun addTodo(todo: Todo) {
        viewModelScope.launch {
            val id = todoRepository.insert(todo)
            if (todo.categoryId != -1L) {
                categoryRepository.incrementTodoCount(todo.categoryId)
            }
            refreshTodos()
        }
    }
    
    /**
     * 更新待办事项
     */
    fun updateTodo(todo: Todo) = viewModelScope.launch {
        todoRepository.updateTodo(todo)
        refreshTodos()
        refreshTodosWithReminders()
    }
    
    fun deleteTodo(todo: Todo) {
        viewModelScope.launch {
            todoRepository.delete(todo)
            if (todo.categoryId != -1L) {
                categoryRepository.decrementTodoCount(todo.categoryId)
            }
            refreshTodos()
            refreshTodosWithReminders()
        }
    }
    
    fun deleteTodos(todos: List<Todo>) {
        viewModelScope.launch {
            val todoIds = todos.map { it.id }
            todoRepository.deleteTodos(todoIds)
            
            // 更新各个类别的计数
            todos.groupBy { it.categoryId }
                .forEach { (categoryId, todosInCategory) ->
                    if (categoryId != -1L) {
                        repeat(todosInCategory.size) {
                            categoryRepository.decrementTodoCount(categoryId)
                        }
                    }
                }
            
            refreshTodos()
            refreshTodosWithReminders()
        }
    }
    
    class Factory(
        private val todoRepository: TodoRepository,
        private val categoryRepository: TodoCategoryRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
                return TodoViewModel(todoRepository, categoryRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
} 