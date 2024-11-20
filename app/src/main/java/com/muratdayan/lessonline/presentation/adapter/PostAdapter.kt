    package com.muratdayan.lessonline.presentation.adapter

    import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.muratdayan.lessonline.R
import com.muratdayan.lessonline.databinding.ItemPostBinding
import com.muratdayan.lessonline.domain.model.firebasemodels.Post

    class PostAdapter(
        private var postList: List<Post>,
        private val onAnswerIconClick: (String,Int)->Unit,
        private val onLikeIconClick: (post:Post,position:Int)->Unit,
        private val onProfilePhotoClick:(String)->Unit,
        private val onBookmarkIconClick: (String,ImageButton) -> Unit,
        private val onPostPhotoClick:(String)->Unit,
    ): RecyclerView.Adapter<PostAdapter.PostRowHolder>() {

        inner class PostRowHolder(view: View): RecyclerView.ViewHolder(view){
            val binding = ItemPostBinding.bind(view)

            fun bind(post: Post, position: Int){
                binding.tvPostComment.text = post.comment
                binding.tvUsername.text = post.username
                if (post.photoUri.isNotEmpty()){
                    Glide.with(binding.ivPostPhoto.context)
                        .load(post.photoUri)
                        .into(binding.ivPostPhoto)
                }else{
                    binding.ivPostPhoto.setImageResource(com.muratdayan.core.R.drawable.ic_person_focused)
                }

                Glide.with(binding.ivUserPhoto.context)
                    .load(post.userPhoto)
                    .into(binding.ivUserPhoto)
                binding.ibtnAnswer.setOnClickListener {
                    onAnswerIconClick(post.postId,position)
                }
                binding.tvLikeCounts.text = post.likeCount.toString()
                binding.tvAnswersCount.text = post.answerCount.toString()
                binding.ibtnLike.setImageResource(
                    if (FirebaseAuth.getInstance().currentUser?.let { post.likedByUsers.contains(it.uid) } == true){
                        R.drawable.ic_like
                    }else{
                        R.drawable.ic_like_outline
                    }
                )
                binding.ibtnLike.setOnClickListener {
                    onLikeIconClick(post,adapterPosition)
                }
                binding.ivUserPhoto.setOnClickListener {
                    onProfilePhotoClick(post.userId)
                }
                binding.ibtnSave.setOnClickListener {
                    onBookmarkIconClick(post.postId,binding.ibtnSave)
                }
                binding.ibtnSave.setImageResource(
                    if (post.isBookmarked) R.drawable.ic_bookmark else R.drawable.ic_bookmark_outline
                )

                binding.ivPostPhoto.setOnClickListener {
                    onPostPhotoClick(post.photoUri)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostRowHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post,parent,false)
            return PostRowHolder(view)
        }

        override fun getItemCount(): Int {
            return postList.size
        }

        override fun onBindViewHolder(holder: PostRowHolder, position: Int) {
            val post = postList[position]
            holder.bind(post, position)
        }

        fun updatePostList(newPostList: List<Post>){
            val diffCallBack = PostDiffCallBack(postList,newPostList)
            val diffResult = DiffUtil.calculateDiff(diffCallBack)

            postList = newPostList

            diffResult.dispatchUpdatesTo(this)
        }

        class PostDiffCallBack(
            private val oldList: List<Post>,
            private val newList:List<Post>
        ): DiffUtil.Callback(){
            override fun getOldListSize(): Int {
                return oldList.size
            }

            override fun getNewListSize(): Int {
                return newList.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition].postId == newList[newItemPosition].postId
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition] == newList[newItemPosition]
            }

        }
    }