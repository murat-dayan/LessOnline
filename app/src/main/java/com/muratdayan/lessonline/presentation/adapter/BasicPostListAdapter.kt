package com.muratdayan.lessonline.presentation.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.auth.User
import com.muratdayan.lessonline.databinding.BasicPostItemBinding
import com.muratdayan.lessonline.domain.model.firebasemodels.Post

class BasicPostListAdapter : ListAdapter<Post,BasicPostListAdapter.UserViewHolder>(UserDiffCallback()) {

    class UserViewHolder(private val binding:BasicPostItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(post: Post){
            val uri = Uri.parse(post.photoUri)
            Glide.with(binding.ivBasicPostPhoto.context)
                .load(uri)
                .into(binding.ivBasicPostPhoto)
        }
    }

    class UserDiffCallback: DiffUtil.ItemCallback<Post>(){
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.postId == newItem.postId
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = BasicPostItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}