package com.swu.myapplication.ui.edit

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.internal.ViewUtils.showKeyboard
import com.swu.myapplication.R
import com.swu.myapplication.data.database.AppDatabase
import com.swu.myapplication.data.model.Note
import com.swu.myapplication.data.repository.NoteRepository
import com.swu.myapplication.databinding.FragmentEditBinding
import com.swu.myapplication.ui.notes.NoteViewModel
import kotlinx.coroutines.launch
import java.util.*

class EditFragment : Fragment() {
    private var _binding: FragmentEditBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: NoteViewModel
    private val args: EditFragmentArgs by navArgs()

    // 编辑状态管理
    private var currentNote: Note? = null
    private var isContentChanged = false
    private val inputMethodManager by lazy {
        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupUI()
        setupTextWatchers()
        loadInitialData()
    }

    private fun setupViewModel() {
        val database = AppDatabase.getDatabase(requireContext())
        val repository = NoteRepository(database.noteDao())
        viewModel = ViewModelProvider(
            this,
            NoteViewModel.Factory(repository)
        )[NoteViewModel::class.java]
    }

    private fun setupUI() {
        with(binding) {
            // 设置焦点和键盘
            etTitle.requestFocus()
            showKeyboard(etTitle)

            // 按钮点击监听
            btnBack.setOnClickListener { handleBackPress() }
            btnSave.setOnClickListener { saveNote() }
        }
    }

    private fun setupTextWatchers() {
        binding.apply {
            etTitle.doAfterTextChanged { isContentChanged = true }
            etContent.doAfterTextChanged { isContentChanged = true }
        }
    }

    private fun loadInitialData() {
        when {
            // 编辑现有笔记
            args.noteId != Note.INVALID_ID -> {
                lifecycleScope.launch {
                    viewModel.getNoteById(args.noteId)?.let { note ->
                        currentNote = note
                        binding.etTitle.setText(note.title)
                        binding.etContent.setText(note.content)
                    } ?: showToast("笔记不存在")
                }
            }
            // 新建笔记时检查笔记本ID有效性
            args.notebookId == Note.INVALID_ID -> {
                showToast("无效的笔记本")
                findNavController().navigateUp()
            }
        }
    }

    private fun saveNote() {
        if (!validateInput()) return

        lifecycleScope.launch {
            try {
                if (currentNote == null) {
                    handleCreateNote()
                } else {
                    handleUpdateNote()
                    currentNote?.id ?: Note.INVALID_ID
                }
                navigateBackWithSuccess()
            } catch (e: Exception) {
                showToast("保存失败: ${e.message}")
            }
        }
    }

    private suspend fun handleCreateNote() {
        val newNote = createNewNote()
        viewModel.insertNote(newNote)
        currentNote = newNote
    }

    private suspend fun handleUpdateNote() {
        currentNote?.let { originalNote ->
            val updatedNote = originalNote.copy(
                title = binding.etTitle.text.toString(),
                content = binding.etContent.text.toString(),
                modifiedTime = System.currentTimeMillis()
            )
            viewModel.updateNote(updatedNote)
        }
    }

    private fun createNewNote(): Note {
        return Note(
            title = binding.etTitle.text.toString().trim().takeIf { it.isNotEmpty() } ?: "无标题",
            content = binding.etContent.text.toString().trim(),
            notebookId = args.notebookId,
            createdTime = System.currentTimeMillis(),
            modifiedTime = System.currentTimeMillis()
        )
    }

    private fun validateInput(): Boolean {
        if (binding.etTitle.text.isNullOrEmpty() && binding.etContent.text.isNullOrEmpty()) {
            showToast("请输入内容")
            return false
        }
        return true
    }

    private fun handleBackPress() {
        if (isContentChanged) {
            showDiscardDialog()
        } else {
            navigateBack()
        }
    }

    private fun showDiscardDialog() {
        // 实现放弃修改对话框
        navigateBack()
    }

    private fun navigateBack() {
        hideKeyboard()
        findNavController().navigateUp()
    }

    private fun navigateBackWithSuccess() {
        hideKeyboard()
        parentFragmentManager.setFragmentResult(REQUEST_KEY_EDIT, Bundle().apply {
            putBoolean(EXTRA_SAVED_SUCCESS, true)
        })
        findNavController().navigateUp()
    }

    private fun hideKeyboard() {
        binding.etTitle.clearFocus()
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val REQUEST_KEY_EDIT = "edit_request"
        const val EXTRA_SAVED_SUCCESS = "saved_success"
    }
}