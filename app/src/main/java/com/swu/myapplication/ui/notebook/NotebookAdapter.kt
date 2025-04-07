// 定义笔记本列表的RecyclerView适配器
package com.swu.myapplication.ui.notebook

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.swu.myapplication.R
import com.swu.myapplication.data.model.Notebook
import com.swu.myapplication.databinding.ItemNotebookBinding

// 主适配器类，继承ListAdapter使用DiffUtil进行高效更新
class NotebookAdapter(
    // 定义三个lambda参数用于回调事件处理
    private val onNotebookClick: (Notebook) -> Unit,    // 笔记本点击事件
    private val onNotebookEdit: (Notebook) -> Unit,     // 编辑事件
    private val onNotebookDelete: (Notebook) -> Unit    // 删除事件
) : ListAdapter<Notebook, NotebookAdapter.NotebookViewHolder>(NotebookDiffCallback()) {

    // 创建ViewHolder实例
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotebookViewHolder {
        // 使用ViewBinding加载列表项布局
        val binding = ItemNotebookBinding.inflate(
            LayoutInflater.from(parent.context),  // 从父容器获取LayoutInflater
            parent,                               // 父视图组
            false                                 // 不立即附加到父视图
        )
        return NotebookViewHolder(binding)  // 返回绑定视图的ViewHolder
    }

    // 绑定数据到ViewHolder
    override fun onBindViewHolder(holder: NotebookViewHolder, position: Int) {
        holder.bind(getItem(position))  // 获取对应位置的数据项进行绑定
    }

    // 内部ViewHolder类
    inner class NotebookViewHolder(
        private val binding: ItemNotebookBinding  // 持有绑定视图的引用
    ) : RecyclerView.ViewHolder(binding.root) {  // 继承自RecyclerView.ViewHolder

        init {
            // 整个列表项的点击监听
            binding.root.setOnClickListener {
                val position = adapterPosition  // 获取当前适配器位置
                if (position != RecyclerView.NO_POSITION) {  // 检查位置有效性
                    onNotebookClick(getItem(position))  // 触发点击回调
                }
            }

            // 更多按钮的点击监听
            binding.btnMore.setOnClickListener { view ->
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    showPopupMenu(view, getItem(position))  // 显示弹出菜单
                }
            }
        }

        // 绑定数据到视图
        fun bind(notebook: Notebook) {
                binding.tvNotebookName.text = notebook.name  // 设置笔记本名称
                binding.tvNoteCount.text = "${notebook.noteCount}篇笔记"  // 设置笔记数量
        }

        // 显示弹出菜单
        private fun showPopupMenu(view: android.view.View, notebook: Notebook) {
            PopupMenu(view.context, view).apply {  // 创建PopupMenu实例
                inflate(R.menu.menu_notebook_item)  // 加载菜单布局

                // 菜单项点击监听
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.action_edit_notebook -> {  // 编辑操作
                            onNotebookEdit(notebook)
                            true
                        }
                        R.id.action_delete_notebook -> {  // 删除操作
                            onNotebookDelete(notebook)
                            true
                        }
                        else -> false
                    }
                }
                show()  // 显示菜单
            }
        }
    }

    // DiffUtil回调类，用于比较列表项差异
    private class NotebookDiffCallback : DiffUtil.ItemCallback<Notebook>() {
        // 检查是否是同一个item（通常通过ID判断）
        override fun areItemsTheSame(oldItem: Notebook, newItem: Notebook): Boolean {
            return oldItem.id == newItem.id
        }

        // 检查内容是否相同（需要数据类实现equals()）
        override fun areContentsTheSame(oldItem: Notebook, newItem: Notebook): Boolean {
            return oldItem == newItem  // 依赖数据类的结构比较
        }
    }
}