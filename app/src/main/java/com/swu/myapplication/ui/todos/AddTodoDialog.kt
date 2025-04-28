package com.swu.myapplication.ui.todos

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.swu.myapplication.R
import com.swu.myapplication.data.model.Todo
import com.swu.myapplication.databinding.DialogAddTodoBinding

class AddTodoDialog {
    private val dialog: Dialog
    private lateinit var binding: DialogAddTodoBinding
    private val context: Context
    private val onSaveTodo: (Todo) -> Unit
    private var existingTodo: Todo? = null
    
    // 构造函数1：添加新待办
    constructor(context: Context, onSaveTodo: (Todo) -> Unit) {
        this.context = context
        this.onSaveTodo = onSaveTodo
        this.dialog = Dialog(context)
        setupDialog()
    }
    
    // 构造函数2：编辑已有待办
    constructor(context: Context, todo: Todo, onSaveTodo: (Todo) -> Unit) {
        this.context = context
        this.onSaveTodo = onSaveTodo
        this.existingTodo = todo
        this.dialog = Dialog(context)
        setupDialog()
        
        // 填充已有数据
        binding.etTodoTitle.setText(todo.title)
        // 如果是编辑模式，更改标题
        binding.tvDialogTitle.text = "编辑待办事项"
    }
    
    private fun setupDialog() {
        binding = DialogAddTodoBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        
        // 设置透明背景和位置
        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setGravity(Gravity.BOTTOM)
            // 对话框以外部分半透明
            setDimAmount(0.5f)
            
            // 设置宽度为屏幕宽度
            val layoutParams = attributes
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
            layoutParams.horizontalMargin = 0f
            
            // 保证对话框在软键盘上方
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            attributes = layoutParams
        }
        
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        // 取消按钮点击事件
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        
        // 保存按钮点击事件
        binding.btnSave.setOnClickListener {
            val title = binding.etTodoTitle.text.toString().trim()
            if (title.isEmpty()) {
                Toast.makeText(context, "请输入待办事项", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val currentTime = System.currentTimeMillis()
            
            // 创建或更新Todo对象
            val todo = existingTodo?.copy(
                title = title,
                modificationTime = currentTime
            ) ?: Todo(
                title = title,
                creationTime = currentTime,
                modificationTime = currentTime
            )
            
            // 回调保存方法
            onSaveTodo(todo)
            dismiss()
        }
        
        // 提醒按钮点击事件（暂不实现）
        binding.btnAddReminder.setOnClickListener {
            Toast.makeText(context, "提醒功能暂未实现", Toast.LENGTH_SHORT).show()
        }
    }
    
    fun show() {
        dialog.show()
        
        // 自动弹出键盘并聚焦到输入框
        binding.etTodoTitle.requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.etTodoTitle, InputMethodManager.SHOW_IMPLICIT)
    }
    
    fun dismiss() {
        dialog.dismiss()
    }
} 