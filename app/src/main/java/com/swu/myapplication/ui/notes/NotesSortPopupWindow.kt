package com.swu.myapplication.ui.notes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.swu.myapplication.databinding.PopupNotesSortBinding

class NotesSortPopupWindow(context: Context) {
    private val binding: PopupNotesSortBinding = PopupNotesSortBinding.inflate(LayoutInflater.from(context))
    private val popupWindow: PopupWindow
    private var currentSortType: SortType = SortType.MODIFY_TIME
    private var onSortTypeChangedListener: ((SortType) -> Unit)? = null

    init {
        // 设置PopupWindow的属性
        popupWindow = PopupWindow(
            binding.root,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            isOutsideTouchable = true
            isFocusable = true
            elevation = 10f
        }

        // 设置点击监听
        setupClickListeners()
        // 初始化UI状态
        updateSortTypeUI(currentSortType)
    }

    private fun setupClickListeners() {
        binding.layoutCreateTime.setOnClickListener {
            updateSortType(SortType.CREATE_TIME)
            onSortTypeChangedListener?.invoke(SortType.CREATE_TIME)
            dismiss()
        }

        binding.layoutModifyTime.setOnClickListener {
            updateSortType(SortType.MODIFY_TIME)
            onSortTypeChangedListener?.invoke(SortType.MODIFY_TIME)
            dismiss()
        }
        binding.layoutEditNotes.setOnClickListener {
            onSortTypeChangedListener?.invoke(SortType.MODIFY_TIME)
            dismiss()
        }
    }

    private fun updateSortType(sortType: SortType) {
        currentSortType = sortType
        updateSortTypeUI(sortType)
    }

    private fun updateSortTypeUI(sortType: SortType) {
        binding.ivCreateTime.visibility = if (sortType == SortType.CREATE_TIME) View.VISIBLE else View.INVISIBLE
        binding.ivModifyTime.visibility = if (sortType == SortType.MODIFY_TIME) View.VISIBLE else View.INVISIBLE
    }

    fun show(anchor: View) {
        // 计算弹出位置，显示在按钮下方
        popupWindow.showAsDropDown(anchor)
    }

    private fun dismiss() {
        popupWindow.dismiss()
    }

    fun setOnSortTypeChangedListener(listener: (SortType) -> Unit) {
        onSortTypeChangedListener = listener
    }

    enum class SortType {
        CREATE_TIME,
        MODIFY_TIME,
        EDIT_NOTES
    }
} 