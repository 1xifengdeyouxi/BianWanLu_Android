package com.swu.myapplication.ui.notebook

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.view.children
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.swu.myapplication.data.model.Notebook

object NotebookChipHelper {
    const val CHIP_ALL_NOTES_ID = -2L //开始默认为全部笔记本标签


    // 定义颜色
    private val SELECTED_COLOR = Color.parseColor("#FFA500") // 选中时的橙色
    private val UNSELECTED_COLOR = Color.parseColor("#F5F5F5") // 未选中时的浅灰色
    private val TEXT_SELECTED_COLOR = Color.WHITE // 选中时的文字颜色
    private val TEXT_UNSELECTED_COLOR = Color.BLACK // 未选中时的文字颜色

    fun updateChipGroup(
        chipGroup: ChipGroup,
        notebooks: List<Notebook>,
        selectedNotebookId: Long = CHIP_ALL_NOTES_ID,
        onNotebookSelected: (Long) -> Unit
    ) {
        chipGroup.removeAllViews()

        // 添加"全部笔记"芯片（这是UI上的标签，不是数据库中的笔记本）
        addChip(chipGroup, "全部笔记", CHIP_ALL_NOTES_ID, selectedNotebookId, onNotebookSelected)

        // 添加其他笔记本芯片（过滤掉默认笔记本）
        notebooks.forEach { notebook ->
            if (notebook.id != NotebookViewModel.DEFAULT_NOTEBOOK_ID) {
                addChip(
                    chipGroup,
                    notebook.name,
                    notebook.id,
                    selectedNotebookId,
                    onNotebookSelected
                )
            }
        }
    }

    private fun addChip(
        chipGroup: ChipGroup,
        text: String,
        notebookId: Long,
        selectedNotebookId: Long,
        onNotebookSelected: (Long) -> Unit
    ) {
        val chip = Chip(chipGroup.context).apply {
            id = ViewGroup.generateViewId()
            this.text = text
            isCheckable = true
            tag = notebookId

            // 设置选中状态
            isChecked = (notebookId == selectedNotebookId)

            // 设置颜色状态列表
            val colorStateList = ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_checked),
                    intArrayOf(-android.R.attr.state_checked)
                ),
                intArrayOf(
                    SELECTED_COLOR,
                    UNSELECTED_COLOR
                )
            )

            // 设置文字颜色状态列表
            val textColorStateList = ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_checked),
                    intArrayOf(-android.R.attr.state_checked)
                ),
                intArrayOf(
                    TEXT_SELECTED_COLOR,
                    TEXT_UNSELECTED_COLOR
                )
            )

            chipBackgroundColor = colorStateList
            setTextColor(textColorStateList)

            // 设置点击监听器
            setOnClickListener {
                Log.d("NotebookChipHelper", "Chip clicked: $notebookId  $selectedNotebookId")
                // 如果点击的不是当前选中的标签
                // 取消其他Chip的选中状态
                chipGroup.children.forEach { child ->
                    if (child is Chip) {
                        child.isChecked = (child == this)
                    }
                }
                // 确保当前标签选中状态
                isChecked = true
                // 触发回调
                onNotebookSelected(notebookId)
            }
        }
        chipGroup.addView(chip)
    }
}