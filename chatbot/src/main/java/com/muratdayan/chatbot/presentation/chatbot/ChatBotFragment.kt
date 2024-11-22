package com.muratdayan.chatbot.presentation.chatbot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.muratdayan.chatbot.databinding.FragmentChatBotBinding
import com.muratdayan.chatbot.domain.model.ChatMessage
import com.muratdayan.chatbot.presentation.adapter.ChatAdapter
import com.muratdayan.core.presentation.BaseFragment
import com.muratdayan.core.util.doIfIsEmptyAndReturn
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatBotFragment : BaseFragment() {

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

        updateViewsVisibility()

        chatAdapter = ChatAdapter()
        binding.rvMessages.adapter = chatAdapter
        binding.rvMessages.layoutManager = LinearLayoutManager(requireContext())

        binding.btnSend.setOnClickListener {
            val isUserQuestionEmpty = binding.etMessage.doIfIsEmptyAndReturn {
                showToast("Your Question is empty",false)
            }

            isUserQuestionEmpty.takeUnless { it }?.let {
                val userMessage = binding.etMessage.text.toString().trim()
                messages.add(ChatMessage(userMessage, isUser = true))
                chatAdapter.submitList(messages.toList())
                chatBotViewModel.sendMessageToChatBot(userMessage)
                binding.etMessage.text.clear()
                updateViewsVisibility()
            }


        }

        chatBotViewModel.chatResponse.observe(viewLifecycleOwner){response ->
            val botMessage = ChatMessage(response, isUser = false)
            messages.add(botMessage)
            chatAdapter.submitList(messages.toList())
            binding.rvMessages.scrollToPosition(messages.size-1)
            updateViewsVisibility()
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

    private fun updateViewsVisibility() {
        if (messages.isEmpty()) {
            binding.rvMessages.visibility = View.GONE
            binding.evChatbot.visibility = View.VISIBLE
        } else {
            binding.rvMessages.visibility = View.VISIBLE
            binding.evChatbot.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}