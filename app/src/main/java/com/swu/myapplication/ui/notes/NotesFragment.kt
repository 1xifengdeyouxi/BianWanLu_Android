package com.swu.myapplication.ui.notes

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.swu.myapplication.data.database.AppDatabase
import com.swu.myapplication.data.model.Notebook
import com.swu.myapplication.data.repository.NoteRepository
import com.swu.myapplication.data.repository.NotebookRepository
import com.swu.myapplication.databinding.FragmentNotesBinding
import com.swu.myapplication.ui.notebook.NotebookChipHelper
import com.swu.myapplication.ui.notebook.NotebookViewModel
import com.swu.myapplication.ui.search.SearchManager
import kotlinx.coroutines.launch
import kotlin.math.abs
import androidx.activity.addCallback
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.swu.myapplication.R

class NotesFragment : Fragment() {
    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: NoteViewModel
    private lateinit var notebookViewModel: NotebookViewModel
    private lateinit var adapter: NoteAdapter
    private val args: NotesFragmentArgs by navArgs()
    private lateinit var sortPopupWindow: NotesSortPopupWindow
    private var isEditMode = false
    private lateinit var searchManager: SearchManager

    // 将currentNotebookId改为ViewModel中的状态
    private val currentNotebookId: Long
        get() = viewModel.currentNotebookId.value

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()

        // 处理笔记本ID的恢复
        handleNotebookSelection()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupClickListeners()
        setupAppBarListener()
        // 初始化排序和编辑PopupWindow
        setupSortPopupWindow()
        
        // 初始化搜索管理器
        setupSearchManager()

