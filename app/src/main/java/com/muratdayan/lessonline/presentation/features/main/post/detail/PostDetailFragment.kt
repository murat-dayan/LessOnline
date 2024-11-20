package com.muratdayan.lessonline.presentation.features.main.post.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.muratdayan.core.presentation.BaseFragment
import com.muratdayan.core.util.goBack
import com.muratdayan.lessonline.R
import com.muratdayan.lessonline.databinding.FragmentPostDetailBinding
import com.muratdayan.lessonline.domain.model.firebasemodels.Post
import com.muratdayan.lessonline.presentation.features.main.bottomsheets.answers.AnswersBottomSheetFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostDetailFragment : BaseFragment() {

    private var _binding: FragmentPostDetailBinding?=null
    private val binding get() = _binding!!

    private val postDetailViewModel:PostDetailViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentPostDetailBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args : PostDetailFragmentArgs by navArgs()
        val postId = args.postId

        lifecycleScope.launch {
            postDetailViewModel.post.collectLatest { post->
                if (post != null){
                    binding.incPostItem.tvPostComment.text = post.comment
                    binding.incPostItem.tvUsername.text = post.username
                    Glide.with(requireContext())
                        .load(post.photoUri)
                        .into(binding.incPostItem.ivPostPhoto)
                    Glide.with(requireContext())
                        .load(post.userPhoto)
                        .into(binding.incPostItem.ivUserPhoto)
                    if (post.isBookmarked){
                        binding.incPostItem.ibtnSave.setImageResource(R.drawable.ic_bookmark)
                    }else{
                        binding.incPostItem.ibtnSave.setImageResource(R.drawable.ic_bookmark_outline)
                    }
                    binding.incPostItem.tvLikeCounts.text = post.likeCount.toString()
                    binding.incPostItem.tvAnswersCount.text = post.answerCount.toString()
                    binding.incPostItem.ibtnLike.setOnClickListener {
                        toggleLike(post)
                    }
                    binding.incPostItem.ibtnAnswer.setOnClickListener {
                        val answersBottomSheet = AnswersBottomSheetFragment(postId){isAnswerChanged->
                            if (isAnswerChanged){
                                postDetailViewModel.getPostById(postId)
                            }
                        }
                        answersBottomSheet.show(childFragmentManager,answersBottomSheet.tag)
                    }
                }
            }
        }

        binding.ibtnBack.setOnClickListener {
            Navigation.goBack(requireView())
        }

        postDetailViewModel.getPostById(postId)

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun toggleLike(post: Post) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId != null) {
            // Kullanıcı, "likedByUsers" listesinde mevcut mu kontrol et
            if (post.likedByUsers.contains(currentUserId)) {
                // Kullanıcı daha önce beğenmiş, beğeniyi kaldır
                post.likedByUsers = post.likedByUsers.filter { it != currentUserId }
                post.likeCount--
                binding.incPostItem.ibtnLike.setImageResource(R.drawable.ic_like_outline)
            } else {
                // Kullanıcı daha önce beğenmemiş, beğeniyi ekle
                post.likedByUsers += currentUserId
                post.likeCount++
                binding.incPostItem.ibtnLike.setImageResource(R.drawable.ic_like)
            }
            postDetailViewModel.updatePostInFirebase(post){result->
                if (result){
                    binding.incPostItem.tvLikeCounts.text = post.likeCount.toString()
                }else{
                    Toast.makeText(requireContext(),"Something went wrong",Toast.LENGTH_SHORT).show()
                }

            }

        } else {
            Toast.makeText(requireContext(),"Something went wrong",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}