package com.muratdayan.chat.presentation.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.muratdayan.chat.databinding.FragmentChatBinding
import com.muratdayan.chat.presentation.adapter.ChatWithUserAdapter
import com.muratdayan.core.presentation.BaseFragment
import com.muratdayan.core.util.Result
import com.muratdayan.core.util.doIfIsEmptyAndReturn
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatFragment : BaseFragment() {

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

        val args :ChatFragmentArgs by navArgs()

        val userId = arguments?.getString("userId") ?: args.targetId!!

        if (userId.isNotEmpty()){
            chatViewModel.getReceiverName(userId)
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
                val isMessageEmpty = binding.etMessage.doIfIsEmptyAndReturn {
                    showToast("Message is empty",false)
                }

                isMessageEmpty.takeUnless { it }?.let {
                    val messageContent = binding.etMessage.text.toString().trim()
                    chatViewModel.sendMessage(
                        chatId = chatViewModel.chatIdState.value ?: "",
                        messageContent = messageContent,
                        targetUserId = userId
                    )
                }
            }
        }

        binding.ibtnBack.setOnClickListener {
            Navigation.findNavController(requireView()).navigateUp()
        }

        lifecycleScope.launch {
            chatViewModel.receiverName.collect{receiverName->
                binding.tvReceiverName.text = receiverName
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
            chatViewModel.messagesState.collect{result->
                when(result){
                    is Result.Error -> {
                        hideLoading()
                        showToast(result.exception.message.toString(),true)
                    }
                    Result.Idle -> {}
                    Result.Loading -> {
                        showLoading()
                    }
                    is Result.Success -> {
                        hideLoading()
                        chatWithUserAdapter.submitList(result.data)

                        if (result.data.isEmpty()){
                            binding.evChat.visibility = View.VISIBLE
                            binding.rvMessages.visibility = View.GONE
                        }else{
                            binding.evChat.visibility = View.GONE
                            binding.rvMessages.visibility = View.VISIBLE
                        }
                    }
                }


            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}