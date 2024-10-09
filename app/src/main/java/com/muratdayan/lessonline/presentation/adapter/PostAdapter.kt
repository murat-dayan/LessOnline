    package com.muratdayan.lessonline.presentation.adapter

    import android.net.Uri
    import android.text.Layout
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import androidx.recyclerview.widget.DiffUtil
    import androidx.recyclerview.widget.RecyclerView
    import com.bumptech.glide.Glide
    import com.muratdayan.lessonline.R
    import com.muratdayan.lessonline.databinding.PostItemBinding
    import com.muratdayan.lessonline.domain.model.firebasemodels.Post

    class PostAdapter(
        private var postList: List<Post>
    ): RecyclerView.Adapter<PostAdapter.PostRowHolder>() {

        inner class PostRowHolder(view: View): RecyclerView.ViewHolder(view){
            val binding = PostItemBinding.bind(view)

            fun bind(post: Post){
                binding.tvPostComment.text = post.comment
                binding.tvUsername.text = post.username
                val uri = Uri.parse(post.photoUri)
                Glide.with(binding.ivPostPhoto.context)
                    .load(uri)
                    .into(binding.ivPostPhoto)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostRowHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.post_item,parent,false)
            return PostRowHolder(view)
        }

        override fun getItemCount(): Int {
            return postList.size
        }

        override fun onBindViewHolder(holder: PostRowHolder, position: Int) {
            val post = postList[position]
            holder.bind(post)
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