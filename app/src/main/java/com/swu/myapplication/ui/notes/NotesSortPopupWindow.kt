package com.swu.myapplication.ui.notes

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.google.android.material.internal.ViewUtils.dpToPx
import com.swu.myapplication.databinding.PopupNotesSortBinding
import com.swu.myapplication.myUtils.dp2px

class NotesSortPopupWindow(context: Context) : PopupWindow(context) {
    private val binding: PopupNotesSortBinding
    private var onSortTypeChangedListener: ((SortType) -> Unit)? = null

    init {
        // 初始化布局
        binding = PopupNotesSortBinding.inflate(LayoutInflater.from(context))
        contentView = binding.root

        // 设置弹窗属性
        width = context.dp2px(160)
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        isFocusable = true
        isOutsideTouchable = true
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 设置点击事件
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.optionCreateTime.setOnClickListener {
            updateSortType(SortType.CREATE_TIME)
        }

        binding.optionModifyTime.setOnClickListener {
            updateSortType(SortType.MODIFY_TIME)
        }

        binding.optionEdit.setOnClickListener {
            updateSortType(SortType.EDIT)
        }
    }

    private fun updateSortType(sortType: SortType) {
        // 更新UI
        binding.ivCreateTimeCheck.visibility = if (sortType == SortType.CREATE_TIME) View.VISIBLE else View.GONE
        binding.ivModifyTimeCheck.visibility = if (sortType == SortType.MODIFY_TIME) View.VISIBLE else View.GONE

        // 通知监听器
        onSortTypeChangedListener?.invoke(sortType)
        dismiss()
    }

    fun setOnSortTypeChangedListener(listener: (SortType) -> Unit) {
        onSortTypeChangedListener = listener
    }

    enum class SortType {
        CREATE_TIME,
        MODIFY_TIME,
        EDIT
    }
} 