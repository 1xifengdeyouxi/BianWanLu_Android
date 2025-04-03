package com.swu.myapplication.data.repository

import androidx.lifecycle.LiveData
import com.swu.myapplication.data.dao.NoteDao
import com.swu.myapplication.data.model.Note
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {
    val allNotes: Flow<List<Note>> = noteDao.getAllNotes()

    fun getNotesByNotebook(notebookId: Long): Flow<List<Note>> {
        return noteDao.getNotesByNotebook(notebookId)
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

    suspend fun getNoteCount(): Int {
        return noteDao.getNoteCount()
    }

    suspend fun getNoteCountByNotebook(notebookId: Long): Int {
        return noteDao.getNoteCountByNotebook(notebookId)
    }

//    fun searchNotes(query: String): LiveData<List<Note>> {
//        return noteDao.searchNotes(query)
//    }
} 