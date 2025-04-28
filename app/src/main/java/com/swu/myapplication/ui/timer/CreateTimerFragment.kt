package com.swu.myapplication.ui.timer

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.swu.myapplication.R
import com.swu.myapplication.data.entity.Timer
import com.swu.myapplication.databinding.FragmentCreateTimerBinding
import kotlinx.coroutines.launch

class CreateTimerFragment : Fragment() {
    private var _binding: FragmentCreateTimerBinding? = null
    private val binding get() = _binding!!

    // 使用ViewModel
    private val viewModel: TimerViewModel by viewModels()

    // 默认时长
    private var selectedDuration: DurationItem = DurationItem(20, "20分钟")

    // 默认氛围
    private var selectedAtmosphere: AtmosphereItem? = null

    // 标题（空间名称）
    private var timerTitle: String? = null

    // 计时器ID（编辑模式）
    private var timerId: Long = 0

    // 是否为新建模式
    private var isNewTimer: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateTimerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 从arguments获取参数
        arguments?.let { args ->
            // 获取创建/编辑模式标志
            if (args.containsKey("is_new_timer")) {
                isNewTimer = args.getBoolean("is_new_timer", true)
            }

            // 获取计时器ID（编辑模式）
            if (args.containsKey("timer_id")) {
                val savedId = args.getLong("timer_id", 0)
                if (savedId > 0) {
                    timerId = savedId
                    isNewTimer = false // 如果有ID，则必定是编辑模式
                }
            }

            // 获取标题
            if (args.containsKey("timer_title")) {
                val title = args.getString("timer_title")
                if (!title.isNullOrEmpty()) {
                    timerTitle = title
                    // 更新UI显示标题
                    binding.etSpaceName.setText(title)
                }
            }

            // 获取时长数据
            if (args.containsKey("selected_duration")) {
                val durationItem = args.getSerializable("selected_duration") as? DurationItem
                if (durationItem != null) {
                    selectedDuration = durationItem
                    // 更新UI显示选中的时长
                    binding.tvDurationText.text = durationItem.displayText
                }
            }

            // 获取氛围标题
            if (args.containsKey("atmosphere_title")) {
                val atmosphereTitle = args.getString("atmosphere_title")
                if (!atmosphereTitle.isNullOrEmpty()) {
                    // 从传递的氛围标题创建氛围项
                    val atmosphereImageResId = when (atmosphereTitle) {
                        "城市" -> R.drawable.chengshi
                        "高山" -> R.drawable.gaoshan
                        "森林" -> R.drawable.senlin
                        "星空" -> R.drawable.xingkong
                        "海洋" -> R.drawable.haiyang
                        //TODO
                        else -> R.drawable.senlin
                    }

                    // 获取自定义图片URI（如果有）
                    val customUri = args.getString("atmosphere_image_uri")
                    val mId = when (atmosphereTitle) {
                        "城市" -> 0
                        "高山" -> 1
                        "森林" -> 2
                        "星空" -> 3
                        "海洋" -> 4
                        //TODO
                        else -> 5
                    }
                    // 创建氛围项
                    selectedAtmosphere = AtmosphereItem(
                        id = mId,
                        title = atmosphereTitle,
                        imageResId = atmosphereImageResId,
                        customImageUri = customUri
                    )

                    // 更新UI显示
                    binding.tvAtmosphereText.text = atmosphereTitle
                }
            }
        }

        // 根据模式设置界面
        setupUI()

