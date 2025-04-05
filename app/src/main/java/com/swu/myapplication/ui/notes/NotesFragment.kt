package com.swu.myapplication.ui.notes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.chip.Chip
import com.swu.myapplication.data.database.AppDatabase
import com.swu.myapplication.data.repository.NoteRepository
import com.swu.myapplication.data.repository.NotebookRepository
import com.swu.myapplication.databinding.FragmentNotesBinding
import com.swu.myapplication.ui.notebook.NotebookChipHelper
import com.swu.myapplication.ui.notebook.NotebookViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.abs

class NotesFragment : Fragment() {
    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: NoteViewModel
    private lateinit var notebookViewModel: NotebookViewModel
    private lateinit var adapter: NoteAdapter
    private val args: NotesFragmentArgs by navArgs()

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
        
        // 观察数据变化
        observeNotes()
        observeNotebooks()
    }

    private fun handleNotebookSelection() {
        lifecycleScope.launch {
            // 如果从其他Fragment传入了特定的笔记本ID
            if (args.selectedNotebookId != -1L) {
                viewModel.setCurrentNotebook(args.selectedNotebookId)
            } else if (currentNotebookId == NotebookChipHelper.CHIP_ALL_NOTES_ID) {
                // 如果当前没有选中的笔记本，使用上次保存的ID
                val lastId = viewModel.currentNotebookId.first()
                viewModel.setCurrentNotebook(lastId)
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
    }

    private fun setupAppBarListener() {
        binding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val maxScroll = appBarLayout.totalScrollRange
            val percentage = abs(verticalOffset).toFloat() / maxScroll.toFloat()
            binding.tvNotesTitle.alpha = 1 - percentage
            binding.tvCollapsedTitle.alpha = percentage
        }
    }

    private fun observeNotes() {
        lifecycleScope.launch {
            viewModel.notes.collect { notes ->
                adapter.submitList(notes)
                binding.tvNotesCount.text = "${notes.size}篇笔记"
            }
        }
    }

    private fun updateCollapsedTitle(notebookName: String) {
        binding.tvCollapsedTitle.text = notebookName
        binding.tvNotesTitle.text = notebookName
    }

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
                        NotebookChipHelper.CHIP_ALL_NOTES_ID -> updateCollapsedTitle("全部笔记")
                        else -> {
                            val notebook = notebooks.find { it.id == selectedNotebookId }
                            updateCollapsedTitle(notebook?.name ?: "全部笔记")
                        }
                    }
                }
            )
        }
    }
} 