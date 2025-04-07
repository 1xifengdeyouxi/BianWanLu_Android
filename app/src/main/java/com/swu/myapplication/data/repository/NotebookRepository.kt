package com.swu.myapplication.data.repository

import com.swu.myapplication.data.dao.NotebookDao
import com.swu.myapplication.data.model.Notebook
import kotlinx.coroutines.flow.Flow

class NotebookRepository(private val notebookDao: NotebookDao) {
    
    // 获取所有笔记本，按修改时间降序排序
    val allNotebooks: Flow<List<Notebook>> = notebookDao.getAllNotebooks()

    // 插入笔记本
    suspend fun insert(notebook: Notebook): Long {
        return notebookDao.insert(notebook)
    }

    // 更新笔记本
    suspend fun update(notebook: Notebook) {
        notebookDao.update(notebook)
    }

    // 删除笔记本
    suspend fun delete(notebook: Notebook) {
        notebookDao.delete(notebook)
    }

    // 根据ID获取笔记本
    suspend fun getNotebookById(id: Long): Notebook? {
        return notebookDao.getNotebookById(id)
    }

    // 增加笔记本中的笔记数量
    suspend fun incrementNoteCount(notebookId: Long) {
        notebookDao.incrementNoteCount(notebookId)
    }

    // 减少笔记本中的笔记数量
    suspend fun decrementNoteCount(notebookId: Long) {
        notebookDao.decrementNoteCount(notebookId)
    }

    // 获取笔记本总数
    suspend fun getNotebookCount(): Int {
        return notebookDao.getNotebookCount()
    }

    // 更新笔记本的笔记数量
    suspend fun updateNoteCount(notebookId: Long, count: Int) {
        val notebook = notebookDao.getNotebookById(notebookId)
        notebook?.let {
            notebookDao.update(it.copy(noteCount = count))
        }
    }
} 