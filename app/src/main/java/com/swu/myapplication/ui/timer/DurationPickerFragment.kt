package com.swu.myapplication.ui.timer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.gzuliyujiang.wheelpicker.OptionPicker
import com.swu.myapplication.databinding.FragmentDurationPickerBinding

class DurationPickerFragment : Fragment() {

    private var _binding: FragmentDurationPickerBinding? = null
    private val binding get() = _binding!!
    
    // 记录选中的分钟数
    private var selectedMinutes: Int = 25 // 默认30分钟

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDurationPickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupListeners()
        // 初始化所有RadioButton为未选中状态
        resetAllRadioButtons()
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener {
            saveSelectedDuration()
            findNavController().navigateUp()
        }
        
        // 初始化自定义分钟显示
        binding.tvCustomMinutes.text = "${selectedMinutes}分钟"
    }

    private fun setupListeners() {
        // 设置各个选项的点击事件
        binding.optionUnlimited.setOnClickListener {
            updateRadioButtonSelection(binding.radioUnlimited)
            saveSelectedDuration(0, "不限时长")
        }
        binding.radioUnlimited.setOnClickListener {
            updateRadioButtonSelection(binding.radioUnlimited)
            saveSelectedDuration(0, "不限时长")
        }

        binding.option25min.setOnClickListener {
            updateRadioButtonSelection(binding.radio25min)
            saveSelectedDuration(25, "25分钟")
        }
        binding.radio25min.setOnClickListener {
            updateRadioButtonSelection(binding.radio25min)
            saveSelectedDuration(25, "25分钟")
        }

        binding.option40min.setOnClickListener {
            updateRadioButtonSelection(binding.radio40min)
            saveSelectedDuration(40, "40分钟")
        }
        binding.radio40min.setOnClickListener {
            updateRadioButtonSelection(binding.radio40min)
            saveSelectedDuration(40, "40分钟")
        }

        binding.option60min.setOnClickListener {
            updateRadioButtonSelection(binding.radio60min)
            saveSelectedDuration(60, "60分钟")
        }
        binding.radio60min.setOnClickListener {
            updateRadioButtonSelection(binding.radio60min)
            saveSelectedDuration(60, "60分钟")
        }

        binding.optionCustom.setOnClickListener {
            updateRadioButtonSelection(binding.radioCustom)
            showOptionPicker()
        }
        binding.radioCustom.setOnClickListener {
            updateRadioButtonSelection(binding.radioCustom)
            showOptionPicker()
        }
    }

    // 重置所有RadioButton为未选中状态
    private fun resetAllRadioButtons() {
        binding.radioUnlimited.isChecked = false
        binding.radio25min.isChecked = false
        binding.radio40min.isChecked = false
        binding.radio60min.isChecked = false
        binding.radioCustom.isChecked = false
    }

    // 更新RadioButton的选中状态
    private fun updateRadioButtonSelection(selectedRadioButton: android.widget.RadioButton) {
        // 先重置所有的RadioButton
        resetAllRadioButtons()
        // 设置当前选中的RadioButton
        selectedRadioButton.isChecked = true
    }
    
    // 保存选择的时长
    private fun saveSelectedDuration(minutes: Int = selectedMinutes, displayText: String? = null) {
        selectedMinutes = minutes
        val text = displayText ?: "${minutes}分钟"
        
        // 如果是自定义选项，更新显示
        if (binding.radioCustom.isChecked) {
            binding.tvCustomMinutes.text = text
        }
        
        // 保存选择的时长到navigation的savedStateHandle，以便返回时使用
        val durationItem = DurationItem(minutes, text)
        findNavController().previousBackStackEntry?.savedStateHandle?.set("selected_duration", durationItem)
    }

    private fun showOptionPicker() {
        // 创建 OptionPicker 实例
        val picker = OptionPicker(requireActivity())
        
        // 准备数据
        val minutesList = listOf(5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 90, 120)
        val displayTexts = minutesList.map { 
            if (it >= 60) {
                val hours = it / 60
                val remainMinutes = it % 60
                if (remainMinutes == 0) {
                    "${hours}小时"
                } else {
                    "${hours}小时${remainMinutes}分钟"
                }
            } else {
                "${it}分钟"
            }
        }

        // 设置数据
        picker.setData(displayTexts)
        
        // 找到当前选中分钟数的索引
        val initialPosition = minutesList.indexOf(selectedMinutes).takeIf { it >= 0 } ?: 5 // 默认选中30分钟(索引5)
        picker.setDefaultPosition(initialPosition)

        // 设置选择回调
        picker.setOnOptionPickedListener { position, item ->
            val minutes = minutesList[position]
            selectedMinutes = minutes
            binding.tvCustomMinutes.text = item.toString()
            
            // 保存选择的时长
            saveSelectedDuration(minutes, item.toString())
        }

        // 显示选择器
        picker.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}