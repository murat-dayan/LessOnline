package com.muratdayan.lessonline.presentation.features.main.answernotifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.muratdayan.core.presentation.BaseFragment
import com.muratdayan.lessonline.R
import com.muratdayan.lessonline.databinding.FragmentAnswerNotificationsBinding
import com.muratdayan.lessonline.presentation.adapter.AnswerNotificationsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AnswersNotificationsFragment : BaseFragment() {

    private var _binding: FragmentAnswerNotificationsBinding? = null
    private val binding get() = _binding!!

    private val answerNotificationsViewModel: AnswerNotificationsViewModel by viewModels()
    private lateinit var likeNotificationsAdapter: AnswerNotificationsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnswerNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        likeNotificationsAdapter = AnswerNotificationsAdapter() { postId ->
            val action =
                AnswersNotificationsFragmentDirections.actionAnswernotificationsfragmentToPostDetailFragment(
                    postId
                )
            findNavController().navigate(action)
        }
        binding.rvNotifications.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = likeNotificationsAdapter
        }

        lifecycleScope.launch {
            answerNotificationsViewModel.uiState.collectLatest { uiState ->
                when (uiState) {
                    is AnswerNotificationsViewModel.NotificationUIState.Loading -> {
                        showLoading()
                    }

                    is AnswerNotificationsViewModel.NotificationUIState.Success -> {
                        hideLoading()
                        likeNotificationsAdapter.submitList(uiState.notifications)

                        if (uiState.notifications.isEmpty()) {
                            binding.evAnswerNotifications.visibility = View.VISIBLE
                        } else {
                            binding.evAnswerNotifications.visibility = View.GONE
                        }
                    }

                    is AnswerNotificationsViewModel.NotificationUIState.Error -> {
                        hideLoading()
                    }

                    is AnswerNotificationsViewModel.NotificationUIState.Empty -> {
                        hideLoading()
                    }
                }
            }
        }

        answerNotificationsViewModel.loadNotifications()
    }

    override fun onResume() {
        super.onResume()
        clearBadge()
    }

    private fun clearBadge(){
        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.removeBadge(R.id.answernotificationsfragment)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        hideLoading()
    }
}