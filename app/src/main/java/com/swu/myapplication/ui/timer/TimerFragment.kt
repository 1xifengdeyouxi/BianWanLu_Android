package com.swu.myapplication.ui.timer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.swu.myapplication.R
import com.swu.myapplication.data.entity.Timer
import com.swu.myapplication.databinding.FragmentTimerBinding

class TimerFragment : Fragment() {
    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: TimerAdapter
    
    // 使用ViewModel
    private val viewModel: TimerViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupClickListeners()
        observeTimers()
    }
    
    private fun observeTimers() {
        viewModel.allTimers.observe(viewLifecycleOwner) { timers ->
            // 保存原始Timer数据以便后续使用
            adapter.setTimerData(timers)
            
            // 将数据转换为TimerItem并更新适配器
            val timerItems = timers.map { timer ->
                TimerItem(timer.title, timer.durationMinutes, timer.id)
            }
            updateAdapterData(timerItems)
        }
    }
    
    private fun updateAdapterData(timerItems: List<TimerItem>) {
        adapter.updateData(timerItems)
    }

    private fun setupRecyclerView() {
        // 初始化适配器（空数据）
        adapter = TimerAdapter(
            items = emptyList(),
            onStartClick = { timerItem ->
                // 从适配器获取完整的Timer对象
                val timer = adapter.getTimerById(timerItem.id)
                
                // 详细记录数据传递情况
                Log.d("TimerFragment", "onStartClick: timerItem.id=${timerItem.id}, timerItem.title=${timerItem.title}, timerItem.durationMinutes=${timerItem.durationMinutes}")
                Log.d("TimerFragment", "timer对象: ${timer != null}")
                
                // 点击开始按钮跳转到RunningTimerFragment
                val bundle = Bundle().apply {
                    // 确保传递非空值，使用timerItem作为备选
                    putString("timerTitle", timer?.title ?: timerItem.title)
                    putInt("durationMinutes", timer?.durationMinutes ?: timerItem.durationMinutes)
                    // 使用timer中的氛围设置，如果为空则默认使用"森林"
                    putString("atmosphereTitle", timer?.atmosphereTitle ?: "森林")
                    // 添加自定义图片URI
                    if (timer?.atmosphereImageUri != null) {
                        putString("atmosphere_image_uri", timer.atmosphereImageUri)
                        Log.d("TimerFragment", "传递自定义图片URI: ${timer.atmosphereImageUri}")
                    } else {
                        Log.d("TimerFragment", "没有自定义图片URI可传递")
                    }
                }
                
                // 记录Bundle内容
                Log.d("TimerFragment", "Bundle内容: timerTitle=${bundle.getString("timerTitle")}, durationMinutes=${bundle.getInt("durationMinutes")}")
                Log.d("TimerFragment", "Bundle内容: atmosphereTitle=${bundle.getString("atmosphereTitle")}, atmosphere_image_uri=${bundle.getString("atmosphere_image_uri")}")
                
                // 确保使用正确的导航参数
                try {
                    findNavController().navigate(R.id.action_timerFragment_to_runningTimerFragment, bundle)
                    Log.d("TimerFragment", "导航已执行")
                } catch (e: Exception) {
                    Log.e("TimerFragment", "导航失败: ${e.message}", e)
                }
            },
            onAddClick = {
                // 导航到创建时钟页面，传递isNewTimer标志
                findNavController().currentBackStackEntry?.savedStateHandle?.set("is_new_timer", true)
                findNavController().navigate(R.id.action_timerFragment_to_createTimerFragment)
            },
            onItemClick = { timerItem ->
                // 点击卡片时导航到编辑页面并传递数据
                navigateToCreateTimer(timerItem)
            }
        )

        binding.timerRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.timerRecyclerView.adapter = adapter
    }

    /**
     * 导航到创建/编辑时钟页面并传递数据
     */
    private fun navigateToCreateTimer(timerItem: TimerItem) {
        // 获取完整的Timer对象
        val timer = adapter.getTimerById(timerItem.id)
        
        // 创建Bundle来保存所有数据
        val bundle = Bundle().apply {
            // 明确设置为编辑模式
            putBoolean("is_new_timer", false)
            
            // 设置计时器ID
            putLong("timer_id", timer?.id ?: timerItem.id)
            
            // 设置时长数据 - 因为DurationItem是可序列化的，我们可以直接传递
            val durationMinutes = timer?.durationMinutes ?: timerItem.durationMinutes
            putSerializable("selected_duration", DurationItem(durationMinutes, "${durationMinutes}分钟"))
            
            // 设置空间名称
            putString("timer_title", timer?.title ?: timerItem.title)
            
            // 设置氛围信息
            timer?.let {
                val atmosphereTitle = it.atmosphereTitle ?: "森林"
                putString("atmosphere_title", atmosphereTitle)
                
                it.atmosphereImageUri?.let { uri ->
                    putString("atmosphere_image_uri", uri)
                }
            }
        }
        
        // 使用Bundle导航到创建/编辑页面
        findNavController().navigate(R.id.action_timerFragment_to_createTimerFragment, bundle)
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 