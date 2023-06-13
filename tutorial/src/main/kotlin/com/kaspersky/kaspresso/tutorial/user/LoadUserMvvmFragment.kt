package com.kaspersky.kaspresso.tutorial.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.kaspersky.kaspresso.tutorial.databinding.FragmentLoadUserBinding
import kotlinx.coroutines.launch

class LoadUserMvvmFragment : Fragment() {

    private var _binding: FragmentLoadUserBinding? = null
    private val binding: FragmentLoadUserBinding
        get() = _binding ?: throw IllegalStateException("FragmentLoadUserBinding is null")

    private lateinit var viewModel: LoadUserViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentLoadUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val forScreenshotTest = requireArguments().getBoolean(EXTRA_FOR_SCREENSHOTS, false)
        if (!forScreenshotTest) {
            viewModel = ViewModelProvider(this)[LoadUserViewModel::class.java]
            binding.loadingButton.setOnClickListener {
                viewModel.loadUser()
            }
        }
        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        is State.Content -> {
                            binding.progressBarLoading.isVisible = false
                            binding.loadingButton.isEnabled = true
                            binding.error.isVisible = false
                            binding.username.isVisible = true

                            val user = state.user
                            binding.username.text = "${user.name} ${user.lastName}"
                        }
                        State.Error -> {
                            binding.progressBarLoading.isVisible = false
                            binding.loadingButton.isEnabled = true
                            binding.error.isVisible = true
                            binding.username.isVisible = false
                        }
                        State.Loading -> {
                            binding.progressBarLoading.isVisible = true
                            binding.loadingButton.isEnabled = false
                            binding.error.isVisible = false
                            binding.username.isVisible = false
                        }
                        State.Initial -> {}
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        private const val EXTRA_FOR_SCREENSHOTS = "for_screenshots"

        fun newInstance(): LoadUserMvvmFragment = LoadUserMvvmFragment().apply {
            arguments = Bundle().apply {
                putBoolean(EXTRA_FOR_SCREENSHOTS, false)
            }
        }

        fun newTestInstance(mockedViewModel: LoadUserViewModel): LoadUserMvvmFragment = LoadUserMvvmFragment().apply {
            arguments = Bundle().apply {
                putBoolean(EXTRA_FOR_SCREENSHOTS, true)
            }
            viewModel = mockedViewModel
        }
    }
}
