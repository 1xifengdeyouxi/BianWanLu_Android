package com.swu.myapplication.ui.notebook

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.swu.myapplication.R
import com.swu.myapplication.data.database.AppDatabase
import com.swu.myapplication.data.model.Notebook
import com.swu.myapplication.data.repository.NotebookRepository
import com.swu.myapplication.databinding.FragmentNotebookListBinding

class NotebookListFragment : Fragment() {
    private var _binding: FragmentNotebookListBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: NotebookViewModel
    private lateinit var adapter: NotebookAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotebookListBinding.inflate(inflater, container, false)
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
        adapter = NotebookAdapter(
            onNotebookClick = { notebook ->
                // 导航到笔记列表页面
                val action = NotebookListFragmentDirections.actionNotebookListFragmentToNotesFragment(notebook.id)
                findNavController().navigate(action)
            },
            onNotebookEdit = { notebook ->
                // 显示编辑对话框
                showEditNotebookDialog(notebook)
            },
            onNotebookDelete = { notebook ->
                // 显示删除确认对话框
                showDeleteNotebookDialog(notebook)
            }
        )

        binding.rvNotebooks.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@NotebookListFragment.adapter
        }
    }

    private fun setupClickListeners() {
        // 返回按钮
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // 全部笔记卡片
        binding.cardAllNotes.setOnClickListener {
            val action = NotebookListFragmentDirections.actionNotebookListFragmentToNotesFragment(-2)
            findNavController().navigate(action)
        }

        // 新建按钮
        binding.btnNew.setOnClickListener {
            showAddNotebookDialog()
        }

        // 菜单按钮
        binding.btnManagerMenu.setOnClickListener {
            val action = NotebookListFragmentDirections.actionNotebookListFragmentToNotebookManagerMenuFragment()
            findNavController().navigate(action)
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

    private fun showAddNotebookDialog() {
        // 重用NotebookManagerFragment中的对话框逻辑
        val dialog = NotebookDialogHelper.createAddNotebookDialog(
            context = requireContext(),
            onConfirm = { name ->
                // 确保名称不为空
                if (name.isEmpty()) {
                    Toast.makeText(requireContext(), "请输入笔记本名称", Toast.LENGTH_SHORT).show()
                    return@createAddNotebookDialog
                }
                // 检查名称是否已存在
                if (viewModel.allNotebooks.value?.any { it.name == name } == true) {
                    Toast.makeText(requireContext(), "笔记本名称已存在", Toast.LENGTH_SHORT).show()
                    return@createAddNotebookDialog
                }
                viewModel.insertNotebook(name)
            }
        )
        dialog.show()
    }

    private fun showEditNotebookDialog(notebook: Notebook) {
        val dialog = NotebookDialogHelper.createEditNotebookDialog(
            context = requireContext(),
            notebook = notebook,
            onConfirm = { name ->
                viewModel.updateNotebook(notebook.copy(name = name))
            }
        )
        dialog.show()
    }

    private fun showDeleteNotebookDialog(notebook: Notebook) {
        val dialog = NotebookDialogHelper.createDeleteNotebookDialog(
            context = requireContext(),
            notebook = notebook,
            onConfirm = {
                viewModel.deleteNotebook(notebook)
            }
        )
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 