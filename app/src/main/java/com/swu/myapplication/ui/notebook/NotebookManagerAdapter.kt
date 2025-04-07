package com.swu.myapplication.ui.notebook

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.swu.myapplication.data.model.Notebook
import com.swu.myapplication.databinding.ItemNotebookManagerBinding

class NotebookManagerAdapter(
    private val onNotebookChecked: (Notebook, Boolean) -> Unit
) : ListAdapter<Notebook, NotebookManagerAdapter.ViewHolder>(NotebookDiffCallback()) {

    private val checkedNotebooks = mutableSetOf<Long>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNotebookManagerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemNotebookManagerBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val notebook = getItem(position)
                    binding.checkBox.isChecked = !binding.checkBox.isChecked
                }
            }

            binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val notebook = getItem(position)
                    if (isChecked) {
                        checkedNotebooks.add(notebook.id)
                    } else {
                        checkedNotebooks.remove(notebook.id)
                    }
                    onNotebookChecked(notebook, isChecked)
                }
            }
        }

        fun bind(notebook: Notebook) {
            binding.apply {
                tvNotebookName.text = notebook.name
                tvNoteCount.text = "${notebook.noteCount}篇笔记"
                checkBox.isChecked = checkedNotebooks.contains(notebook.id)
            }
        }
    }

    private class NotebookDiffCallback : DiffUtil.ItemCallback<Notebook>() {
        override fun areItemsTheSame(oldItem: Notebook, newItem: Notebook): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Notebook, newItem: Notebook): Boolean {
            return oldItem == newItem
        }
    }

    fun clearChecked() {
        checkedNotebooks.clear()
        notifyDataSetChanged()
    }
} 