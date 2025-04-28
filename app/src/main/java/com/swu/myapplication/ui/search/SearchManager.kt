package com.swu.myapplication.ui.search

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.swu.myapplication.R
import com.swu.myapplication.data.model.Note
import com.swu.myapplication.ui.notes.NoteViewModel
import kotlinx.coroutines.flow.collect

class SearchManager(
    private val context: Context,
    private val rootView: ViewGroup,
    private val bottomNav: BottomNavigationView,
    private val viewModel: NoteViewModel,
    private val lifecycleScope: LifecycleCoroutineScope,
    private val onNoteClicked: (Note) -> Unit,
    private val onSearchClosed: () -> Unit
) {
    private val searchView: View = LayoutInflater.from(context).inflate(R.layout.search_layout, rootView, false)
    private val searchAdapter = SearchAdapter { note -> onNoteClicked(note) }
    
    private val etSearch: EditText = searchView.findViewById(R.id.etSearch)
    private val btnSearchBack: ImageButton = searchView.findViewById(R.id.btnSearchBack)
    private val btnClearSearch: ImageButton = searchView.findViewById(R.id.btnClearSearch)
    private val searchResultsRecyclerView: RecyclerView = searchView.findViewById(R.id.searchResultsRecyclerView)
    private val tvNoResults: TextView = searchView.findViewById(R.id.tvNoResults)
    
    init {
        setupRecyclerView()
        setupListeners()
        observeSearchResults()
    }

    private fun setupRecyclerView() {
        searchResultsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = searchAdapter
        }
    }

    private fun setupListeners() {
        // 返回按钮点击监听
        btnSearchBack.setOnClickListener {
            hideSearch()
            onSearchClosed()
        }
        
        // 清除按钮点击监听
        btnClearSearch.setOnClickListener {
            etSearch.text.clear()
            viewModel.clearSearch()
            btnClearSearch.visibility = View.GONE
        }
        
        // 搜索框文本变化监听
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                viewModel.searchNotes(query)
                btnClearSearch.visibility = if (query.isNotEmpty()) View.VISIBLE else View.GONE
            }
            
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun observeSearchResults() {
        lifecycleScope.launchWhenStarted {
            viewModel.searchResults.collect { results ->
                searchAdapter.submitList(results)
                updateNoResultsVisibility(results)
            }
        }
    }

    private fun updateNoResultsVisibility(results: List<Note>) {
        if (results.isEmpty() && etSearch.text.isNotEmpty()) {
            tvNoResults.visibility = View.VISIBLE
            searchResultsRecyclerView.visibility = View.GONE
        } else {
            tvNoResults.visibility = View.GONE
            searchResultsRecyclerView.visibility = View.VISIBLE
        }
    }

    fun showSearch() {
        // 隐藏底部导航栏
        bottomNav.visibility = View.GONE
        
        // 将搜索视图添加到根视图
        if (searchView.parent == null) {
            rootView.addView(searchView)
        }
        
        // 显示软键盘
        etSearch.requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(etSearch, InputMethodManager.SHOW_IMPLICIT)
    }

    fun hideSearch() {
        // 隐藏软键盘
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(etSearch.windowToken, 0)
        
        // 从根视图移除搜索视图
        rootView.removeView(searchView)
        
        // 显示底部导航栏
        bottomNav.visibility = View.VISIBLE
        
        // 清除搜索结果
        viewModel.clearSearch()
        
        // 通知搜索已关闭
        onSearchClosed()
    }

    fun isVisible(): Boolean {
        return searchView.parent != null
    }
} 