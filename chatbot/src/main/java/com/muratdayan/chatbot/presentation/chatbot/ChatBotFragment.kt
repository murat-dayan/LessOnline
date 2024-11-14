package com.muratdayan.chatbot.presentation.chatbot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.muratdayan.chatbot.databinding.FragmentChatBotBinding
import com.muratdayan.chatbot.domain.model.ChatMessage
import com.muratdayan.chatbot.presentation.adapter.ChatAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatBotFragment : Fragment() {

    private var _binding : FragmentChatBotBinding? = null
    private val binding get() = _binding!!

    private val chatBotViewModel: ChatBotViewModel by viewModels()
    private lateinit var chatAdapter: ChatAdapter
    private val messages = mutableListOf<ChatMessage>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentChatBotBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatAdapter = ChatAdapter()
        binding.rvMessages.adapter = chatAdapter
        binding.rvMessages.layoutManager = LinearLayoutManager(requireContext())

        binding.btnSend.setOnClickListener {
            val userMessage = binding.etMessage.text.toString()
            if(userMessage.isNotEmpty()){
                messages.add(ChatMessage(userMessage, isUser = true))
                chatAdapter.submitList(messages.toList())
                chatBotViewModel.sendMessageToChatBot(userMessage)
                binding.etMessage.text.clear()
            }
        }

        chatBotViewModel.chatResponse.observe(viewLifecycleOwner){response ->
            val botMessage = ChatMessage(response, isUser = false)
            messages.add(botMessage)
            chatAdapter.submitList(messages.toList())
            binding.rvMessages.scrollToPosition(messages.size-1)
        }

        chatBotViewModel.currentUser.observe(viewLifecycleOwner){user->
            user?.let {
                binding.tvUserGreeting.text = "Merhaba ${it.displayName}"
            } ?: run {
                binding.tvUserGreeting.text = "User Not Found"
            }
        }

        binding.ibtnBack.setOnClickListener {
            findNavController().navigateUp()
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}