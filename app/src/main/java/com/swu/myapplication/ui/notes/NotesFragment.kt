package com.swu.myapplication.ui.notes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.swu.myapplication.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.math.abs
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.chip.Chip
import com.swu.myapplication.data.database.AppDatabase
import com.swu.myapplication.data.repository.NoteRepository
import com.swu.myapplication.data.repository.NotebookRepository
import com.swu.myapplication.databinding.FragmentNotesBinding
import com.swu.myapplication.ui.notebook.NotebookViewModel
import kotlinx.coroutines.launch

class NotesFragment : Fragment() {
    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: NoteViewModel
    private lateinit var notebookViewModel: NotebookViewModel
    private lateinit var adapter: NoteAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var addNoteFab: FloatingActionButton
    private lateinit var tvNotesTitle: TextView
    private lateinit var tvCollapsedTitle: TextView
    private lateinit var tvNotesCount: TextView
    private lateinit var appBarLayout: AppBarLayout
    private val args: NotesFragmentArgs by navArgs()
    private var currentNotebookName: String = "全部笔记"

    private var isArgumentsHandled = false

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
        setupViewModel()
        initViews(view)
        setupRecyclerView()
        setupClickListeners()
        setupAppBarListener()
        observeNotes()
        observeNotebooks()
    }

    private fun setupViewModel() {
        val database = AppDatabase.getDatabase(requireContext())
        val repository = NoteRepository(database.noteDao())
        val factory = NoteViewModel.Factory(repository)
        viewModel = ViewModelProvider(this, factory)[NoteViewModel::class.java]

        val notebookRepository = NotebookRepository(database.notebookDao())
        val notebookFactory = NotebookViewModel.Factory(notebookRepository)
        notebookViewModel = ViewModelProvider(this, notebookFactory)[NotebookViewModel::class.java]

        notebookViewModel.initDefaultNotebooks()
    }

    private fun initViews(view: View) {
        recyclerView = view.findViewById(R.id.notesRecyclerView)
        addNoteFab = view.findViewById(R.id.addNoteFab)
        tvNotesTitle = view.findViewById(R.id.tvNotesTitle)
        tvCollapsedTitle = view.findViewById(R.id.tvCollapsedTitle)
        tvNotesCount = view.findViewById(R.id.tvNotesCount)
        appBarLayout = view.findViewById(R.id.appBarLayout)
    }

    private fun setupRecyclerView() {
        adapter = NoteAdapter()
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@NotesFragment.adapter
        }
    }

    private fun setupClickListeners() {
        addNoteFab.setOnClickListener {
            val action = NotesFragmentDirections.actionNotesFragmentToEditFragment(
                notebookId = viewModel.currentNotebookId.value ?: 0L
            )
            findNavController().navigate(action)
        }

        binding.btnNotebookManager.setOnClickListener {
            findNavController().navigate(R.id.action_notesFragment_to_notebookManagerFragment)
        }

        view?.findViewById<View>(R.id.searchButton)?.setOnClickListener {
            // TODO: Implement search functionality
        }

        view?.findViewById<View>(R.id.settingsButton)?.setOnClickListener {
            // TODO: Implement settings functionality
        }

        view?.findViewById<View>(R.id.layoutAllNotes)?.setOnClickListener {
            // TODO: Show bottom sheet for tag management
        }

        binding.btnNotebookManager.setOnClickListener {
            findNavController().navigate(R.id.action_notesFragment_to_notebookManagerFragment)
        }
    }

    private fun setupAppBarListener() {
        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val maxScroll = appBarLayout.totalScrollRange
            val percentage = abs(verticalOffset).toFloat() / maxScroll.toFloat()

            // 控制标题的渐变效果
            tvNotesTitle.alpha = 1 - percentage
            tvCollapsedTitle.alpha = percentage
        })
    }

    private fun observeNotes() {
        lifecycleScope.launch {
            viewModel.notes.collect { notes ->
                adapter.submitList(notes)
                tvNotesCount.text = "${notes.size}篇笔记"
            }
        }
    }

    private fun updateCollapsedTitle(notebookName: String) {
        currentNotebookName = notebookName
        tvCollapsedTitle.text = notebookName
        tvNotesTitle.text = notebookName
    }

    private fun observeNotebooks() {
        notebookViewModel.allNotebooks.observe(viewLifecycleOwner) { notebooks ->
            NotebookChipHelper.updateChipGroup(binding.notebookChipGroup, notebooks) { selectedNotebookId ->
                viewModel.setCurrentNotebook(selectedNotebookId)
                when (selectedNotebookId) {
                    -2L -> updateCollapsedTitle("全部笔记")
                    -1L -> updateCollapsedTitle("待办")
                    else -> {
                        val notebook = notebooks.find { it.id == selectedNotebookId }
                        updateCollapsedTitle(notebook?.name ?: "全部笔记")
                    }
                }
            }
            if (!isArgumentsHandled) {
                isArgumentsHandled = true
                binding.notebookChipGroup.post {
                    handleArguments()
                }
            }
        }
    }

    private fun handleArguments() {
        val selectedNotebookId = args.selectedNotebookId

        Log.d("wmt","NotesFragment中 selectedNotebookId = $selectedNotebookId")
        
        viewModel.setCurrentNotebook(selectedNotebookId)

        binding.notebookChipGroup.post {
            try {
                when (selectedNotebookId) {
                    -2L -> {
                        binding.notebookChipGroup.check(NotebookChipHelper.CHIP_ALL_NOTES_ID)
                        updateCollapsedTitle("全部笔记")
                        Log.d("wmt", "选中全部笔记标签")
                    }
                    -1L -> {
                        binding.notebookChipGroup.check(NotebookChipHelper.CHIP_TODO_ID)
                        updateCollapsedTitle("待办")
                        Log.d("wmt", "选中待办标签")
                    }
                    else -> {
                        var found = false
                        binding.notebookChipGroup.children.forEach { view ->
                            if (view is Chip) {
                                val chipNotebookId = view.tag as? Long
                                if (chipNotebookId == selectedNotebookId) {
                                    binding.notebookChipGroup.check(view.id)
                                    found = true
                                    val notebook = notebookViewModel.allNotebooks.value?.find { it.id == selectedNotebookId }
                                    updateCollapsedTitle(notebook?.name ?: "全部笔记")
                                    Log.d("wmt", "选中笔记本标签: $selectedNotebookId")
                                    return@forEach
                                }
                            }
                        }
                        if (!found) {
                            binding.notebookChipGroup.check(NotebookChipHelper.CHIP_ALL_NOTES_ID)
                            viewModel.setCurrentNotebook(0L)
                            updateCollapsedTitle("全部笔记")
                            Log.d("wmt", "未找到对应笔记本，默认选中全部笔记")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("wmt", "设置选中状态失败: ${e.message}")
                binding.notebookChipGroup.check(NotebookChipHelper.CHIP_ALL_NOTES_ID)
                viewModel.setCurrentNotebook(0L)
                updateCollapsedTitle("全部笔记")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 