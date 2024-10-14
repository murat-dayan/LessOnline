package com.muratdayan.lessonline.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.muratdayan.lessonline.databinding.AnswerItemBinding
import com.muratdayan.lessonline.domain.model.firebasemodels.Answer

class AnswerAdapter : ListAdapter<Answer,AnswerAdapter.AnswerViewHolder>(AnswerDiffCallback()) {

    class AnswerViewHolder(private val binding: AnswerItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(answer: Answer){
            binding.tvAnswer.text = answer.answer
            binding.tvUsername.text = answer.username
        }
    }

    class AnswerDiffCallback: DiffUtil.ItemCallback<Answer>(){
        override fun areItemsTheSame(oldItem: Answer, newItem: Answer): Boolean {
            return oldItem.answerId == newItem.answerId
        }

        override fun areContentsTheSame(oldItem: Answer, newItem: Answer): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerViewHolder {
        val binding = AnswerItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AnswerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnswerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}