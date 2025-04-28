package com.swu.myapplication.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.swu.myapplication.R
import com.swu.myapplication.databinding.FragmentGameListBinding

class GameListFragment : Fragment() {
    private var _binding: FragmentGameListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        // 返回按钮点击事件
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // 2048游戏点击事件
        binding.game2048Layout.setOnClickListener {
            findNavController().navigate(R.id.action_gameListFragment_to_game2048Fragment)
        }

        // 滑动拼图点击事件
        binding.puzzleGameLayout.setOnClickListener {
            findNavController().navigate(R.id.action_gameListFragment_to_slidingPuzzleFragment)
        }

        // 敬请期待点击事件
        binding.comingSoonLayout.setOnClickListener {
            Toast.makeText(requireContext(), "更多游戏敬请期待", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 