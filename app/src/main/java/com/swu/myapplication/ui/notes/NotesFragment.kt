package com.swu.myapplication.ui.notes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
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
import com.swu.myapplication.ui.notebook.NotebookChipHelper
import com.swu.myapplication.ui.notebook.NotebookViewModel
import kotlinx.coroutines.launch

class NotesFragment : Fragment() {
    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: NoteViewModel
    private lateinit var notebookViewModel: NotebookViewModel
    private lateinit var adapter: NoteAdapter
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

        //初始化笔记本
        notebookViewModel.initDefaultNotebooks()
    }

    private fun setupRecyclerView() {
        adapter = NoteAdapter()
        binding.notesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@NotesFragment.adapter
        }
    }

    private fun setupClickListeners() {
        binding.addNoteFab.setOnClickListener {
            val action = NotesFragmentDirections.actionNotesFragmentToEditFragment(
                notebookId = viewModel.currentNotebookId.value ?: 0L
            )
            findNavController().navigate(action)
        }

        binding.btnNotebookManager.setOnClickListener {
            findNavController().navigate(R.id.action_notesFragment_to_notebookListFragment)
        }
    }

    private fun setupAppBarListener() {
        binding.appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val maxScroll = appBarLayout.totalScrollRange
            val percentage = abs(verticalOffset).toFloat() / maxScroll.toFloat()

            // 控制标题的渐变效果
            binding.tvNotesTitle.alpha = 1 - percentage
            binding.tvCollapsedTitle.alpha = percentage
        })
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
        currentNotebookName = notebookName
        binding.tvCollapsedTitle.text = notebookName
        binding.tvNotesTitle.text = notebookName
    }

    private fun observeNotebooks() {
        notebookViewModel.allNotebooks.observe(viewLifecycleOwner) { notebooks ->
            NotebookChipHelper.updateChipGroup(
                chipGroup = binding.notebookChipGroup,
                notebooks = notebooks,
                selectedNotebookId = args.selectedNotebookId,  // 传入选中的笔记本ID
                onNotebookSelected = { selectedNotebookId ->
                    viewModel.setCurrentNotebook(selectedNotebookId)
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

    private fun handleArguments() {
        val selectedNotebookId = args.selectedNotebookId
        viewModel.setCurrentNotebook(selectedNotebookId)

        // 更新标题
        when (selectedNotebookId) {
            NotebookChipHelper.CHIP_ALL_NOTES_ID -> updateCollapsedTitle("全部笔记")
            else -> {
                lifecycleScope.launch {
                    val notebook = notebookViewModel.allNotebooks.value?.find { it.id == selectedNotebookId }
                    updateCollapsedTitle(notebook?.name ?: "全部笔记")
                }
            }
        }
    }
} 