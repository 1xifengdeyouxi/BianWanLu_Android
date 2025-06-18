package com.swu.myapplication.ui.notes

import androidx.lifecycle.*
import com.swu.myapplication.data.model.Note
import com.swu.myapplication.data.model.Notebook
import com.swu.myapplication.data.repository.NoteRepository
import com.swu.myapplication.data.repository.NotebookRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NoteViewModel(
    private val repository: NoteRepository,
    private val notebookRepository: NotebookRepository
) : ViewModel() {

    // 当前选中的笔记本ID
    private val _currentNotebookId = MutableStateFlow<Long>(-2) // -2表示全部笔记
    val currentNotebookId: StateFlow<Long> = _currentNotebookId.asStateFlow()

    // 笔记列表
    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    // 排序相关
    private var currentSortType = NotesSortPopupWindow.SortType.MODIFY_TIME

    // 搜索相关
    private val _searchResults = MutableStateFlow<List<Note>>(emptyList())
    val searchResults: StateFlow<List<Note>> = _searchResults

    init {
        viewModelScope.launch {
            currentNotebookId.collect { notebookId ->
                refreshNotes()
            }
        }
    }

    // 设置当前笔记本
    fun setCurrentNotebook(notebookId: Long) {
        if (_currentNotebookId.value != notebookId) {
            _currentNotebookId.value = notebookId
            // 在设置ID后立即刷新笔记列表
            refreshNotes()
        }
    }

    // 插入笔记
    fun insertNote(note: Note) = viewModelScope.launch {
        repository.insert(note)
        updateNoteCount(note.notebookId)
        refreshNotes()
    }

    // 更新笔记
    fun updateNote(note: Note) = viewModelScope.launch {
        repository.update(note)
        refreshNotes()
    }

    // 删除笔记
    fun deleteNote(note: Note) = viewModelScope.launch {
        repository.delete(note)
        updateNoteCount(note.notebookId)
        refreshNotes()
    }

    // 移动笔记到其他笔记本
    fun moveNoteToNotebook(note: Note, targetNotebookId: Long) = viewModelScope.launch {
        val oldNotebookId = note.notebookId
        repository.update(note.copy(notebookId = targetNotebookId))
        updateNoteCount(oldNotebookId)
        updateNoteCount(targetNotebookId)
        refreshNotes()
    }

    // 搜索笔记
    fun searchNotes(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                _searchResults.value = emptyList()
                return@launch
            }
            
            val allNotes = repository.getAllNotes().first()
            val filteredNotes = allNotes.filter { note ->
                note.title.contains(query, ignoreCase = true) || 
                note.content.contains(query, ignoreCase = true)
            }
            _searchResults.value = filteredNotes
        }
    }

    // 根据ID获取笔记
    suspend fun getNoteById(id: Long): Note? {
        return repository.getNoteById(id)
    }

    // 更新笔记本的笔记数量
    private suspend fun updateNoteCount(notebookId: Long) {
        val count = repository.getNoteCountInNotebook(notebookId)
        notebookRepository.updateNoteCount(notebookId, count)
    }

    // 设置排序类型
    fun setSortType(sortType: NotesSortPopupWindow.SortType) {
        if (currentSortType != sortType) {
            currentSortType = sortType
            refreshNotes()
        }
    }

    //初始化笔记本里面的笔记
    fun initDefaultNotes()  {

    }
    //获取所有笔记本
    fun getAllNotes() = repository.getAllNotes()

    // 刷新笔记列表
    fun refreshNotes() {
        viewModelScope.launch {
            val notesList = when (currentNotebookId.value) {
                -2L -> repository.getAllNotes().first()
                else -> repository.getNotesByNotebookId(currentNotebookId.value).first()
            }
            sortAndUpdateNotes(notesList)
        }
    }

    // 排序并更新笔记列表
    private fun sortAndUpdateNotes(notes: List<Note>) {
        val sortedNotes = when (currentSortType) {
            NotesSortPopupWindow.SortType.CREATE_TIME -> {
                notes.sortedByDescending { it.createdTime }
            }
            NotesSortPopupWindow.SortType.MODIFY_TIME -> {
                notes.sortedByDescending { it.modifiedTime }
            }

            NotesSortPopupWindow.SortType.EDIT -> {
                // 编辑模式下使用修改时间排序
                notes.sortedByDescending { it.modifiedTime }
            }
        }
        _notes.value = sortedNotes
    }

    fun deleteNotes(notes: List<Note>) = viewModelScope.launch {
        notes.forEach { note ->
            repository.delete(note)
            updateNoteCount(note.notebookId)
        }
        refreshNotes()
    }

    fun clearSearch() {
        _searchResults.value = emptyList()
    }

    // 置顶笔记
    fun pinNote(note: Note) = viewModelScope.launch {
        repository.pinNote(note)
        refreshNotes()
    }

    // 移动笔记位置（拖拽排序）
    fun moveNote(fromPosition: Int, toPosition: Int) = viewModelScope.launch {
        val currentNotes = _notes.value.toMutableList()
        if (fromPosition < currentNotes.size && toPosition < currentNotes.size) {
            val movedNote = currentNotes.removeAt(fromPosition)
            currentNotes.add(toPosition, movedNote)

            // 更新UI
            _notes.value = currentNotes

            // 更新数据库中的排序
            repository.moveNote(fromPosition, toPosition, currentNotes)
        }
    }

    class Factory(
        private val repository: NoteRepository,
        private val notebookRepository: NotebookRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return NoteViewModel(repository, notebookRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
} 