package com.swu.myapplication

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.swu.myapplication.databinding.ActivityMainBinding
import com.swu.myapplication.utils.ThemeManager
import android.view.View
import android.view.WindowManager
import android.os.Build
import android.view.WindowInsets

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var themeManager: ThemeManager

    override fun onCreate(savedInstanceState: Bundle?) {
        // 在界面创建前应用主题
        themeManager = ThemeManager.getInstance(this)
        
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 设置状态栏
        setupStatusBar()

        // 正确获取NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // 设置底部导航
        val navView: BottomNavigationView = binding.bottomNavigation
        navView.setupWithNavController(navController)
        
        // 隐藏ActionBar
        supportActionBar?.hide()
        
        // 添加导航监听器，用于控制底部导航栏的显示和隐藏
        navController.addOnDestinationChangedListener { _, destination, _ ->
            updateBottomNavigationVisibility(destination)
        }
    }
    
    private fun setupStatusBar() {
        // 设置状态栏颜色
        window.statusBarColor = resources.getColor(R.color.primary, theme)
        
        // 如果是浅色状态栏，设置深色图标
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val isLightTheme = resources.configuration.uiMode and 
                    Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO
            window.decorView.systemUiVisibility = if (isLightTheme) {
                window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
        }
    }
    
    /**
     * 根据导航目的地控制底部导航栏的显示和隐藏
     */
    private fun updateBottomNavigationVisibility(destination: NavDestination) {
        // 在以下Fragment中显示底部导航栏
        val showBottomNavInFragments = setOf(
            R.id.notesFragment,
            R.id.nav_todos,
            R.id.nav_profile
        )

        // 在以下Fragment中隐藏底部导航栏
        val hideBottomNavInFragments = setOf(
            R.id.timerFragment,
            R.id.gameListFragment,
            R.id.darkModeFragment,
            R.id.editFragment,
            R.id.notebookListFragment,
            R.id.game2048Fragment,
            R.id.slidingPuzzleFragment,
            R.id.runningTimerFragment,
            R.id.createTimerFragment,
            R.id.durationPickerFragment,
            R.id.atmospherePickerFragment,
            R.id.notebookManagerMenuFragment
        )
        
        // 更新底部导航栏的可见性
        if (destination.id in showBottomNavInFragments) {
            binding.bottomNavigation.visibility = View.VISIBLE
        } else if (destination.id in hideBottomNavInFragments) {
            binding.bottomNavigation.visibility = View.GONE
        }
    }
    
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        
        // 如果设置了跟随系统且系统深色模式变化
        if (themeManager.isFollowSystem()) {
            val currentNightMode = newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK
            when (currentNightMode) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    // 系统切换到夜间模式
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    // 重新创建活动以应用新主题
                    recreate()
                }
                Configuration.UI_MODE_NIGHT_NO -> {
                    // 系统切换到浅色模式
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    // 重新创建活动以应用新主题
                    recreate()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
} 