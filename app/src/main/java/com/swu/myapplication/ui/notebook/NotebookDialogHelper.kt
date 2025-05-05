package com.swu.myapplication.ui.notebook

import android.content.Context
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.swu.myapplication.R
import com.swu.myapplication.data.model.Notebook

object NotebookDialogHelper {
    fun createAddNotebookDialog(
        context: Context,
        onConfirm: (String) -> Unit
    ): AlertDialog {
        val editText = EditText(context).apply {
            hint = "笔记本名称"
            
            // 设置内边距，优化布局
            setPadding(
                context.resources.getDimensionPixelSize(R.dimen.dialog_padding_horizontal),
                context.resources.getDimensionPixelSize(R.dimen.dialog_padding_vertical),
                context.resources.getDimensionPixelSize(R.dimen.dialog_padding_horizontal),
                context.resources.getDimensionPixelSize(R.dimen.dialog_padding_vertical)
            )
        }
        
        // 使用帧布局包装EditText，增加内边距
        val container = FrameLayout(context).apply {
            setPadding(
                context.resources.getDimensionPixelSize(R.dimen.dialog_padding_horizontal),
                context.resources.getDimensionPixelSize(R.dimen.dialog_padding_vertical),
                context.resources.getDimensionPixelSize(R.dimen.dialog_padding_horizontal),
                context.resources.getDimensionPixelSize(R.dimen.dialog_padding_vertical)
            )
            addView(editText)
        }

        return MaterialAlertDialogBuilder(context)
            .setTitle("新建笔记本")
            .setView(container)
            .setPositiveButton("确定") { _, _ ->
                val name = editText.text.toString().trim()
                onConfirm(name)
            }
            .setNegativeButton("取消", null)
            .create()
    }

    fun createEditNotebookDialog(
        context: Context,
        notebook: Notebook,
        onConfirm: (String) -> Unit
    ): AlertDialog {
        val editText = EditText(context).apply {
            setText(notebook.name)
            hint = "笔记本名称"
            
            // 设置内边距，优化布局
            setPadding(
                context.resources.getDimensionPixelSize(R.dimen.dialog_padding_horizontal),
                context.resources.getDimensionPixelSize(R.dimen.dialog_padding_vertical),
                context.resources.getDimensionPixelSize(R.dimen.dialog_padding_horizontal),
                context.resources.getDimensionPixelSize(R.dimen.dialog_padding_vertical)
            )
        }
        
        // 使用帧布局包装EditText，增加内边距
        val container = FrameLayout(context).apply {
            setPadding(
                context.resources.getDimensionPixelSize(R.dimen.dialog_padding_horizontal),
                context.resources.getDimensionPixelSize(R.dimen.dialog_padding_vertical),
                context.resources.getDimensionPixelSize(R.dimen.dialog_padding_horizontal),
                context.resources.getDimensionPixelSize(R.dimen.dialog_padding_vertical)
            )
            addView(editText)
        }

        return MaterialAlertDialogBuilder(context)
            .setTitle("编辑笔记本")
            .setView(container)
            .setPositiveButton("确定") { _, _ ->
                val name = editText.text.toString().trim()
                if (name.isNotEmpty()) {
                    onConfirm(name)
                }
            }
            .setNegativeButton("取消", null)
            .create()
    }

    fun createDeleteNotebookDialog(
        context: Context,
        notebook: Notebook,
        onConfirm: () -> Unit
    ): AlertDialog {
        return MaterialAlertDialogBuilder(context)
            .setTitle("删除笔记本")
            .setMessage("确定要删除笔记本${notebook.name}吗？")
            .setPositiveButton("确定") { _, _ ->
                onConfirm()
            }
            .setNegativeButton("取消", null)
            .create()
    }
} 