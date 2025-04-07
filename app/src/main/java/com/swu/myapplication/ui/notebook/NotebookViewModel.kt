package com.swu.myapplication.ui.notebook

import androidx.lifecycle.*
import com.swu.myapplication.data.model.Notebook
import com.swu.myapplication.data.repository.NotebookRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class NotebookViewModel(private val repository: NotebookRepository) : ViewModel() {

    // 获取所有笔记本，但过滤掉默认笔记本（全部笔记）
    val allNotebooks = repository.allNotebooks.asLiveData()

    // 初始化默认笔记本
    fun initDefaultNotebooks() = viewModelScope.launch {
        if (repository.getNotebookCount() == 0) {
            // 创建默认笔记本（全部笔记），但在UI中不显示
            repository.insert(Notebook(
                id = DEFAULT_NOTEBOOK_ID,
                name = "全部笔记"
            ))
        }
    }

    //根据id获取笔记本
    fun getNotebookById(id: Long) = viewModelScope.launch {
        repository.getNotebookById(id)
    }

    fun insertNotebook(name: String) = viewModelScope.launch {
        repository.insert(Notebook(name = name))
    }

    fun updateNotebook(notebook: Notebook) = viewModelScope.launch {
        // 确保不能修改默认笔记本
        if (notebook.id != DEFAULT_NOTEBOOK_ID) {
            repository.update(notebook)
        }
    }

    fun deleteNotebook(notebook: Notebook) = viewModelScope.launch {
        // 确保不能删除默认笔记本
        if (notebook.id != DEFAULT_NOTEBOOK_ID) {
            repository.delete(notebook)
        }
    }

    //增加笔记本中的笔记数量
    fun incrementNoteCount(notebookId: Long) = viewModelScope.launch {
        repository.incrementNoteCount(notebookId)
    }

    // 减少笔记本中的笔记数量
    fun decrementNoteCount(notebookId: Long) = viewModelScope.launch {
        repository.decrementNoteCount(notebookId)
    }

    class Factory(private val repository: NotebookRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NotebookViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return NotebookViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    companion object {
        const val DEFAULT_NOTEBOOK_ID = -2L // 默认笔记本的ID
    }
} 