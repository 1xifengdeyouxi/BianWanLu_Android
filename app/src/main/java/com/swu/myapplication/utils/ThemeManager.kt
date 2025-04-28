package com.swu.myapplication.utils

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate

/**
 * 主题管理器，用于处理应用深色模式的设置
 */
class ThemeManager(private val context: Context) {
    
    companion object {
        // 主题类型常量
        const val MODE_FOLLOW_SYSTEM = 0  // 跟随系统
        const val MODE_LIGHT = 1          // 普通模式
        const val MODE_DARK = 2           // 深色模式
        
        // SharedPreferences 键名
        private const val PREF_NAME = "theme_prefs"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_LAST_MANUAL_MODE = "last_manual_mode" // 记住上次手动设置的模式
        
        @Volatile
        private var INSTANCE: ThemeManager? = null
        
        fun getInstance(context: Context): ThemeManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ThemeManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
    
    /**
     * 获取当前保存的主题模式
     */
    fun getThemeMode(): Int {
        return prefs.getInt(KEY_THEME_MODE, MODE_FOLLOW_SYSTEM)
    }
    
    /**
     * 保存并应用主题模式
     */
    fun setThemeMode(mode: Int) {
        // 保存设置
        prefs.edit().putInt(KEY_THEME_MODE, mode).apply()
        
        // 如果是手动模式，记住这个选择
        if (mode != MODE_FOLLOW_SYSTEM) {
            prefs.edit().putInt(KEY_LAST_MANUAL_MODE, mode).apply()
        }
        
        // 应用设置
        when (mode) {
            MODE_FOLLOW_SYSTEM -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                } else {
                    // Android 10以下默认使用浅色模式
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
            MODE_LIGHT -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            MODE_DARK -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
    }
    
    /**
     * 获取上次手动选择的主题模式
     */
    fun getLastManualMode(): Int {
        return prefs.getInt(KEY_LAST_MANUAL_MODE, MODE_LIGHT) // 默认是浅色模式
    }
    
    /**
     * 检查是否是深色模式
     */
    fun isNightMode(): Boolean {
        return when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_YES -> true
            AppCompatDelegate.MODE_NIGHT_NO -> false
            else -> false // 默认情况
        }
    }
    
    /**
     * 检查是否跟随系统
     */
    fun isFollowSystem(): Boolean {
        return getThemeMode() == MODE_FOLLOW_SYSTEM
    }
} 