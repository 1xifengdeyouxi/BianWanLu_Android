package com.swu.myapplication.ui.timer

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.swu.myapplication.R
import com.swu.myapplication.databinding.FragmentAtmospherePickerBinding
import kotlin.math.abs

class AtmospherePickerFragment : Fragment() {

    private var _binding: FragmentAtmospherePickerBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var adapter: AtmosphereAdapter
    private var atmosphereItems = mutableListOf<AtmosphereItem>()
    
    // 当前选中的氛围项
    private var selectedAtmosphereItem: AtmosphereItem? = null
    
    // 从CreateTimerFragment传过来的选中氛围
    private var previousSelectedAtmosphere: AtmosphereItem? = null
    
    // 正在处理图片选择
    private var isPickingImage = false
    
    // 自定义氛围的位置
    private val customAtmospherePosition: Int 
        get() = atmosphereItems.size - 1
    
    // 从相册选择图片的处理器
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // 标记处理图片选择已完成
        isPickingImage = false
        
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                // 将图片URI保存到SharedPreferences
                saveCustomImageUri(uri)
                // 更新自定义氛围项的图片
                val updatedCustomItem = updateCustomAtmosphereItem(uri)
                
                // 将选中的自定义氛围返回给CreateTimerFragment并返回
                findNavController().previousBackStackEntry?.savedStateHandle?.set(
                    "selected_atmosphere", 
                    updatedCustomItem
                )
                findNavController().navigateUp()
            }
        } else {
            // 用户取消选择图片，什么都不做，保留在当前界面
            Log.d("AtmospherePickerFragment", "用户取消了图片选择")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, 
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAtmospherePickerBinding.inflate(inflater, container, false)
        
        // 从arguments中获取之前选择的氛围
        arguments?.let { args ->
            if (args.containsKey("current_atmosphere")) {
                val atmosphere = args.getSerializable("current_atmosphere") as? AtmosphereItem
                atmosphere?.let {
                    previousSelectedAtmosphere = it
                    Log.d("AtmospherePickerFragment", "Received previous atmosphere: $it")
                }
            }
        }
        Log.d("AtmospherePickerFragment", "Checking for previous atmosphere")
        
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupAtmosphereItems()
        setupViewPager()
        setupClickListeners()
        
        // 在所有设置完成后，记录日志信息以便调试
        Log.d("AtmospherePickerFragment", "Setup complete. Selected item: ${selectedAtmosphereItem?.title}, position: ${atmosphereItems.indexOf(selectedAtmosphereItem)}")
    }
    
    private fun setupAtmosphereItems() {
        // 创建氛围列表
        atmosphereItems = mutableListOf(
            AtmosphereItem(0, "城市", R.drawable.chengshi),
            AtmosphereItem(1, "高山", R.drawable.gaoshan),
            AtmosphereItem(2, "森林", R.drawable.senlin),
            AtmosphereItem(3, "星空", R.drawable.xingkong),
            AtmosphereItem(4, "海洋", R.drawable.haiyang)
        )
        
        // 获取保存的自定义图片URI
        val savedCustomImageUri = getCustomImageUri()
        
        // 添加自定义氛围选项
        val customAtmosphere = if (savedCustomImageUri != null) {
            AtmosphereItem(
                5,
                "自定义",
                R.drawable.ic_add_image,
                customImageUri = savedCustomImageUri.toString()
            )
        } else {
            AtmosphereItem(
                6,
                "自定义", 
                R.drawable.ic_add_image
            )
        }
        atmosphereItems.add(customAtmosphere)
        
        // 不在这里设置默认选中项，而是在setupViewPager中处理
    }
    
    private fun setupViewPager() {
        adapter = AtmosphereAdapter()
        adapter.updateItems(atmosphereItems)
        binding.viewPager.adapter = adapter
        
        // 设置离屏页面限制为1，避免预加载过多页面
        binding.viewPager.offscreenPageLimit = 1
        
        // 获取ViewPager2内部的RecyclerView并设置边距和padding
        val recyclerView = binding.viewPager.getChildAt(0) as RecyclerView
        recyclerView.apply {
            // 设置padding以确保每个页面之间有足够间距
            val padding = resources.getDimensionPixelOffset(R.dimen.viewpager_page_margin)
            setPadding(padding, 0, padding, 0)
            // 允许边缘渐变效果
            clipToPadding = false
        }
        
        // 使用组合式页面转换器
        val compositePageTransformer = CompositePageTransformer().apply {
            // 添加页面边距
            addTransformer(MarginPageTransformer(resources.getDimensionPixelOffset(R.dimen.viewpager_page_margin) / 2))
            
            // 添加缩放和透明度效果
            addTransformer { page, position ->
                val r = 1 - abs(position)
                // 根据位置调整缩放比例，当页面居中时缩放为1，离中心越远缩放越小
                page.scaleY = 0.85f + r * 0.15f
                page.scaleX = 0.85f + r * 0.15f
                // 根据位置调整透明度，当页面居中时透明度为1，离中心越远透明度越低
                page.alpha = 0.5f + r * 0.5f
            }
        }
        
        // 应用页面转换器
        binding.viewPager.setPageTransformer(compositePageTransformer)
        
        // 配置点击监听
        adapter.setOnAtmosphereClickListener { 
            onCardClick()
        }
        
        // 创建指示器点
        createDotIndicators()
        
        // 处理初始选中状态
        handleInitialSelection()
        
        // 监听页面变化 - 最后设置，避免初始化过程触发不必要的回调
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                
                // 更新指示器点和标题
                updatePageIndicator(position)
                binding.tvAtmosphereTitle.text = atmosphereItems[position].title
                
                // 记录当前显示的页面
                Log.d("AtmospherePickerFragment", "页面切换到: ${atmosphereItems[position].title}, position: $position")
                Log.d("AtmospherePickerFragment", "当前选中项是: ${selectedAtmosphereItem?.title}")
                
                // 当页面切换到选中项时，显示视觉提示（选中框）
                if (selectedAtmosphereItem != null && 
                    position < atmosphereItems.size && 
                    atmosphereItems[position].id == selectedAtmosphereItem?.id) {
                    adapter.setSelectedPosition(position)
                    Log.d("AtmospherePickerFragment", "显示页面是当前选中项，显示选中状态")
                } else {
                    // 当页面不是选中项时，不显示选中框
                    // 这里需要注意不能直接使用notifyItemChanged，因为这会导致RecyclerView重建整个项
                    if (adapter.getSelectedItem()?.id != atmosphereItems[position].id) {
                        // 仅在当前项不是选中项时触发刷新
                        Log.d("AtmospherePickerFragment", "显示页面不是当前选中项")
                        adapter.notifyItemChanged(position)
                    }
                }
            }
        })
    }
    
    /**
     * 处理初始选中状态
     */
    private fun handleInitialSelection() {
        var initialPosition = 0
        
        if (previousSelectedAtmosphere != null) {
            // 调试输出
            Log.d("AtmospherePickerFragment", "Previous atmosphere: ${previousSelectedAtmosphere?.title}, id: ${previousSelectedAtmosphere?.id}")
            
            // 优先使用ID精确匹配 - 最可靠
            val positionById = atmosphereItems.indexOfFirst {
                it.id == previousSelectedAtmosphere?.id
            }
            
            // 辅助标志 - 是否为自定义氛围
            val isCustom = previousSelectedAtmosphere?.title?.equals("自定义") == true
            
            initialPosition = if (positionById >= 0) {
                // ID匹配成功
                Log.d("AtmospherePickerFragment", "ID匹配成功: position=$positionById, title=${atmosphereItems[positionById].title}")
                positionById
            } else if (isCustom) {
                // 如果是自定义氛围但ID未匹配到，使用自定义位置
                Log.d("AtmospherePickerFragment", "使用自定义氛围位置: position=$customAtmospherePosition")
                customAtmospherePosition
            } else {
                // 尝试通过标题匹配
                val positionByTitle = atmosphereItems.indexOfFirst { 
                    it.title == previousSelectedAtmosphere?.title 
                }
                
                if (positionByTitle >= 0) {
                    Log.d("AtmospherePickerFragment", "标题匹配成功: position=$positionByTitle, title=${atmosphereItems[positionByTitle].title}")
                    positionByTitle
                } else {
                    // 所有匹配都失败，使用默认位置
                    Log.d("AtmospherePickerFragment", "所有匹配失败! 使用默认位置0")
                    0
                }
            }
            
            // 记录匹配的具体信息
            val matchInfo = "匹配结果: 将ID=${previousSelectedAtmosphere?.id},title=${previousSelectedAtmosphere?.title}匹配到position=$initialPosition,title=${atmosphereItems[initialPosition].title},id=${atmosphereItems[initialPosition].id}"
            Log.d("AtmospherePickerFragment", matchInfo)
            
            // 检查是否匹配错误 - 应该匹配到海洋但却匹配到自定义
            if (previousSelectedAtmosphere?.title == "海洋" && atmosphereItems[initialPosition].title == "自定义") {
                Log.d("AtmospherePickerFragment", "检测到错误匹配! 海洋被错误地匹配到自定义氛围，强制修正")
                // 修正为海洋的位置
                val correctPosition = atmosphereItems.indexOfFirst { it.title == "海洋" }
                if (correctPosition >= 0) {
                    initialPosition = correctPosition
                    Log.d("AtmospherePickerFragment", "已修正为海洋位置: $correctPosition")
                }
            }
            
            // 设置选中项
            selectedAtmosphereItem = atmosphereItems[initialPosition]
            
            // 调试输出
            Log.d("AtmospherePickerFragment", "设置选中项为: ${selectedAtmosphereItem?.title}, position: $initialPosition")
        } else {
            // 默认选中第一项
            selectedAtmosphereItem = atmosphereItems.first()
            Log.d("AtmospherePickerFragment", "没有之前的氛围，使用第一项: ${selectedAtmosphereItem?.title}")
        }
        
        // 设置ViewPager位置
        binding.viewPager.setCurrentItem(initialPosition, false)
        
        // 更新选中状态
        adapter.setSelectedPosition(initialPosition)
        
        // 更新标题
        binding.tvAtmosphereTitle.text = atmosphereItems[initialPosition].title
        
        // 更新指示器点的选中状态
        updatePageIndicator(initialPosition)
    }
    
    /**
     * 当卡片被点击时
     */
    private fun onCardClick() {
        val currentPosition = binding.viewPager.currentItem
        
        // 检查是否是自定义氛围
        if (currentPosition == customAtmospherePosition) {
            // 无论是否已有自定义图片，都打开相册
            Log.d("AtmospherePickerFragment", "Opening gallery for custom atmosphere")
            openGallery()
        } else {
            // 非自定义氛围，更新选中状态
            val previousSelected = selectedAtmosphereItem
            selectedAtmosphereItem = atmosphereItems[currentPosition]
            
            // 更新UI和选中状态
            adapter.setSelectedPosition(currentPosition)
            
            // 确保指示器点也被更新
            updatePageIndicator(currentPosition)
            
            // 在选中时显示明确的视觉反馈，让用户知道已选中
            Log.d("AtmospherePickerFragment", "Selected changed from ${previousSelected?.title} to ${selectedAtmosphereItem?.title}")
            Toast.makeText(requireContext(), "已选择\"${selectedAtmosphereItem?.title}\"氛围", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * 返回上一页面并传递选中的氛围
     */
    private fun returnWithSelectedItem(item: AtmosphereItem) {
        // 使用previousBackStackEntry设置数据
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            "selected_atmosphere", 
            item
        )
        Log.d("AtmospherePickerFragment", "Returning selected atmosphere: ${item.title}, id: ${item.id}")
        findNavController().navigateUp()
    }
    
    private fun setupClickListeners() {
        // 返回按钮
        binding.btnBack.setOnClickListener {
            if (!isPickingImage) {
                // 返回用户选中的项，而不是当前显示的项
                if (selectedAtmosphereItem != null) {
                    Log.d("AtmospherePickerFragment", "Returning with selected item: ${selectedAtmosphereItem?.title}, id: ${selectedAtmosphereItem?.id}")
                    returnWithSelectedItem(selectedAtmosphereItem!!)
                } else {
                    // 如果没有选中项，则使用之前选中的项，或者默认第一项
                    val itemToReturn = previousSelectedAtmosphere ?: atmosphereItems.firstOrNull()
                    Log.d("AtmospherePickerFragment", "No selected item, returning with: ${itemToReturn?.title}")
                    itemToReturn?.let {
                        returnWithSelectedItem(it)
                    } ?: findNavController().navigateUp() // 如果列表为空，直接返回
                }
            } else {
                Log.d("AtmospherePickerFragment", "Picking image in progress, ignoring back button")
            }
        }
    }
    
    /**
     * 打开系统相册
     */
    private fun openGallery() {
        // 标记正在选择图片
        isPickingImage = true
        
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        
        try {
            pickImageLauncher.launch(intent)
        } catch (e: Exception) {
            // 重置标记
            isPickingImage = false
            Toast.makeText(requireContext(), "无法打开相册", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * 保存自定义图片URI到SharedPreferences
     */
    private fun saveCustomImageUri(uri: Uri) {
        requireActivity().getSharedPreferences("atmosphere_prefs", Activity.MODE_PRIVATE)
            .edit()
            .putString("custom_atmosphere_uri", uri.toString())
            .apply()
    }
    
    /**
     * 获取保存的自定义图片URI
     */
    private fun getCustomImageUri(): Uri? {
        val uriString = requireActivity().getSharedPreferences("atmosphere_prefs", Activity.MODE_PRIVATE)
            .getString("custom_atmosphere_uri", null)
        
        return if (uriString != null) Uri.parse(uriString) else null
    }
    
    /**
     * 更新自定义氛围项
     * @return 更新后的自定义氛围项
     */
    private fun updateCustomAtmosphereItem(uri: Uri): AtmosphereItem {
        // 更新自定义氛围项的标题
        val customTitle = "自定义"
        val updatedCustomItem = AtmosphereItem(
            5,  // 修改ID为5，与列表中的自定义氛围ID保持一致
            customTitle,
            R.drawable.ic_add_image,
            customImageUri = uri.toString()
        )
        
        // 更新列表中的项
        atmosphereItems[customAtmospherePosition] = updatedCustomItem
        adapter.notifyItemChanged(customAtmospherePosition)
        
        // 如果当前正在查看自定义项，更新选中项
        if (binding.viewPager.currentItem == customAtmospherePosition) {
            selectedAtmosphereItem = updatedCustomItem
            binding.tvAtmosphereTitle.text = customTitle
        }
        
        return updatedCustomItem
    }
    
    private fun createDotIndicators() {
        binding.dotsIndicator.removeAllViews()
        
        // 为每个页面创建一个点
        for (i in atmosphereItems.indices) {
            val dot = ImageView(requireContext())
            dot.setImageDrawable(ContextCompat.getDrawable(
                requireContext(),
                R.drawable.dot_indicator_selector
            ))
            
            // 设置点的大小和间距
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(12, 0, 12, 0) // 增大间距
            dot.layoutParams = params
            
            // 初始不选中任何点，选中状态由updatePageIndicator控制
            dot.isSelected = false
            
            // 添加点击监听，点击时切换到对应页面
            dot.setOnClickListener {
                binding.viewPager.currentItem = i
            }
            
            // 添加到容器
            binding.dotsIndicator.addView(dot)
        }
        
        // 根据当前ViewPager位置更新点的选中状态
        updatePageIndicator(binding.viewPager.currentItem)
    }
    
    private fun updatePageIndicator(currentPosition: Int) {
        // 更新指示器点的选中状态
        for (i in 0 until binding.dotsIndicator.childCount) {
            val dot = binding.dotsIndicator.getChildAt(i) as ImageView
            dot.isSelected = i == currentPosition
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 