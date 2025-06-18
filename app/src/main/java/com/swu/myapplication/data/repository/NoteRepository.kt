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

    // 更新笔记排序
    suspend fun updateNoteSortOrder(noteId: Long, sortOrder: Int) {
        noteDao.updateNoteSortOrder(noteId, sortOrder)
    }

    // 获取最大排序值
    suspend fun getMaxSortOrder(notebookId: Long): Int {
        return noteDao.getMaxSortOrder(notebookId) ?: 0
    }

    // 置顶笔记（设置为最小排序值）
    suspend fun pinNote(note: Note) {
        // 将其他笔记的排序值增加1
        noteDao.incrementSortOrderFrom(note.notebookId, 0)
        // 将当前笔记设置为0（置顶）
        noteDao.updateNoteSortOrder(note.id, 0)
    }

    // 移动笔记位置
    suspend fun moveNote(fromPosition: Int, toPosition: Int, notes: List<Note>) {
        // 更新所有受影响笔记的排序值
        notes.forEachIndexed { index, note ->
            noteDao.updateNoteSortOrder(note.id, index)
        }
    }

    // 获取笔记本中的笔记数量
    suspend fun getNoteCountInNotebook(notebookId: Long): Int {
        return noteDao.getNoteCountInNotebook(notebookId)
    }
} 