package com.swu.myapplication.ui.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.swu.myapplication.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.swu.myapplication.ui.viewmodel.NotesViewModel


class EditFragment : Fragment() {
    private val viewModel: NotesViewModel by viewModels<NotesViewModel>()
    private lateinit var btnBack: ImageButton
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
        setupClickListeners()

    }

    private fun initViews(view:View) {
        btnBack = view.findViewById(R.id.btnBack)
    }
    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_editFragment_to_notesFragment)
        }
    }
} 