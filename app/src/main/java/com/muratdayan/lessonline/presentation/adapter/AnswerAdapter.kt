package com.muratdayan.lessonline.presentation.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.muratdayan.lessonline.databinding.AnswerItemBinding
import com.muratdayan.lessonline.domain.model.firebasemodels.Answer
import com.muratdayan.lessonline.presentation.util.UserRole
import java.util.Date

class AnswerAdapter(
    private val isPostOwner: Boolean,
    private val currentUserId: String
) : ListAdapter<Answer, AnswerAdapter.AnswerViewHolder>(AnswerDiffCallback()) {


    class AnswerViewHolder(private val binding: AnswerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(answer: Answer, isPostOwner: Boolean, currentUserId: String) {
            binding.tvAnswer.text = answer.answer
            binding.tvUsername.text = answer.username
            val savedTimeStamp = answer.timestamp as? Long ?: return
            val date = Date(savedTimeStamp)
            val timeAgo = getTimeAgo(date)
            binding.tvTimestamp.text = timeAgo


            binding.ibtnDiscuss.visibility =
                if (isPostOwner && answer.userId != currentUserId) View.VISIBLE else View.GONE


        }

        private fun getTimeAgo(date: Date): String {
            val diff = System.currentTimeMillis() - date.time

            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24
            return when {
                seconds < 60 -> "just now"
                minutes < 60 -> "$minutes minutes ago"
                hours < 24 -> "$hours hours ago"
                days < 7 -> "$days days ago"
                else -> "$days days ago"
            }
        }
    }

    class AnswerDiffCallback : DiffUtil.ItemCallback<Answer>() {
        override fun areItemsTheSame(oldItem: Answer, newItem: Answer): Boolean {
            return oldItem.answerId == newItem.answerId
        }

        override fun areContentsTheSame(oldItem: Answer, newItem: Answer): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerViewHolder {
        val binding = AnswerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AnswerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnswerViewHolder, position: Int) {
        holder.bind(getItem(position), isPostOwner ,currentUserId)
    }
}