package com.swu.myapplication.ui.edit

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import com.swu.myapplication.data.database.AppDatabase
import com.swu.myapplication.data.model.Note
import com.swu.myapplication.data.repository.NoteRepository
import com.swu.myapplication.data.repository.NotebookRepository
import com.swu.myapplication.databinding.FragmentEditBinding
import com.swu.myapplication.ui.notebook.NotebookChipHelper
import com.swu.myapplication.ui.notebook.NotebookViewModel
import com.swu.myapplication.ui.notes.NoteViewModel
import kotlinx.coroutines.launch

class EditFragment : Fragment() {
    private var _binding: FragmentEditBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: NoteViewModel
    private lateinit var notebookViewModel: NotebookViewModel
    private val args: EditFragmentArgs by navArgs()

    private var isSaved: Boolean = false

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

        Log.d("EditFragment", "传入notebook的id ${args.notebookId}  ${args.noteId}")
    }

    private fun setupViewModel() {
        val database = AppDatabase.getDatabase(requireContext())
        val noteRepository = NoteRepository(database.noteDao())
        val notebookRepository = NotebookRepository(database.notebookDao())
        val factory = NoteViewModel.Factory(noteRepository, notebookRepository)
        viewModel = ViewModelProvider(this, factory)[NoteViewModel::class.java]
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
            }
        }
    }

    private fun saveNote() {
        val title = binding.etTitle.text.toString().trim()
        val content = binding.etContent.text.toString().trim()

        if (title.isEmpty() && content.isEmpty()) {
            hideKeyboard()
            showToast("请输入内容")
            return
        }

        lifecycleScope.launch {
            if (args.noteId == Note.INVALID_ID) {
                // 新建笔记的情况
                if (!isSaved) {
                    // 第一次保存，创建新笔记
                    val newNote = Note(
                        id = 0, // 让Room自动生成ID
                        title = title.ifEmpty { "无标题" },
                        content = content,
                        notebookId = args.notebookId,
                        createdTime = System.currentTimeMillis(),
                        modifiedTime = System.currentTimeMillis()
                    )
                    viewModel.insertNote(newNote)
                    currentNote = newNote
                    isSaved = true
                    showToast("创建笔记成功")
                } else {
                    // 已经保存过，更新最近创建的笔记
                    currentNote?.let { note ->
                        val updatedNote = note.copy(
                            title = title.ifEmpty { "无标题" },
                            content = content,
                            modifiedTime = System.currentTimeMillis()
                        )
                        viewModel.updateNote(updatedNote)
                        showToast("保存成功")
                    }
                }
            } else {
                // 编辑已有笔记的情况
                if (isContentChanged) {
                    currentNote?.let { note ->
                        val updatedNote = note.copy(
                            title = title.ifEmpty { "无标题" },
                            content = content,
                            modifiedTime = System.currentTimeMillis()
                        )
                        viewModel.updateNote(updatedNote)
                        showToast("保存成功")
                    }
                } else {
                    showToast("内容未发生修改，无需保存")
                }
            }
        }
        hideKeyboard()
    }

    private fun handleBackPress() {
        lifecycleScope.launch {
            if (isContentChanged) {
                if (args.noteId == Note.INVALID_ID) {
                    // 新建笔记的情况
                    if (!isSaved) {
                        // 如果从未保存过且内容有变化，创建新笔记
                        saveNote()
                    }
                } else {
                    // 编辑已有笔记的情况
                    currentNote?.let { note ->
                        val updatedNote = note.copy(
                            title = binding.etTitle.text.toString().trim().ifEmpty { "无标题" },
                            content = binding.etContent.text.toString().trim(),
                            modifiedTime = System.currentTimeMillis()
                        )
                        viewModel.updateNote(updatedNote)
                        showToast("保存成功")
                    }
                }
            }
            hideKeyboard()
            findNavController().navigateUp()
        }
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
}