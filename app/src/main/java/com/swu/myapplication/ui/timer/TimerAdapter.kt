package com.swu.myapplication.ui.timer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.swu.myapplication.R
import com.swu.myapplication.data.entity.Timer

class TimerAdapter(
    private var items: List<TimerItem>,
    private val onStartClick: (TimerItem) -> Unit,
    private val onAddClick: () -> Unit,
    private val onItemClick: (TimerItem) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_TIMER_ITEM = 0
    private val TYPE_ADD_ITEM = 1
    
    // 存储原始Timer数据
    private var timerData: List<Timer> = emptyList()
    
    /**
     * 更新适配器数据
     */
    fun updateData(newItems: List<TimerItem>) {
        this.items = newItems
        notifyDataSetChanged()
    }
    
    /**
     * 设置原始Timer数据
     */
    fun setTimerData(timers: List<Timer>) {
        this.timerData = timers
    }
    
    /**
     * 根据TimerItem的ID获取对应的完整Timer对象
     */
    fun getTimerById(timerId: Long): Timer? {
        return timerData.find { it.id == timerId }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_TIMER_ITEM -> {
                val view = LayoutInflater.from(parent.context).inflate(
                    R.layout.item_timer, parent, false
                )
                TimerViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(
                    R.layout.item_add_timer, parent, false
                )
                AddTimerViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TimerViewHolder -> {
                val timerItem = items[position]
                holder.bind(timerItem)
                holder.btnStart.setOnClickListener {
                    onStartClick(timerItem)
                }
                holder.itemView.setOnClickListener {
                    onItemClick(timerItem)
                }
            }
            is AddTimerViewHolder -> {
                holder.itemView.setOnClickListener {
                    onAddClick()
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size + 1

    override fun getItemViewType(position: Int): Int {
        return if (position < items.size) TYPE_TIMER_ITEM else TYPE_ADD_ITEM
    }

    inner class TimerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTimerTitle)
        private val tvDuration: TextView = itemView.findViewById(R.id.tvTimerDuration)
        val btnStart: Button = itemView.findViewById(R.id.btnStartTimer)

        fun bind(timerItem: TimerItem) {
            tvTitle.text = timerItem.title
            tvDuration.text = "${timerItem.durationMinutes}分钟 >"
        }
    }

    inner class AddTimerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
} 