package com.swu.myapplication.ui.todos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.swu.myapplication.R
import com.swu.myapplication.data.model.Todo
import java.text.SimpleDateFormat
import java.util.*

class TodoAdapter(
    private val onTodoClick: (Todo) -> Unit,
    private val onTodoCheckChanged: (Todo, Boolean) -> Unit
) : ListAdapter<Todo, TodoAdapter.TodoViewHolder>(TodoDiffCallback()) {

    private var isEditMode = false
    private val selectedTodos = mutableSetOf<Todo>()
    private var onSelectionChangedListener: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_todo, parent, false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todo = getItem(position)
        holder.bind(todo, isEditMode, selectedTodos.contains(todo))
    }

    fun setEditMode(editMode: Boolean) {
        isEditMode = editMode
        selectedTodos.clear()
        notifyDataSetChanged()
        onSelectionChangedListener?.invoke(0)
    }

    fun getSelectedTodos(): Set<Todo> = selectedTodos.toSet()

    fun setOnSelectionChangedListener(listener: (Int) -> Unit) {
        onSelectionChangedListener = listener
    }

    fun selectAll() {
        val currentList = currentList
        if (currentList.isNotEmpty()) {
            selectedTodos.addAll(currentList)
            notifyDataSetChanged()
            onSelectionChangedListener?.invoke(selectedTodos.size)
        }
    }
    
    fun clearAllSelections() {
        if (selectedTodos.isNotEmpty()) {
            selectedTodos.clear()
            notifyDataSetChanged()
            onSelectionChangedListener?.invoke(0)
        }
    }
    
    fun isAllSelected(): Boolean {
        val currentList = currentList
        return currentList.isNotEmpty() && selectedTodos.size == currentList.size
    }

    inner class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTodoTitle)
        private val checkBox: CheckBox = itemView.findViewById(R.id.cbTodoComplete)
        private val ivStar: ImageView = itemView.findViewById(R.id.ivTodoStar)
        private val ivDateReminder: ImageView = itemView.findViewById(R.id.ivDateReminder)
        private val ivTimeReminder: ImageView = itemView.findViewById(R.id.ivTimeReminder)
        private val ivSelectCheckbox: CheckBox = itemView.findViewById(R.id.cbTodoSelect)
        private val tvReminderDate: TextView = itemView.findViewById(R.id.tvReminderDate)

        fun bind(todo: Todo, isEditMode: Boolean, isSelected: Boolean) {
            tvTitle.text = todo.title
            checkBox.isChecked = todo.isCompleted
            ivStar.visibility = if (todo.isStarred) View.VISIBLE else View.GONE
            
            // 处理提醒图标
            ivDateReminder.visibility = if (todo.reminderDate != null) View.VISIBLE else View.GONE
            ivTimeReminder.visibility = if (todo.reminderTime != null) View.VISIBLE else View.GONE
            
            // 显示提醒日期
            if (todo.reminderDate != null) {
                tvReminderDate.visibility = View.VISIBLE
                tvReminderDate.text = formatDate(todo.reminderDate)
            } else {
                tvReminderDate.visibility = View.GONE
            }

            // 编辑模式下显示选择框，隐藏完成复选框
            ivSelectCheckbox.visibility = if (isEditMode) View.VISIBLE else View.GONE
            checkBox.visibility = if (isEditMode) View.GONE else View.VISIBLE
            ivSelectCheckbox.isChecked = isSelected
            
            // 根据完成状态调整标题样式
            if (todo.isCompleted && !isEditMode) {
                tvTitle.alpha = 0.5f
            } else {
                tvTitle.alpha = 1.0f
            }

            // 设置点击事件
            checkBox.setOnClickListener {
                onTodoCheckChanged(todo, checkBox.isChecked)
            }

            // 选择框点击事件
            ivSelectCheckbox.setOnClickListener {
                if (ivSelectCheckbox.isChecked) {
                    selectedTodos.add(todo)
                } else {
                    selectedTodos.remove(todo)
                }
                onSelectionChangedListener?.invoke(selectedTodos.size)
            }

            // 整个条目的点击事件
            itemView.setOnClickListener {
                if (isEditMode) {
                    ivSelectCheckbox.isChecked = !ivSelectCheckbox.isChecked
                    if (ivSelectCheckbox.isChecked) {
                        selectedTodos.add(todo)
                    } else {
                        selectedTodos.remove(todo)
                    }
                    onSelectionChangedListener?.invoke(selectedTodos.size)
                } else {
                    onTodoClick(todo)
                }
            }
        }

        private fun formatDate(timestamp: Long?): String {
            if (timestamp == null) return ""
            val date = Date(timestamp)
            val format = SimpleDateFormat("MM-dd", Locale.getDefault())
            return format.format(date)
        }
    }

    class TodoDiffCallback : DiffUtil.ItemCallback<Todo>() {
        override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            return oldItem == newItem
        }
    }
} 