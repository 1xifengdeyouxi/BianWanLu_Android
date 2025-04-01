package com.swu.myapplication.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.swu.myapplication.data.database.AppDatabase
import com.swu.myapplication.data.model.Note
import com.swu.myapplication.data.repository.NoteRepository
import kotlinx.coroutines.launch
import java.util.Date

class NotesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NoteRepository
    val allNotes: LiveData<List<Note>>
    val allNotesByModifiedTime: LiveData<List<Note>>

    init {
        val noteDao = AppDatabase.getDatabase(application).noteDao()
        repository = NoteRepository(noteDao)
        allNotes = repository.allNotes
        allNotesByModifiedTime = repository.allNotesByModifiedTime
    }

    fun insert(title: String, content: String) = viewModelScope.launch {
        val note = Note(
            title = title,
            content = content,
            notebook = "默认笔记本",  // 默认分类
            createdAt = Date().toString(),
            updatedAt = Date().toString()
        )
        repository.insert(note)
    }

    fun update(note: Note) = viewModelScope.launch {
        repository.update(note)
    }

    fun delete(note: Note) = viewModelScope.launch {
        repository.delete(note)
    }

    fun searchNotes(query: String): LiveData<List<Note>> {
        return repository.searchNotes(query)
    }
}