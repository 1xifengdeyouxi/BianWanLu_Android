package com.swu.myapplication.ui.edit

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.swu.myapplication.R
import com.swu.myapplication.data.database.AppDatabase
import com.swu.myapplication.data.model.Note
import com.swu.myapplication.data.repository.NoteRepository
import com.swu.myapplication.ui.notes.NoteViewModel

class EditFragment : Fragment() {
    private lateinit var etTitle: EditText
    private lateinit var etContent: EditText
    private lateinit var btnBack: ImageButton
    private lateinit var btnSave: Button
    private lateinit var viewModel: NoteViewModel
    private val args: EditFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setupViewModel()
        setupClickListeners()
        
        // 自动聚焦到标题并显示键盘
        etTitle.requestFocus()
        showKeyboard(etTitle)

        Log.d("EditFragment", "Received notebookId: ${args.notebookId}")
    }

    private fun initViews(view: View) {
        etTitle = view.findViewById(R.id.etTitle)
        etContent = view.findViewById(R.id.etContent)
        btnBack = view.findViewById(R.id.btnBack)
        btnSave = view.findViewById(R.id.btnSave)
    }

    private fun setupViewModel() {
        val database = AppDatabase.getDatabase(requireContext())
        val repository = NoteRepository(database.noteDao())
        val factory = NoteViewModel.Factory(repository)
        viewModel = ViewModelProvider(this, factory)[NoteViewModel::class.java]
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            hideKeyboard()
            findNavController().navigateUp()
        }

        btnSave.setOnClickListener {
            saveNote()
        }
    }

    private fun saveNote() {
        val title = etTitle.text.toString().trim()
        val content = etContent.text.toString().trim()

        // 隐藏键盘
        hideKeyboard()

        when {
            title.isEmpty() && content.isEmpty() -> {
                // 标题和内容都为空，提示用户
                Toast.makeText(requireContext(), "请输入内容", Toast.LENGTH_SHORT).show()
                return
            }
            title.isEmpty() -> {
                // 标题为空但内容不为空，使用默认标题
                createAndSaveNote("默认笔记", content)
                Toast.makeText(requireContext(), "保存成功", Toast.LENGTH_SHORT).show()
            }
            else -> {
                // 标题不为空，直接保存
                createAndSaveNote(title, content)
                Toast.makeText(requireContext(), "保存成功", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createAndSaveNote(title: String, content: String) {
        val note = Note(
            title = title,
            content = content,
            createdAt = System.currentTimeMillis().toString(),
            updatedAt = System.currentTimeMillis().toString(),
            notebookId = args.notebookId  // 使用传入的笔记本ID
        )
        viewModel.insert(note)
        findNavController().navigateUp()
    }

    private fun showKeyboard(view: View) {
        view.post {
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocus = activity?.currentFocus
        if (currentFocus != null) {
            imm.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }

    override fun onPause() {
        super.onPause()
        hideKeyboard()
    }
} 