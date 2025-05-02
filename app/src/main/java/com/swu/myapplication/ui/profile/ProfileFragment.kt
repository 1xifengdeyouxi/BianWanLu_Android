package com.swu.myapplication.ui.profile

import android.Manifest
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.swu.myapplication.R
import com.swu.myapplication.databinding.DialogAboutUsBinding
import com.swu.myapplication.databinding.DialogFeedbackOptionsBinding
import com.swu.myapplication.databinding.DialogQrcodeViewBinding
import com.swu.myapplication.databinding.FragmentProfileBinding
import com.swu.myapplication.databinding.DialogRateSupportBinding
import android.widget.TextView
import com.swu.myapplication.utils.RomUtils

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    
    // 邮箱地址常量
    private val EMAIL_ADDRESS = "wmtcode@qq.com"
    
    // 当前需要保存的Drawable
    private var currentDrawableToSave: Drawable? = null
    
    // 权限请求回调
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // 权限获取成功，保存图片
            currentDrawableToSave?.let {
                if (saveImageToGallery(requireContext(), it)) {
                    Toast.makeText(requireContext(), "二维码已保存到相册", Toast.LENGTH_SHORT).show()
                    // 保存成功后打开微信扫一扫
                    requireContext().openWeChatScanner()
                }
            }
        } else {
            Toast.makeText(requireContext(), "需要存储权限才能保存图片", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        // 设置功能卡片的点击事件
        binding.btnTimer.setOnClickListener {
            findNavController().navigate(R.id.action_nav_profile_to_timerFragment)
        }

        binding.btnGame.setOnClickListener {
            findNavController().navigate(R.id.action_nav_profile_to_gameListFragment)
        }

        binding.btnDarkMode.setOnClickListener {
            findNavController().navigate(R.id.action_nav_profile_to_darkModeFragment)
        }

        // 设置按钮的点击事件
        binding.btnFeedback.setOnClickListener {
            showFeedbackOptions()
        }

        binding.btnAboutUs.setOnClickListener {
            showAboutUsDialog()
        }
        
        // 用户协议与隐私政策点击事件
        binding.btnUserAgreement.setOnClickListener {
            findNavController().navigate(R.id.action_nav_profile_to_userAgreementFragment)
        }

        binding.btnRate.setOnClickListener {
            showRateSupportDialog()
        }
    }

    /**
     * 显示关于我们对话框
     */
    private fun showAboutUsDialog() {
        val dialog = Dialog(requireContext())
        val dialogBinding = DialogAboutUsBinding.inflate(layoutInflater)
        
        dialog.setContentView(dialogBinding.root)
        dialog.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            
            // 设置对话框宽度为屏幕宽度的85%
            val displayMetrics = resources.displayMetrics
            val width = (displayMetrics.widthPixels * 0.85).toInt()
            
            // 设置对话框布局参数
            val layoutParams = attributes
            layoutParams.width = width
            attributes = layoutParams
        }
        
        // 获取应用版本信息
        val packageInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
        val versionName = packageInfo.versionName
        
        // 设置版本信息
        dialogBinding.tvVersion.text = "版本$versionName"
        
        // 设置应用描述
        dialogBinding.tvAppDescription.text = getString(R.string.app_description)
        
        // 了解更多按钮点击事件
        dialogBinding.btnLearnMore.setOnClickListener {
            // 这里可以添加跳转到官网或更多信息的逻辑
            Toast.makeText(requireContext(), R.string.more_features_coming, Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        
        dialog.show()
    }

    private fun showFeedbackOptions() {
        val dialog = Dialog(requireContext())
        val dialogBinding = DialogFeedbackOptionsBinding.inflate(layoutInflater)
        
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        
        // 设置关闭按钮点击事件
        dialogBinding.btnClose.setOnClickListener {
            dialog.dismiss()
        }
        
        // 设置选项点击事件
        dialogBinding.cardQQGroup.setOnClickListener {
            // 调用加入QQ群方法
            if (joinQQGroup("lDqPLTgVcCx5ClpA_1ygWPnLbQA2Xqh9")) {
                Toast.makeText(requireContext(), "正在打开QQ", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "未安装QQ或QQ版本不支持", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        
        dialogBinding.cardEmail.setOnClickListener {
            // 打开邮箱应用
            openMail()
            dialog.dismiss()
        }
        
        dialogBinding.cardPublicAccount.setOnClickListener {
            // 显示二维码对话框
            showQRCodeDialog()
            dialog.dismiss()
        }
        
        dialog.show()
    }
    
    /**
     * 显示二维码对话框
     */
    private fun showQRCodeDialog() {
        val dialog = Dialog(requireContext())
        val dialogBinding = DialogQrcodeViewBinding.inflate(layoutInflater)
        
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        
        // 设置二维码图片 - 使用weixi.jpg
        dialogBinding.ivQrCode.setImageResource(R.drawable.weixi)
        
        // 设置关闭按钮点击事件
        dialogBinding.btnClose.setOnClickListener {
            dialog.dismiss()
        }
        
        // 设置图片长按保存事件
        dialogBinding.ivQrCode.setOnLongClickListener {
            val drawable = dialogBinding.ivQrCode.drawable
            // 保存当前需要保存的Drawable
            currentDrawableToSave = drawable
            
            // 检查权限并保存图片
            if (checkAndRequestStoragePermission()) {
                if (saveImageToGallery(requireContext(), drawable)) {
                    Toast.makeText(requireContext(), R.string.save_and_open_wechat, Toast.LENGTH_SHORT).show()
                    // 保存成功后打开微信扫一扫
                    requireContext().openWeChatScanner()
                }
            }
            true
        }
        
        dialog.show()
    }
    
    /**
     * 检查并请求存储权限
     * @return 是否已经有权限
     */
    private fun checkAndRequestStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10及以上使用MediaStore API不需要存储权限
            true
        } else {
            // Android 9及以下需要检查WRITE_EXTERNAL_STORAGE权限
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    true
                }
                else -> {
                    // 请求权限
                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    false
                }
            }
        }
    }
    
    /**
     * 保存图片到系统相册
     */
    private fun saveImageToGallery(context: Context, drawable: Drawable?): Boolean {
        // 类型检查
        if (drawable !is BitmapDrawable) {
            Toast.makeText(context, "无效的图片格式", Toast.LENGTH_SHORT).show()
            return false
        }
        
        val bitmap = drawable.bitmap

        return try {
            // 准备媒体库数据
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "qrcode_${System.currentTimeMillis()}.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
            }

            // 插入媒体库记录
            val uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, 
                contentValues
            ) ?: run {
                Toast.makeText(context, "创建文件失败", Toast.LENGTH_SHORT).show()
                return false
            }

            // 写入图片数据
            context.contentResolver.openOutputStream(uri)?.use { os ->
                if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)) {
                    Toast.makeText(context, "图片压缩失败", Toast.LENGTH_SHORT).show()
                    return false
                }

                // Android Q+ 特殊处理
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                    context.contentResolver.update(uri, contentValues, null, null)
                }

                // 刷新相册
                context.sendBroadcast(
                    Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri)
                )
                true
            } ?: false
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "保存失败: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            false
        }
    }
    
    /**
     * 跳转到微信扫码界面
     * @receiver Context 上下文
     * @return Boolean 是否成功跳转
     */
    private fun Context.openWeChatScanner(): Boolean {
        return try {
            // 方法一：直接打开微信主界面
            val intent = Intent()
            intent.setAction(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            
            // 微信的包名和启动Activity
            val componentName = ComponentName("com.tencent.mm", 
                "com.tencent.mm.ui.LauncherUI")
            intent.setComponent(componentName)
            intent.putExtra("LauncherUI.From.Scaner.Shortcut", true)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            
            try {
                startActivity(intent)
                Toast.makeText(this, "正在打开微信...", Toast.LENGTH_SHORT).show()
                return true
            } catch (e1: Exception) {
                // 方法一失败，尝试方法二
                e1.printStackTrace()
            }
            
            // 方法二：尝试使用URI方式
            val intentUri = Intent(Intent.ACTION_VIEW, Uri.parse("weixin://scanqrcode"))
            intentUri.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            
            try {
                startActivity(intentUri)
                Toast.makeText(this, "正在打开微信扫一扫...", Toast.LENGTH_SHORT).show()
                return true
            } catch (e2: Exception) {
                // 方法二失败，尝试方法三
                e2.printStackTrace()
            }
            
            // 方法三：直接通过包名启动应用
            val launchIntent = packageManager.getLaunchIntentForPackage("com.tencent.mm")
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(launchIntent)
                Toast.makeText(this, "正在打开微信，请手动点击右上角"+"按钮并选择扫一扫",
                    Toast.LENGTH_LONG).show()
                return true
            }
            
            // 所有方法都失败
            Toast.makeText(this, getString(R.string.wechat_not_installed), Toast.LENGTH_SHORT).show()
            false
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, getString(R.string.wechat_launch_failed), Toast.LENGTH_SHORT).show()
            false
        }
    }

    /**
     * 检查微信是否安装
     */
    private fun Context.isWeChatInstalled(): Boolean {
        val packageManager = this.packageManager
        val packageInfoList = packageManager.getInstalledPackages(0)
        
        for (packageInfo in packageInfoList) {
            if (packageInfo.packageName == "com.tencent.mm") {
                return true
            }
        }
        return false
    }
    
    /**
     * 打开邮箱应用
     */
    private fun openMail() {
        try {
            // 直接构造mailto URI，不对邮箱地址进行编码
            val uri = Uri.parse("mailto:$EMAIL_ADDRESS")
            
            // 构造邮件发送Intent
            val intent = Intent(Intent.ACTION_SENDTO, uri)
            intent.putExtra(Intent.EXTRA_SUBJECT, "问题反馈")
            intent.putExtra(Intent.EXTRA_TEXT, "请在此描述您遇到的问题...")
            
            // 创建选择器并启动
            val chooserIntent = Intent.createChooser(intent, "请选择邮箱应用")
            startActivity(chooserIntent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "未找到邮箱应用", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * 发起添加群流程。群号：笔记反馈群(1040268750) 的 key 为： lDqPLTgVcCx5ClpA_1ygWPnLbQA2Xqh9
     * 调用 joinQQGroup(lDqPLTgVcCx5ClpA_1ygWPnLbQA2Xqh9) 即可发起手Q客户端申请加群 笔记反馈群(1040268750)
     * @param key 由QQ群管理员提供的key
     * @return 返回true表示呼起手Q成功，返回false表示呼起失败
     */
    private fun joinQQGroup(key: String): Boolean {
        val intent = Intent()
        intent.data = Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D$key")
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面
        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return try {
            startActivity(intent)
            true
        } catch (e: Exception) {
            // 未安装手Q或安装的版本不支持
            false
        }
    }

    /**
     * 显示支持作者对话框
     */
    private fun showRateSupportDialog() {
        val dialog = Dialog(requireContext())
        val dialogBinding = DialogRateSupportBinding.inflate(layoutInflater)
        
        dialog.setContentView(dialogBinding.root)
        dialog.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            
            // 设置对话框宽度为屏幕宽度的85%
            val displayMetrics = resources.displayMetrics
            val width = (displayMetrics.widthPixels * 0.85).toInt()
            
            // 设置对话框布局参数
            val layoutParams = attributes
            layoutParams.width = width
            attributes = layoutParams
        }
        
        // 设置关闭按钮点击事件
        dialogBinding.btnClose.setOnClickListener {
            dialog.dismiss()
        }
        
        // 设置五星好评按钮点击事件
        dialogBinding.btnFiveStarRate.setOnClickListener {
            // 跳转到应用宝
            jumpToYingyongbao()
            dialog.dismiss()
        }
        
        // 设置打赏作者按钮点击事件
        dialogBinding.btnSupportAuthor.setOnClickListener {
            // 显示确认对话框
            showDonateConfirmDialog()
            dialog.dismiss()
        }
        
        dialog.show()
    }
    
    /**
     * 显示捐赠确认对话框
     */
    private fun showDonateConfirmDialog() {
        val confirmDialog = Dialog(requireContext())
        confirmDialog.setContentView(R.layout.dialog_confirm)
        confirmDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        
        // 设置对话框宽度为屏幕宽度的85%
        val displayMetrics = resources.displayMetrics
        val width = (displayMetrics.widthPixels * 0.85).toInt()
        confirmDialog.window?.let {
            val layoutParams = it.attributes
            layoutParams.width = width
            it.attributes = layoutParams
        }
        
        // 设置对话框内容
        val tvTitle = confirmDialog.findViewById<TextView>(R.id.tvTitle)
        val tvMessage = confirmDialog.findViewById<TextView>(R.id.tvMessage)
        val btnCancel = confirmDialog.findViewById<Button>(R.id.btnCancel)
        val btnConfirm = confirmDialog.findViewById<Button>(R.id.btnConfirm)
        
        tvTitle.text = "支付宝捐赠"
        tvMessage.text = "确认跳转到支付宝？"
        
        btnCancel.setOnClickListener {
            confirmDialog.dismiss()
        }
        
        btnConfirm.setOnClickListener {
            val alipayPackageName = "com.eg.android.AlipayGphone"
            
            if (RomUtils.checkApkExist(requireContext(), alipayPackageName)) {
                donateWithAlipay()
            } else {
                Toast.makeText(requireContext(), "本机未安装支付宝", Toast.LENGTH_SHORT).show()
            }
            confirmDialog.dismiss()
        }
        
        confirmDialog.show()
    }
    
    /**
     * 使用支付宝进行捐赠
     */
    private fun donateWithAlipay() {
        // 支付宝付款码
        val urlCode = "fkx17372v8km7by2niomj08"
        val intentFullUrl = """
            intent://platformapi/startapp?
            saId=10000007&
            clientVersion=3.7.0.0718&
            qrcode=https%3A%2F%2Fqr.alipay.com%2F$urlCode%3F_s%3Dweb-other&
            _t=1472443966571#Intent;
            scheme=alipayqr;
            package=com.eg.android.AlipayGphone;
            end
        """.trimIndent().replace("\n", "") // 移除换行符

        try {
            val intent = Intent.parseUri(intentFullUrl, Intent.URI_INTENT_SCHEME).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            requireContext().startActivity(intent)
            Toast.makeText(requireContext(), "正在打开支付宝...", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "跳转支付宝失败: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * 跳转到应用宝
     */
    private fun jumpToYingyongbao() {
        try {
            val packageName = requireContext().packageName
            
            // 尝试打开应用宝中本应用的页面
            val uri = Uri.parse("market://details?id=$packageName")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.tencent.android.qqdownloader") // 指定应用宝包名
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            // 检查是否有应用可以处理该意图（是否安装了应用宝）
            if (intent.resolveActivity(requireContext().packageManager) != null) {
                startActivity(intent)
                Toast.makeText(requireContext(), "正在前往应用宝...", Toast.LENGTH_SHORT).show()
            } else {
                // 如果没有安装应用宝，尝试打开其他应用市场
                val marketIntent = Intent(Intent.ACTION_VIEW, uri)
                marketIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                
                try {
                    startActivity(marketIntent)
                    Toast.makeText(requireContext(), "正在前往应用市场...", Toast.LENGTH_SHORT).show()
                } catch (e: ActivityNotFoundException) {
                    // 如果没有任何应用市场，打开应用宝网页版
                    val webUri = Uri.parse("https://sj.qq.com/myapp/detail.htm?apkName=$packageName")
                    val webIntent = Intent(Intent.ACTION_VIEW, webUri)
                    webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    try {
                        startActivity(webIntent)
                        Toast.makeText(requireContext(), "正在前往应用宝网页版...", Toast.LENGTH_SHORT).show()
                    } catch (e2: Exception) {
                        Toast.makeText(requireContext(), "无法打开应用宝，请检查网络或是否安装浏览器", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "跳转应用宝失败，请稍后再试", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 