package com.swu.myapplication.ui.notebook

import android.content.Context
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.swu.myapplication.data.model.Notebook

object NotebookDialogHelper {
    fun createAddNotebookDialog(
        context: Context,
        onConfirm: (String) -> Unit
    ): AlertDialog {
        val editText = EditText(context).apply {
            hint = "笔记本名称"
        }

        return AlertDialog.Builder(context)
            .setTitle("新建笔记本")
            .setView(editText)
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
        }

        return AlertDialog.Builder(context)
            .setTitle("编辑笔记本")
            .setView(editText)
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
        return AlertDialog.Builder(context)
            .setTitle("删除笔记本")
            .setMessage("确定要删除笔记本${notebook.name}吗？")
            .setPositiveButton("确定") { _, _ ->
                onConfirm()
            }
            .setNegativeButton("取消", null)
            .create()
    }
} 