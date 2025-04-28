package com.swu.myapplication.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.swu.myapplication.R
import com.swu.myapplication.data.model.Note
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SearchAdapter(private val onNoteClicked: (Note) -> Unit) :
    ListAdapter<Note, SearchAdapter.SearchViewHolder>(NoteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_result, parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val note = getItem(position)
        holder.bind(note)
    }

    inner class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvSearchResultTitle)
        private val tvContent: TextView = itemView.findViewById(R.id.tvSearchResultContent)
        private val tvDate: TextView = itemView.findViewById(R.id.tvSearchResultDate)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onNoteClicked(getItem(position))
                }
            }
        }

        fun bind(note: Note) {
            tvTitle.text = note.title.ifEmpty { "无标题" }
            tvContent.text = note.content
            tvDate.text = formatDate(note.modifiedTime)
        }

        private fun formatDate(timestamp: Long): String {
            val date = Date(timestamp)
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            return format.format(date)
        }
    }

    class NoteDiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }
} 