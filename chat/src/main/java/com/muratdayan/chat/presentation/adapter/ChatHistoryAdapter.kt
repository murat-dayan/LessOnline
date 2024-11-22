package com.muratdayan.chat.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.muratdayan.chat.data.model.ChatUserModel
import com.muratdayan.chat.databinding.ItemChatHistoryBinding

class ChatHistoryAdapter(
    private val ChatHistoryNamesList: List<ChatUserModel>,
    private val onItemClickListener: (String) -> Unit
) : RecyclerView.Adapter<ChatHistoryAdapter.ChatHistoryViewHolder>() {

    inner class ChatHistoryViewHolder(val binding: ItemChatHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user:ChatUserModel, onItemClickListener: (String) -> Unit) {
            binding.tvUsername.text = user.name
            binding.root.setOnClickListener {
                onItemClickListener(user.id)
            }
            user.photoUrl?.let { photoUrl ->
                Glide.with(binding.ivProfile.context)
                    .load(photoUrl)
                    .into(binding.ivProfile)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatHistoryViewHolder {
        val binding = ItemChatHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatHistoryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return  ChatHistoryNamesList.size
    }

    override fun onBindViewHolder(holder: ChatHistoryViewHolder, position: Int) {
        holder.bind(ChatHistoryNamesList[position], onItemClickListener)
    }
}