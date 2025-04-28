package com.swu.myapplication.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.swu.myapplication.R
import com.swu.myapplication.databinding.FragmentDarkModeBinding
import com.swu.myapplication.utils.ThemeManager

class DarkModeFragment : Fragment() {
    private var _binding: FragmentDarkModeBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var themeManager: ThemeManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDarkModeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 初始化主题管理器
        themeManager = ThemeManager.getInstance(requireContext())
        
        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        // 根据当前主题设置初始状态
        when (themeManager.getThemeMode()) {
            ThemeManager.MODE_FOLLOW_SYSTEM -> {
                binding.switchFollowSystem.isChecked = true
                binding.cardManualSelection.visibility = View.GONE
            }
            ThemeManager.MODE_LIGHT -> {
                binding.switchFollowSystem.isChecked = false
                binding.radioLightMode.isChecked = true
                binding.cardManualSelection.visibility = View.VISIBLE
            }
            ThemeManager.MODE_DARK -> {
                binding.switchFollowSystem.isChecked = false
                binding.radioDarkMode.isChecked = true
                binding.cardManualSelection.visibility = View.VISIBLE
            }
        }
    }

    private fun setupListeners() {
        // 返回按钮
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // 跟随系统开关
        binding.switchFollowSystem.setOnCheckedChangeListener { _, isChecked ->
            binding.cardManualSelection.visibility = if (isChecked) View.GONE else View.VISIBLE
            if (isChecked) {
                // 跟随系统开启
                applyThemeAndRecreate(ThemeManager.MODE_FOLLOW_SYSTEM, "跟随系统")
            } else {
                // 跟随系统关闭，恢复上次手动选择的主题
                val lastMode = themeManager.getLastManualMode()
                
                // 更新UI选中状态
                when (lastMode) {
                    ThemeManager.MODE_LIGHT -> binding.radioLightMode.isChecked = true
                    ThemeManager.MODE_DARK -> binding.radioDarkMode.isChecked = true
                }
                
                // 应用主题
                val modeName = if (lastMode == ThemeManager.MODE_LIGHT) "普通模式" else "深色模式"
                applyThemeAndRecreate(lastMode, modeName)
            }
        }

        // 深色/普通模式选择
        binding.radioGroupTheme.setOnCheckedChangeListener { _, checkedId ->
            if (!binding.switchFollowSystem.isChecked) {
                when (checkedId) {
                    R.id.radioLightMode -> applyThemeAndRecreate(ThemeManager.MODE_LIGHT, "普通模式")
                    R.id.radioDarkMode -> applyThemeAndRecreate(ThemeManager.MODE_DARK, "深色模式")
                }
            }
        }
    }
    
    private fun applyThemeAndRecreate(mode: Int, modeName: String) {
        themeManager.setThemeMode(mode)
        Toast.makeText(requireContext(), "已切换至$modeName", Toast.LENGTH_SHORT).show()
        requireActivity().recreate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 