        setupClickListeners()
        observeSelectedDuration()
        observeSelectedAtmosphere()
    }

    private fun setupUI() {
        if (isNewTimer) {
            // 新建模式
            binding.tvTitle.text = "创建时钟"
            binding.cardDeleteTimer.visibility = View.GONE
        } else {
            // 编辑模式
            binding.tvTitle.text = "修改时钟"
            binding.cardDeleteTimer.visibility = View.VISIBLE
        }
    }

    private fun observeSelectedDuration() {
        // 从SavedStateHandle监听时长选择结果
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<DurationItem>("selected_duration")
            ?.observe(viewLifecycleOwner) { durationItem ->
                selectedDuration = durationItem
                // 更新UI显示选中的时长
                binding.tvDurationText.text = durationItem.displayText
            }
    }

    private fun observeSelectedAtmosphere() {
        // 监听从AtmospherePickerFragment返回的数据
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<AtmosphereItem>("selected_atmosphere")
            ?.observe(viewLifecycleOwner) { atmosphereItem ->
                if (atmosphereItem != null) {
                    selectedAtmosphere = atmosphereItem
                    // 更新UI显示选中的氛围
                    binding.tvAtmosphereText.text = atmosphereItem.title
                }
            }
    }

    private fun setupClickListeners() {
        // 取消按钮
        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }

        // 完成按钮
        binding.btnComplete.setOnClickListener {
            saveTimer()
        }

        // 使用时长选择
        binding.tvDuration.setOnClickListener {
            findNavController().navigate(R.id.action_createTimerFragment_to_durationPickerFragment)
        }

        // 氛围选择
        binding.tvAtmosphere.setOnClickListener {
            // 使用Bundle传递氛围数据
            val bundle = Bundle()
            if (selectedAtmosphere != null) {
                Log.d(
                    "CreateTimerFragment",
                    "Passing atmosphere: ${selectedAtmosphere?.title}, id: ${selectedAtmosphere?.id}"
                )
                bundle.putSerializable("current_atmosphere", selectedAtmosphere)
            } else {
                Log.d("CreateTimerFragment", "No atmosphere to pass, creating default")
                // 如果没有选中的氛围，创建一个默认的森林氛围
                val defaultAtmosphere = AtmosphereItem(1, "城市", R.drawable.chengshi)
                bundle.putSerializable("current_atmosphere", defaultAtmosphere)
            }
            findNavController().navigate(
                R.id.action_createTimerFragment_to_atmospherePickerFragment,
                bundle
            )
        }

        // 删除时钟
        binding.cardDeleteTimer.setOnClickListener {
            showDeleteConfirmDialog()
        }
    }

    /**
     * 保存计时器
     */
    private fun saveTimer() {
        // 获取输入的空间名称
        val spaceName = binding.etSpaceName.text.toString().trim()
        if (spaceName.isEmpty()) {
            Toast.makeText(requireContext(), "请输入空间名称", Toast.LENGTH_SHORT).show()
            return
        }

        // 获取氛围信息
        val atmosphereTitle = selectedAtmosphere?.title
        val atmosphereImageUri = selectedAtmosphere?.customImageUri
            ?: selectedAtmosphere?.let { it.imageResId.toString() }

        if (isNewTimer) {
            // 创建新计时器
            lifecycleScope.launch {
                try {
                    viewModel.createTimer(
                        title = spaceName,
                        durationMinutes = selectedDuration.minutes,
                        atmosphereTitle = atmosphereTitle,
                        atmosphereImageUri = atmosphereImageUri
                    )

                    // 创建成功后直接返回上一页
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "创建成功", Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        requireContext(),
                        "创建时钟失败: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            // 更新现有计时器
            val timer = Timer(
                id = timerId,
                title = spaceName,
                durationMinutes = selectedDuration.minutes,
                atmosphereTitle = atmosphereTitle,
                atmosphereImageUri = atmosphereImageUri
            )
            viewModel.update(timer)

            // 返回上一页面
            Toast.makeText(requireContext(), "保存成功", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }

    /**
     * 显示删除确认对话框
     */
    private fun showDeleteConfirmDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("确认删除")
            .setMessage("确定要删除此时钟吗？此操作无法撤销。")
            .setPositiveButton("删除") { _, _ ->
                if (timerId > 0) {
                    viewModel.deleteById(timerId)
                }
                findNavController().navigateUp()
            }
            .setNegativeButton("取消", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 