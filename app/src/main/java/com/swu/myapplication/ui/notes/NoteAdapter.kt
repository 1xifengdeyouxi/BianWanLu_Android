package com.swu.myapplication.ui.notes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.swu.myapplication.data.model.Note
import com.swu.myapplication.databinding.ItemNoteBinding
import java.text.SimpleDateFormat
import java.util.*

class NoteAdapter(
    private val onNoteClick: (Note) -> Unit
) : ListAdapter<Note, NoteAdapter.NoteViewHolder>(NoteDiffCallback()) {

    private var currentSortType = NotesSortPopupWindow.SortType.MODIFY_TIME
    private var isEditMode = false
    private val selectedNotes = mutableSetOf<Note>()
    private var onSelectionChanged: ((Int) -> Unit)? = null

    fun updateSortType(sortType: NotesSortPopupWindow.SortType) {
        currentSortType = sortType
        notifyDataSetChanged()
    }

    fun setEditMode(editMode: Boolean) {
        isEditMode = editMode
        selectedNotes.clear()
        notifyDataSetChanged()
    }

    fun getSelectedNotes(): Set<Note> = selectedNotes.toSet()

    fun setOnSelectionChangedListener(listener: (Int) -> Unit) {
        onSelectionChanged = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = getItem(position)
        holder.bind(note)
    }

    inner class NoteViewHolder(
        private val binding: ItemNoteBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onNoteClick(getItem(position))
                }
            }
        }

        fun bind(note: Note) {
            binding.apply {
                tvTitle.text = note.title
                tvContent.text = note.content

                // 根据排序类型显示不同的时间
                val timeToShow = when (currentSortType) {
                    NotesSortPopupWindow.SortType.CREATE_TIME -> note.createdTime
                    NotesSortPopupWindow.SortType.MODIFY_TIME -> note.modifiedTime
                    NotesSortPopupWindow.SortType.EDIT -> {
                        TODO()
                    }
                }
                tvTime.text = formatTime(timeToShow)

                checkBox.apply {
                    visibility = if (isEditMode) View.VISIBLE else View.GONE
                    isChecked = selectedNotes.contains(note)
                    setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            selectedNotes.add(note)
                        } else {
                            selectedNotes.remove(note)
                        }
                        onSelectionChanged?.invoke(selectedNotes.size)
                    }
                }

                root.setOnClickListener {
                    if (isEditMode) {
                        checkBox.isChecked = !checkBox.isChecked
                    } else {
                        onNoteClick(note)
                    }
                }
            }
        }

        private fun formatTime(timestamp: Long): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
    }

    private class NoteDiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }
} 