package com.swu.myapplication.ui.todos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.swu.myapplication.R
import com.swu.myapplication.data.database.AppDatabase
import com.swu.myapplication.data.model.Todo
import com.swu.myapplication.data.model.TodoCategory
import com.swu.myapplication.data.repository.TodoCategoryRepository
import com.swu.myapplication.data.repository.TodoRepository
import com.swu.myapplication.databinding.FragmentTodosBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TodosFragment : Fragment() {
    private var _binding: FragmentTodosBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: TodoViewModel
    private lateinit var categoryViewModel: TodoCategoryViewModel
    private lateinit var adapter: TodoAdapter
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModels()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupCategoryChips()
        setupClickListeners()
        setupAppBarListener()
        observeTodos()
        observeCategories()
    }

    private fun setupViewModels() {
        val database = AppDatabase.getDatabase(requireContext())
        val todoRepository = TodoRepository(database.todoDao())
        val categoryRepository = TodoCategoryRepository(database.todoCategoryDao())
        
        // 初始化TodoViewModel
        val todoFactory = TodoViewModel.Factory(todoRepository, categoryRepository)
        viewModel = ViewModelProvider(this, todoFactory)[TodoViewModel::class.java]

        // 初始化TodoCategoryViewModel
        val categoryFactory = TodoCategoryViewModel.Factory(categoryRepository)
        categoryViewModel = ViewModelProvider(this, categoryFactory)[TodoCategoryViewModel::class.java]

        // 初始化默认类别
        categoryViewModel.initDefaultCategories()
    }

    private fun setupRecyclerView() {
        adapter = TodoAdapter(
            onTodoClick = { todo ->
                // 点击待办事项时显示编辑对话框
                showEditTodoDialog(todo)
            },
            onTodoCheckChanged = { todo, isChecked ->
                viewModel.toggleTodoCompletion(todo.id, isChecked)
            }
        )
        
        binding.todosRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@TodosFragment.adapter
        }
    }

    private fun setupCategoryChips() {
        // 在实际实现中，可以添加类别筛选芯片
    }

    private fun setupClickListeners() {
        // 添加新任务
        binding.addTodoFab.setOnClickListener {
            showAddTodoDialog()
        }
        
        // 编辑模式相关按钮
        binding.btnEdit.setOnClickListener {
            toggleEditMode(true)
        }
        
        binding.btnCancel.setOnClickListener {
            toggleEditMode(false)
        }
        
        // 全选/取消全选按钮
        binding.btnComplete.setOnClickListener {
            if (adapter.isAllSelected()) {
                // 如果已经全选，取消全选
                adapter.clearAllSelections()
                binding.btnComplete.text = "全选"
            } else {
                // 如果未全选，执行全选
                adapter.selectAll()
                binding.btnComplete.text = "取消全选"
            }
            // 更新底部按钮状态
            updateButtonsState(adapter.getSelectedTodos().size)
        }
        
        binding.btnDelete.setOnClickListener {
            val selectedTodos = adapter.getSelectedTodos()
            if (selectedTodos.isNotEmpty()) {
                viewModel.deleteTodos(selectedTodos.toList())
                toggleEditMode(false)
            }
        }
        
        // 设置选择变化监听
        adapter.setOnSelectionChangedListener { selectedCount ->
            updateButtonsState(selectedCount)
            // 更新全选按钮文本
            if (adapter.isAllSelected() && selectedCount > 0) {
                binding.btnComplete.text = "取消全选"
            } else {
                binding.btnComplete.text = "全选"
            }
        }
    }

    private fun updateButtonsState(selectedCount: Int) {
        binding.btnDelete.isEnabled = selectedCount > 0
    }

    private fun observeTodos() {
        lifecycleScope.launch {
            viewModel.todos.collect { todos ->
                adapter.submitList(todos)
                updateEmptyView(todos)
                
                // 更新待办数量显示
                binding.tvTodosCount.text = "${todos.size}个待办"
            }
        }
    }

    private fun updateEmptyView(todos: List<Todo>) {
        if (todos.isEmpty()) {
            binding.emptyView.visibility = View.VISIBLE
            binding.todosRecyclerView.visibility = View.GONE
        } else {
            binding.emptyView.visibility = View.GONE
            binding.todosRecyclerView.visibility = View.VISIBLE
        }
    }

    private fun observeCategories() {
        lifecycleScope.launch {
            categoryViewModel.allCategories.collect { categories ->
                // 在实际实现中，可以根据categories更新UI
            }
        }
    }

    private fun toggleEditMode(enabled: Boolean) {
        isEditMode = enabled
        
        // 获取底部导航栏
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        
        // 更新UI
        binding.apply {
            // 标准模式元素
            normalModeToolbar.visibility = if (enabled) View.GONE else View.VISIBLE
            toolbar.visibility = if (enabled) View.GONE else View.VISIBLE
            addTodoFab.visibility = if (enabled) View.GONE else View.VISIBLE
            
            // 编辑模式元素
            editModeToolbar.visibility = if (enabled) View.VISIBLE else View.GONE
            bottomActionBar.visibility = if (enabled) View.VISIBLE else View.GONE
        }
        
        // 隐藏/显示底部导航栏
        bottomNav?.visibility = if (enabled) View.GONE else View.VISIBLE
        
        // 更新适配器
        adapter.setEditMode(enabled)
    }

    /**
     * 显示添加待办事项的对话框
     */
    private fun showAddTodoDialog() {
        AddTodoDialog(requireContext()) { todo ->
            // 保存新增的待办事项
            viewModel.addTodo(todo)
        }.show()
    }

    /**
     * 显示编辑待办事项的对话框
     */
    private fun showEditTodoDialog(todo: Todo) {
        // 创建编辑对话框
        AddTodoDialog(requireContext(), todo) { updatedTodo ->
            // 更新待办事项
            viewModel.updateTodo(updatedTodo)
        }.show()
    }

    /**
     * 设置AppBar滑动监听，处理折叠和展开效果
     */
    private fun setupAppBarListener() {
        binding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val totalScrollRange = appBarLayout.totalScrollRange
            val offsetRatio = (Math.abs(verticalOffset)).toFloat() / totalScrollRange
            
            // 调整标题的可见性
            binding.tvCollapsedTitle.alpha = offsetRatio
            
            // 隐藏展开状态的标题
            val expandedTitleAlpha = 1 - offsetRatio * 2
            binding.tvTodosTitle.alpha = if (expandedTitleAlpha < 0) 0f else expandedTitleAlpha
            binding.tvTodosCount.alpha = if (expandedTitleAlpha < 0) 0f else expandedTitleAlpha
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 