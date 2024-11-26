package com.muratdayan.chat.presentation.chathistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.muratdayan.chat.databinding.FragmentChatHistoryBinding
import com.muratdayan.chat.presentation.adapter.ChatHistoryAdapter
import com.muratdayan.core.presentation.BaseFragment
import com.muratdayan.core.util.Result
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatHistoryFragment : BaseFragment() {


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

        chatHistoryAdapter = ChatHistoryAdapter(mutableListOf()){
            val action = ChatHistoryFragmentDirections.actionChatHistoryFragmentToChatFragment(it)
            findNavController().navigate(action)
        }

        binding.rvChatHistory.apply {
            adapter = chatHistoryAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                /*val position = viewHolder.adapterPosition
                val chatId = chatHistoryAdapter.getItem(position).id
                    Log.d("ChatHistoryFragment", "Chat ID: $chatId")
                chatHistoryViewModel.deleteChat(chatId)
                chatHistoryAdapter.removeItem(position)*/
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvChatHistory)


        lifecycleScope.launch {
            chatHistoryViewModel.usersChattedList.collect { result ->
                when (result) {
                    is Result.Error -> {
                        hideLoading()
                    }
                    Result.Idle -> {}
                    Result.Loading -> {
                        showLoading()
                    }
                    is Result.Success -> {
                        hideLoading()
                        chatHistoryAdapter.updateList(result.data)

                        if (result.data.isEmpty()){
                            binding.evChatHistory.visibility = View.VISIBLE
                        }else{
                            binding.evChatHistory.visibility = View.GONE
                        }
                    }

                }


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