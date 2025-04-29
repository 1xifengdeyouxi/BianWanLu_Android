package com.swu.myapplication.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.swu.myapplication.ui.game.erLingSiBagame.ui.game.GameScreen
import com.swu.myapplication.ui.game.erLingSiBagame.ui.theme.Compose2048Theme
import com.swu.myapplication.databinding.FragmentGame2048Binding
import com.swu.myapplication.R

class Game2048Fragment : Fragment() {
    private var _binding: FragmentGame2048Binding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGame2048Binding.inflate(inflater, container, false)
        val view = binding.root
        
        // 设置返回按钮点击事件
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        
        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                // 使用Compose2048Theme主题，自动适应深色模式
                Compose2048Theme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colors.background
                    ) {
                        Box(
                            Modifier.padding(16.dp)
                        ) {
                            GameScreen()
                        }
                    }
                }
            }
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}