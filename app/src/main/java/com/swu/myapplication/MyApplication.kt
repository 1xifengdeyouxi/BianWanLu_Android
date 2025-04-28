package com.swu.myapplication

import android.app.Application
import com.swu.myapplication.utils.ThemeManager

/**
 * 自定义Application类，用于应用初始化操作
 */
class MyApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // 初始化主题设置
        val themeManager = ThemeManager.getInstance(this)
        themeManager.setThemeMode(themeManager.getThemeMode()) // 应用已保存的主题设置
    }
} 