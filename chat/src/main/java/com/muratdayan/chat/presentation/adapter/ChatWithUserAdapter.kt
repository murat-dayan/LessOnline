package com.muratdayan.chat.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.muratdayan.chat.data.model.MessageModel
import com.muratdayan.chat.databinding.ItemReceiverMessageBinding
import com.muratdayan.chat.databinding.ItemSenderMessageBinding

class ChatWithUserAdapter(
    private val itemViewTypeCheck: (MessageModel) -> Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<MessageModel>() {
        override fun areItemsTheSame(oldItem: MessageModel, newItem: MessageModel): Boolean {
            return oldItem.timestamp == newItem.timestamp
        }

        override fun areContentsTheSame(oldItem: MessageModel, newItem: MessageModel): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    inner class SenderViewHolder(private val binding: ItemSenderMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: MessageModel) {
            binding.messageTextView.text = message.content
        }
    }

    inner class ReceiverViewHolder(private val binding: ItemReceiverMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: MessageModel) {
            binding.messageTextView.text = message.content
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return if (viewType == 1) {
            val binding = ItemSenderMessageBinding.inflate(layoutInflater, parent, false)
            SenderViewHolder(binding)
        } else {
            val binding = ItemReceiverMessageBinding.inflate(layoutInflater, parent, false)
            ReceiverViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = differ.currentList[position]
        if (holder is SenderViewHolder) {
            holder.bind(message)
        } else if (holder is ReceiverViewHolder) {
            holder.bind(message)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = differ.currentList[position]
        return itemViewTypeCheck(message)
    }

    override fun getItemCount(): Int = differ.currentList.size

    // Verileri güncellemek için yeni bir metot ekle
    fun submitList(list: List<MessageModel>) {
        differ.submitList(list)
    }
}