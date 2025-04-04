package com.swu.myapplication.data.repository

import com.swu.myapplication.data.dao.NoteDao
import com.swu.myapplication.data.model.Note
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {
    val allNotes: Flow<List<Note>> = noteDao.getAllNotes()

    fun getNotesByNotebook(notebookId: Long): Flow<List<Note>> {
        return noteDao.getNotesByNotebookId(notebookId)
    }

    suspend fun insert(note: Note): Long {
        return noteDao.insert(note)
    }

    suspend fun update(note: Note) {
        noteDao.update(note)
    }

    suspend fun delete(note: Note) {
        noteDao.delete(note)
    }

    suspend fun getNoteCountByNotebook(notebookId: Long): Int {
        return noteDao.getNoteCountInNotebook(notebookId)
    }

    fun getTodoNotes(): Flow<List<Note>> = noteDao.getTodoNotes()

    fun getNotesByNotebookId(notebookId: Long): Flow<List<Note>> = noteDao.getNotesByNotebookId(notebookId)

    suspend fun getNoteById(id: Long): Note? {
        return noteDao.getNoteById(id)
    }

    fun searchNotes(query: String): Flow<List<Note>> {
        return noteDao.searchNotes("%$query%")
    }
} 