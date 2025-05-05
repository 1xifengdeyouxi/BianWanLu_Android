package com.swu.myapplication.ui.notebook

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.swu.myapplication.data.database.AppDatabase
import com.swu.myapplication.data.model.Notebook
import com.swu.myapplication.data.repository.NotebookRepository
import com.swu.myapplication.databinding.FragmentNotebookManagerMenuBinding

class NotebookManagerMenuFragment : Fragment() {
    private var _binding: FragmentNotebookManagerMenuBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: NotebookViewModel
    private lateinit var adapter: NotebookManagerAdapter
    private val selectedNotebooks = mutableSetOf<Notebook>()
    private var currentDialog: androidx.appcompat.app.AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotebookManagerMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupRecyclerView()
        setupClickListeners()
        observeNotebooks()
    }

    private fun setupViewModel() {
        val database = AppDatabase.getDatabase(requireContext())
        val repository = NotebookRepository(database.notebookDao())
        val factory = NotebookViewModel.Factory(repository)
        viewModel = ViewModelProvider(this, factory)[NotebookViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = NotebookManagerAdapter(
            onNotebookChecked = { notebook, isChecked ->
                if (isChecked) {
                    selectedNotebooks.add(notebook)
                } else {
                    selectedNotebooks.remove(notebook)
                }
                updateUI()
            }
        )

        binding.rvNotebooks.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@NotebookManagerMenuFragment.adapter
        }
    }

    private fun setupClickListeners() {
        binding.btnFinish.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnEdit.setOnClickListener {
            if (selectedNotebooks.size == 1) {
                // 显示编辑对话框
                showEditNotebookDialog(selectedNotebooks.first())
            }
        }

        binding.btnDelete.setOnClickListener {
            if (selectedNotebooks.isNotEmpty()) {
                // 显示删除确认对话框
                showDeleteNotebooksDialog(selectedNotebooks.toList())
            }
        }
    }

    private fun observeNotebooks() {
        viewModel.allNotebooks.observe(viewLifecycleOwner) { notebooks ->
            // 过滤掉特殊笔记本
            val filtered = notebooks.filterNot { it.id == Notebook.ALL_NOTEBOOK_ID }
            adapter.submitList(filtered)

            // 更新全部笔记数量
            val totalNotes = notebooks.sumOf { it.noteCount }
            binding.tvAllNotesCount.text = totalNotes.toString()
        }
    }

    private fun updateUI() {
        // 更新标题
        binding.tvTitle.text = when (selectedNotebooks.size) {
            0 -> "笔记本管理"
            else -> "已选择${selectedNotebooks.size}项"
        }

        // 更新按钮状态
        binding.btnEdit.apply {
            isEnabled = selectedNotebooks.size == 1
            alpha = if (isEnabled) 1f else 0.5f
        }

        binding.btnDelete.apply {
            isEnabled = selectedNotebooks.isNotEmpty()
            alpha = if (isEnabled) 1f else 0.5f
        }
    }

    private fun showEditNotebookDialog(notebook: Notebook) {
        // 检查是否已有对话框显示
        if (currentDialog?.isShowing == true) {
            return
        }
        
        val dialog = NotebookDialogHelper.createEditNotebookDialog(
            context = requireContext(),
            notebook = notebook,
            onConfirm = { name ->
                viewModel.updateNotebook(notebook.copy(name = name))
                selectedNotebooks.clear()
                updateUI()
                currentDialog = null
            }
        )
        
        // 设置对话框消失监听
        dialog.setOnDismissListener {
            currentDialog = null
        }
        
        currentDialog = dialog
        dialog.show()
    }

    private fun showDeleteNotebooksDialog(notebooks: List<Notebook>) {
        // 检查是否已有对话框显示
        if (currentDialog?.isShowing == true) {
            return
        }
        
        val message = if (notebooks.size == 1) {
            "确定要删除笔记本${notebooks[0].name}吗？"
        } else {
            "确定要删除选中的${notebooks.size}个笔记本吗？"
        }

        val dialog = NotebookDialogHelper.createDeleteNotebookDialog(
            context = requireContext(),
            notebook = notebooks[0], // 这里传入第一个笔记本只是为了复用对话框
            onConfirm = {
                notebooks.forEach { notebook ->
                    viewModel.deleteNotebook(notebook)
                }
                selectedNotebooks.clear()
                updateUI()
                currentDialog = null
            }
        )
        
        // 设置对话框消失监听
        dialog.setOnDismissListener {
            currentDialog = null
        }
        
        currentDialog = dialog
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 