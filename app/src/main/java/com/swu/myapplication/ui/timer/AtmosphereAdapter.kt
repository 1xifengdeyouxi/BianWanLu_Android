package com.swu.myapplication.ui.timer

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.swu.myapplication.R

/**
 * 氛围适配器类
 */
class AtmosphereAdapter : RecyclerView.Adapter<AtmosphereAdapter.ViewHolder>() {

    private val items = mutableListOf<AtmosphereItem>()
    private var selectedPosition = 0 // 默认选中第一项
    private var onItemSelectedListener: ((AtmosphereItem) -> Unit)? = null
    private var onAtmosphereClickListener: (() -> Unit)? = null

    /**
     * 设置选中项变化监听器
     */
    fun setOnItemSelectedListener(listener: (AtmosphereItem) -> Unit) {
        onItemSelectedListener = listener
    }
    
    /**
     * 设置氛围点击监听器
     */
    fun setOnAtmosphereClickListener(listener: () -> Unit) {
        onAtmosphereClickListener = listener
    }

    /**
     * 更新数据列表
     */
    fun updateItems(newItems: List<AtmosphereItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    /**
     * 获取当前选中的氛围项
     */
    fun getSelectedItem(): AtmosphereItem? {
        return if (items.isNotEmpty() && selectedPosition >= 0 && selectedPosition < items.size) {
            items[selectedPosition]
        } else {
            null
        }
    }

    /**
     * 设置选中位置
     */
    fun setSelectedPosition(position: Int) {
        if (position != selectedPosition && position >= 0 && position < items.size) {
            val oldPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(oldPosition)
            notifyItemChanged(selectedPosition)
            onItemSelectedListener?.invoke(items[selectedPosition])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_atmosphere, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        
        // 设置数据
        holder.titleTextView.text = item.title
        
        // 设置图片
        if (item.customImageUri != null && item.customImageUri.isNotEmpty()) {
            try {
                // 尝试加载自定义图片URI
                holder.iconImageView.setImageURI(Uri.parse(item.customImageUri))
            } catch (e: Exception) {
                // 加载失败时使用资源图片
                holder.iconImageView.setImageResource(item.imageResId)
            }
        } else {
            // 使用资源图片
            holder.iconImageView.setImageResource(item.imageResId)
        }

        // 设置选中状态
        val isSelected = selectedPosition == position
        
        // 切换背景资源
        holder.container.isSelected = isSelected
        holder.container.setBackgroundResource(
            if (isSelected) R.drawable.atmosphere_selected_indicator
            else R.drawable.atmosphere_item_background
        )

        // 设置点击事件
        holder.itemView.setOnClickListener {
            // 更新选中状态
            setSelectedPosition(position)
            // 通知外部点击事件
            onAtmosphereClickListener?.invoke()
        }
    }

    override fun getItemCount(): Int = items.size

    /**
     * ViewHolder类
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val container: ConstraintLayout = itemView.findViewById(R.id.container)
        val iconImageView: ImageView = itemView.findViewById(R.id.ivAtmosphereIcon)
        val titleTextView: TextView = itemView.findViewById(R.id.tvAtmosphereTitle)
    }
}