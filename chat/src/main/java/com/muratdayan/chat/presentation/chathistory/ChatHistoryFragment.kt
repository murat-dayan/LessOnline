package com.muratdayan.chat.presentation.chathistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.muratdayan.chat.databinding.FragmentChatHistoryBinding
import com.muratdayan.chat.presentation.adapter.ChatHistoryAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatHistoryFragment : Fragment() {


    private var _binding: FragmentChatHistoryBinding? = null
    private val binding get() = _binding!!

    private val chatHistoryViewModel: ChatHistoryViewModel by viewModels()
    private lateinit var chatHistoryAdapter: ChatHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentChatHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatHistoryAdapter = ChatHistoryAdapter(emptyList()){}


        lifecycleScope.launch {
            chatHistoryViewModel.userState.collect { users ->
                chatHistoryAdapter = ChatHistoryAdapter(users){
                    val action = ChatHistoryFragmentDirections.actionChatHistoryFragmentToChatFragment(it)
                    findNavController().navigate(action)
                }
                binding.rvChatHistory.apply {
                    adapter = chatHistoryAdapter
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(requireContext())
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}