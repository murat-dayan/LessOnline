package com.muratdayan.lessonline.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.muratdayan.lessonline.databinding.ItemAnswerNotificationBinding
import com.muratdayan.lessonline.domain.model.firebasemodels.NotificationModel

class AnswerNotificationsAdapter(
    private val onItemClicked: (String) -> Unit
):
    ListAdapter<NotificationModel, AnswerNotificationsAdapter.AnswerNotificationsViewHolder>(NotificationDiffCallback()) {

    class AnswerNotificationsViewHolder(private val binding: ItemAnswerNotificationBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(notification: NotificationModel){
            binding.tvUsername.text = notification.commenterName

        }
    }

    class NotificationDiffCallback : DiffUtil.ItemCallback<NotificationModel>(){
        override fun areItemsTheSame(
            oldItem: NotificationModel,
            newItem: NotificationModel
        ): Boolean {
            return oldItem.timestamp == newItem.timestamp
        }

        override fun areContentsTheSame(
            oldItem: NotificationModel,
            newItem: NotificationModel
        ): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerNotificationsViewHolder {
        val binding = ItemAnswerNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AnswerNotificationsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnswerNotificationsViewHolder, position: Int) {
        val notification = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(notification.postId)
        }
        holder.bind(notification)
    }
}