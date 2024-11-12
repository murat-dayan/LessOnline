package com.muratdayan.chat.presentation.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.muratdayan.chat.databinding.FragmentChatBinding
import com.muratdayan.chat.presentation.adapter.ChatWithUserAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val chatViewModel : ChatViewModel by viewModels()
    private lateinit var chatWithUserAdapter: ChatWithUserAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentChatBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = ChatFragmentArgs.fromBundle(requireArguments())

        val userId = arguments?.getString("userId") ?: args.targetId
        if (userId.isNotEmpty()){
            chatViewModel.initiateChat(targetUserId = userId)
            //chatViewModel.chatIdState.value?.let { chatViewModel.observeMessages(it) }
            chatWithUserAdapter = ChatWithUserAdapter(){messageModel ->
                chatViewModel.checkViewType(messageModel.senderId)
            }
            binding.rvMessages.apply {
                adapter = chatWithUserAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
            binding.btnSend.setOnClickListener {
                val messageContent = binding.etMessage.text.toString()
                messageContent.let {
                    chatViewModel.sendMessage(
                        chatId = chatViewModel.chatIdState.value ?: "",
                        messageContent = it,
                        targetUserId = userId
                    )
                }
            }
        }

        lifecycleScope.launch {
            chatViewModel.sendMessageStatus.collect { sendResult ->
                if (sendResult) {
                    binding.etMessage.text.clear()
                    chatViewModel.chatIdState.value?.let { chatViewModel.observeMessages(it) }
                }
            }
        }

        lifecycleScope.launch {
            chatViewModel.messagesState.collect{messages->
                chatWithUserAdapter.submitList(messages)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}