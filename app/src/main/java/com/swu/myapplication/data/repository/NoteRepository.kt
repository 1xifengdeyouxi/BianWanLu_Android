package com.swu.myapplication.data.repository

import com.swu.myapplication.data.dao.NoteDao
import com.swu.myapplication.data.model.Note
import kotlinx.coroutines.flow.Flow

class NoteRepository(
    private val noteDao: NoteDao,
) {
    // 获取所有笔记
    fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes()

    // 获取特定笔记本的笔记
    fun getNotesByNotebookId(notebookId: Long): Flow<List<Note>> = noteDao.getNotesByNotebookId(notebookId)

    // 插入笔记
    suspend fun insert(note: Note): Long {
        return noteDao.insert(note)
    }

    // 更新笔记
    suspend fun update(note: Note, oldNotebookId: Long? = null) {

        noteDao.update(note)
    }

    // 删除笔记
    suspend fun delete(note: Note) {
        noteDao.delete(note)
    }

    // 根据ID获取笔记
    suspend fun getNoteById(id: Long): Note? {
        return noteDao.getNoteById(id)
    }

    // 搜索笔记
    fun searchNotes(query: String): Flow<List<Note>> {
        return noteDao.searchNotes("%$query%")
    }

    // 获取笔记本中的笔记数量
    suspend fun getNoteCountInNotebook(notebookId: Long): Int {
        return noteDao.getNoteCountInNotebook(notebookId)
    }
} 