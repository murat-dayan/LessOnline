package com.muratdayan.chatbot.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.muratdayan.chatbot.domain.model.ChatMessage
import com.muratdayan.chatbot.databinding.ItemBotMessageBinding
import com.muratdayan.chatbot.databinding.ItemUserMessageBinding

class ChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.message == newItem.message
        }

        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    inner class ChatViewHolder(private val binding: ItemUserMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: ChatMessage) {
            binding.messageTextView.text = message.message
        }
    }

    inner class BotViewHolder(private val binding: ItemBotMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: ChatMessage) {
            binding.messageTextView.text = message.message
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return if (viewType == 1) {
            val binding = ItemUserMessageBinding.inflate(layoutInflater, parent, false)
            ChatViewHolder(binding)
        } else {
            val binding = ItemBotMessageBinding.inflate(layoutInflater, parent, false)
            BotViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = differ.currentList[position]
        if (holder is ChatViewHolder) {
            holder.bind(message)
        } else if (holder is BotViewHolder) {
            holder.bind(message)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (differ.currentList[position].isUser) 1 else 0 // 1: kullanıcı, 0: bot
    }

    override fun getItemCount(): Int = differ.currentList.size

    // Verileri güncellemek için yeni bir metot ekle
    fun submitList(list: List<ChatMessage>) {
        differ.submitList(list)
    }
}