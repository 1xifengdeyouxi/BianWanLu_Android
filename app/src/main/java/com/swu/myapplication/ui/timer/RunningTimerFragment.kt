package com.swu.myapplication.ui.timer

import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.swu.myapplication.R
import com.swu.myapplication.databinding.FragmentRunningTimerBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 * 计时器运行界面Fragment
 */
class RunningTimerFragment : Fragment() {

    private var _binding: FragmentRunningTimerBinding? = null
    private val binding get() = _binding!!
    
    // 接收计时器参数
    private var timerTitle: String = "静心"
    private var durationMinutes: Int = 25
    private var atmosphereTitle: String = "森林"
    private var atmosphereResId: Int = R.drawable.senlin
    private var customImageUri: String? = null
    
    // 倒计时器
    private var countDownTimer: CountDownTimer? = null
    private var remainingTimeMillis: Long = 0
    
    // 计时器状态
    private var isTimerRunning = false
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRunningTimerBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 获取传递的参数
        arguments?.let { args ->
            timerTitle = args.getString("timerTitle", "静心")
            durationMinutes = args.getInt("durationMinutes", 25)
            
            // 获取氛围名称
            atmosphereTitle = args.getString("atmosphereTitle", "森林")
            
            // 获取自定义图片URI
            customImageUri = args.getString("atmosphere_image_uri")
            
            // 设置对应的预定义图片资源ID
            atmosphereResId = when (atmosphereTitle) {
                "城市" -> R.drawable.chengshi
                "高山" -> R.drawable.gaoshan
                "森林" -> R.drawable.senlin
                "星空" -> R.drawable.xingkong
                "海洋" -> R.drawable.haiyang
                //其他默认图片
                else -> R.drawable.chengshi
            }
           Log.d("RunningTimerFragment", " RunningTimerFragment中 $timerTitle $durationMinutes  $atmosphereTitle $customImageUri $atmosphereResId")
        }
        
        // 初始化UI
        setupUI()
        
        // 设置点击事件
        setupClickListeners()
        
        // 不再自动开始计时器，改为显示播放按钮
        remainingTimeMillis = durationMinutes * 60 * 1000L
        binding.btnPlayPause.setImageResource(R.drawable.ic_play)
    }
    
    private fun setupUI() {
        // 设置日期
        val dateFormat = SimpleDateFormat("M月d日，EEEE", Locale.CHINESE)
        binding.tvDate.text = dateFormat.format(Date())
        
        // 设置标题
        binding.tvTimerTitle.text = timerTitle
        Log.d("RunningTimerFragment", "Timer Title: $timerTitle")
        // 设置背景 - 优先使用标题
        if (!customImageUri.isNullOrEmpty()) {
            try {
                // 尝试加载自定义图片URI作为背景
                val uri = Uri.parse(customImageUri)
                binding.ivBackgroundImage.setImageURI(uri)
                binding.ivBackgroundImage.visibility = View.VISIBLE
                // 避免重复设置背景资源
                binding.root.setBackgroundResource(android.R.color.transparent)
                Log.d("RunningTimerFragment", "自定义图片: $uri")
            } catch (e: Exception) {
                // 加载失败时使用预定义资源
                binding.ivBackgroundImage.visibility = View.GONE
                binding.root.setBackgroundResource(atmosphereResId)
                Toast.makeText(requireContext(), "无法加载自定义背景，已使用默认背景", Toast.LENGTH_SHORT).show()
            }
        } else {
            // 使用预定义资源
            binding.ivBackgroundImage.visibility = View.GONE
            binding.root.setBackgroundResource(atmosphereResId)
            Log.d("RunningTimerFragment", "使用预定义资源:")
        }
        
        // 初始化时间显示
        updateTimerDisplay(durationMinutes * 60 * 1000L)
    }
    
    private fun setupClickListeners() {
        // 播放/暂停按钮
        binding.btnPlayPause.setOnClickListener {
            if (isTimerRunning) {
                pauseTimer()
            } else {
                startTimer()
            }
        }
        
        // 将设置按钮改为退出按钮
        binding.btnSettings.contentDescription = "退出"
        binding.btnSettings.setOnClickListener {
            // 退出并返回上一页面
            findNavController().navigateUp()
        }
    }
    
    private fun startTimer() {
        // 如果计时器已经运行，先取消
        countDownTimer?.cancel()
        
        // 使用剩余时间创建新的计时器
        countDownTimer = object : CountDownTimer(remainingTimeMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTimeMillis = millisUntilFinished
                updateTimerDisplay(millisUntilFinished)
            }
            
            override fun onFinish() {
                // 计时结束
                updateTimerDisplay(0)
                isTimerRunning = false
                binding.btnPlayPause.setImageResource(R.drawable.ic_play)
                showTimerFinishedMessage()
            }
        }.start()
        
        isTimerRunning = true
        binding.btnPlayPause.setImageResource(R.drawable.ic_pause)
    }
    
    private fun pauseTimer() {
        countDownTimer?.cancel()
        isTimerRunning = false
        binding.btnPlayPause.setImageResource(R.drawable.ic_play)
    }
    
    private fun updateTimerDisplay(millisUntilFinished: Long) {
        val minutes = (millisUntilFinished / 1000) / 60
        val seconds = (millisUntilFinished / 1000) % 60
        binding.tvTimerDisplay.text = String.format("%02d:%02d", minutes, seconds)
    }
    
    private fun showTimerFinishedMessage() {
        // 计时结束时的处理
        Toast.makeText(requireContext(), "计时完成", Toast.LENGTH_SHORT).show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
        _binding = null
    }
} 