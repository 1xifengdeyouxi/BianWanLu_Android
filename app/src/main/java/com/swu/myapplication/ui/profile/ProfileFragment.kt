package com.swu.myapplication.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.swu.myapplication.R
import com.swu.myapplication.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

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
            showFeatureNotImplemented("问题反馈")
        }

        binding.btnAboutUs.setOnClickListener {
            showFeatureNotImplemented("关于我们")
        }

        binding.btnRate.setOnClickListener {
            showFeatureNotImplemented("给个好评")
        }
    }

    private fun showFeatureNotImplemented(featureName: String) {
        Toast.makeText(requireContext(), "$featureName 功能即将上线", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 