        // 观察数据变化
        observeNotes()
        observeNotebooks()
    }

    private fun handleNotebookSelection() {
        lifecycleScope.launch {
            // 如果从其他Fragment传入了特定的笔记本ID
            if (args.selectedNotebookId != -1L) {
                viewModel.setCurrentNotebook(args.selectedNotebookId)
            }
            // 否则保持当前选中的笔记本
        }
    }

    private fun setupViewModel() {
        val database = AppDatabase.getDatabase(requireContext())
        val noteRepository = NoteRepository(database.noteDao())
        val notebookRepository = NotebookRepository(database.notebookDao())
        
        // 初始化NoteViewModel
        val noteFactory = NoteViewModel.Factory(noteRepository, notebookRepository)
        viewModel = ViewModelProvider(this, noteFactory)[NoteViewModel::class.java]

        // 初始化NotebookViewModel
        val notebookFactory = NotebookViewModel.Factory(notebookRepository)
        notebookViewModel = ViewModelProvider(this, notebookFactory)[NotebookViewModel::class.java]

        // 初始化笔记本
        notebookViewModel.initDefaultNotebooks()
    }

    private fun setupRecyclerView() {
        adapter = NoteAdapter { note ->
            val action = NotesFragmentDirections.actionNotesFragmentToEditFragment(
                notebookId = note.notebookId,
                noteId = note.id
            )
            findNavController().navigate(action)
        }
        
        binding.notesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@NotesFragment.adapter
        }
    }

    private fun setupSortPopupWindow() {
        sortPopupWindow = NotesSortPopupWindow(requireContext())
        sortPopupWindow.setOnSortTypeChangedListener { sortType ->
            when (sortType) {
                NotesSortPopupWindow.SortType.CREATE_TIME -> {
                    viewModel.setSortType(NotesSortPopupWindow.SortType.CREATE_TIME)
                }
                NotesSortPopupWindow.SortType.MODIFY_TIME -> {
                    viewModel.setSortType(NotesSortPopupWindow.SortType.MODIFY_TIME)
                }
                NotesSortPopupWindow.SortType.EDIT -> {
                    toggleEditMode(true)
                }
            }
        }

        binding.btnSort.setOnClickListener { view ->
            if (sortPopupWindow.isShowing) {
                sortPopupWindow.dismiss()
            } else {
                // 计算弹窗显示位置
                val location = IntArray(2)
                view.getLocationOnScreen(location)
                val x = location[0] - (sortPopupWindow.width - view.width) / 2
                val y = location[1] + view.height

                // 显示弹窗
                sortPopupWindow.showAtLocation(
                    binding.root,
                    Gravity.NO_GRAVITY,
                    x,
                    y
                )
            }
        }
    }

    private fun setupClickListeners() {
        binding.addNoteFab.setOnClickListener {
            val action = NotesFragmentDirections.actionNotesFragmentToEditFragment(
                notebookId = currentNotebookId,
                noteId = -1L
            )
            findNavController().navigate(action)
        }

        binding.btnNotebookManager.setOnClickListener {
            findNavController().navigate(NotesFragmentDirections.actionNotesFragmentToNotebookListFragment())
        }

        // 编辑模式按钮点击事件
        binding.btnCancel.setOnClickListener {
            toggleEditMode(false)
        }

        binding.btnComplete.setOnClickListener {
            toggleEditMode(false)
        }

        binding.btnDelete.setOnClickListener {
            val selectedNotes = adapter.getSelectedNotes()
            if (selectedNotes.isNotEmpty()) {
                // 删除选中的笔记
                viewModel.deleteNotes(selectedNotes.toList())
                toggleEditMode(false)
            }
        }

        // 设置选择变化监听
        adapter.setOnSelectionChangedListener { selectedCount ->
            updateBottomButtons()
        }
    }

    private fun setupAppBarListener() {
        var isToolbarShown = false
        
        // 监听AppBar的偏移变化
        binding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val maxScroll = appBarLayout.totalScrollRange
            val percentage = abs(verticalOffset).toFloat() / maxScroll.toFloat()
            
            // 根据滚动百分比设置标题的透明度
            binding.tvCollapsedTitle.alpha = percentage
            binding.tvNotesTitle.alpha = 1 - percentage
            
            // 处理工具栏显示状态
            val shouldShowToolbar = percentage > 0.9f
            if (isToolbarShown != shouldShowToolbar) {
                isToolbarShown = shouldShowToolbar
                binding.appBarLayout.isActivated = shouldShowToolbar
                binding.toolbar.isActivated = shouldShowToolbar
            }
        }
    }

    private fun setupSearchManager() {
        // 获取底部导航栏
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        
        searchManager = SearchManager(
            context = requireContext(),
            rootView = binding.root as ViewGroup,
            bottomNav = bottomNav,
            viewModel = viewModel,
            lifecycleScope = lifecycleScope,
            onNoteClicked = { note ->
                val action = NotesFragmentDirections.actionNotesFragmentToEditFragment(
                    notebookId = note.notebookId,
                    noteId = note.id
                )
                findNavController().navigate(action)
            },
            onSearchClosed = {
                // 恢复其他UI元素
                binding.appBarLayout.visibility = View.VISIBLE
                binding.notesRecyclerView.visibility = View.VISIBLE
                binding.addNoteFab.visibility = View.VISIBLE
                binding.emptyView.visibility = if (adapter.itemCount == 0) View.VISIBLE else View.GONE
            }
        )
        
        // 设置搜索按钮点击事件
        binding.searchButton.setOnClickListener {
            if (!isEditMode) {
                // 隐藏其他UI元素
                binding.appBarLayout.visibility = View.GONE
                binding.notesRecyclerView.visibility = View.GONE
                binding.addNoteFab.visibility = View.GONE
                binding.emptyView.visibility = View.GONE
                
                // 显示搜索界面
                searchManager.showSearch()
            }
        }
    }

    private fun observeNotes() {
        lifecycleScope.launch {
            // 收集笔记列表更新
            viewModel.notes.collect { notes ->
                adapter.submitList(notes)
                // 更新笔记数量显示
                updateNotesCount(notes.size)
                // 更新空视图状态
                binding.emptyView.visibility = if (notes.isEmpty()) View.VISIBLE else View.GONE
            }
        }

        // 观察当前笔记本ID
        lifecycleScope.launch {
            viewModel.currentNotebookId.collect { notebookId ->
                // 更新标题
                val notebook = notebookViewModel.allNotebooks.value?.find { it.id == notebookId }
                binding.tvNotesTitle.text = when (notebookId) {
                    -2L -> "全部笔记"
                    else -> notebook?.name ?: "全部笔记"
                }
                binding.tvCollapsedTitle.text = binding.tvNotesTitle.text
            }
        }
    }

    private fun updateNotesCount(count: Int) {
        binding.tvNotesCount.text = "${count}篇笔记"
    }

    override fun onResume() {
        super.onResume()
        // 每次返回到Fragment时刷新笔记列表
        viewModel.refreshNotes()
        
        // 添加返回键处理
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            when {
                searchManager.isVisible() -> searchManager.hideSearch()
                isEditMode -> toggleEditMode(false)
                else -> isEnabled = false
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observeNotebooks() {
        notebookViewModel.allNotebooks.observe(viewLifecycleOwner) { notebooks ->
            Log.d("NotesFragment", "1Notebooks: $currentNotebookId")

            NotebookChipHelper.updateChipGroup(
                chipGroup = binding.notebookChipGroup,
                notebooks = notebooks,
                selectedNotebookId = currentNotebookId,
                onNotebookSelected = { selectedNotebookId ->
                    viewModel.setCurrentNotebook(selectedNotebookId)
                    Log.d("NotesFragment", "2Notebooks: $currentNotebookId")
                    when (selectedNotebookId) {
                        NotebookChipHelper.CHIP_ALL_NOTES_ID -> {
                            updateCollapsedTitle("全部笔记")
                            // 更新全部笔记数量
                            val totalNotes = notebooks.sumOf { it.noteCount }
                            binding.tvNotesCount.text = "${totalNotes}篇笔记"
                        }
                        //更新数量
                        else -> {
                            val notebook = notebooks.find { it.id == selectedNotebookId }
                            updateCollapsedTitle(notebook?.name ?: "全部笔记")
                            updateTVNotes(notebook!!)
                        }
                    }
                }
            )
        }
    }

    private fun updateCollapsedTitle(s: String) {
        binding.tvCollapsedTitle.text = s
    }

    @SuppressLint("SetTextI18n")
    private fun updateTVNotes(notebook: Notebook) {
        val noteCount = notebook.noteCount
        binding.tvNotesCount.text = "${noteCount}篇笔记"
    }

    private fun toggleEditMode(enabled: Boolean) {
        isEditMode = enabled
        
        // 更新界面
        binding.apply {
            // 标准模式组件
            tvCollapsedTitle.isVisible = !enabled
            searchButton.isVisible = !enabled
            btnSort.isVisible = !enabled
            
            // 编辑模式组件
            editModeToolbar.isVisible = enabled
            bottomActionBar.isVisible = enabled
        }
        
        // 更新适配器
        adapter.setEditMode(enabled)
        
        // 更新底部按钮状态
        updateBottomButtons()
    }

    private fun updateBottomButtons() {
        val selectedCount = adapter.getSelectedNotes().size
        binding.btnDelete.isEnabled = selectedCount > 0
        binding.btnMove.isEnabled = selectedCount > 0
    }
} 