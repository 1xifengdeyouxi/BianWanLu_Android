package com.swu.myapplication.ui.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.swu.myapplication.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.swu.myapplication.ui.viewmodel.NotesViewModel
import kotlin.math.abs

class NotesFragment : Fragment() {
    private val viewModel: NotesViewModel by viewModels<NotesViewModel>()
    private lateinit var adapter: NoteAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var addNoteFab: FloatingActionButton
    private lateinit var tvNotesTitle: TextView
    private lateinit var tvCollapsedTitle: TextView
    private lateinit var tvNotesCount: TextView
    private lateinit var appBarLayout: AppBarLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupRecyclerView()
        setupClickListeners()
        setupAppBarListener()
        observeNotes()
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
        adapter = NoteAdapter { note ->

        }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@NotesFragment.adapter
        }
    }

    private fun setupClickListeners() {
        addNoteFab.setOnClickListener {
            //跳转到EditFragment界面
            findNavController().navigate(R.id.action_notesFragment_to_editFragment)
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

        view?.findViewById<View>(R.id.cardDefaultNotes)?.setOnClickListener {
            // TODO: Filter notes by default tag
        }

        view?.findViewById<View>(R.id.cardMaterials)?.setOnClickListener {
            // TODO: Switch to materials view
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
        viewModel.allNotes.observe(viewLifecycleOwner) { notes ->
            adapter.submitList(notes)
            tvNotesCount.text = "${notes.size} 篇笔记"
        }
    }
} 