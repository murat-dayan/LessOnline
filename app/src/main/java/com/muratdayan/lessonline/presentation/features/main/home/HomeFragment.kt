package com.muratdayan.lessonline.presentation.features.main.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.muratdayan.lessonline.R
import com.muratdayan.lessonline.databinding.FragmentHomeBinding
import com.muratdayan.lessonline.domain.model.firebasemodels.Post
import com.muratdayan.lessonline.presentation.adapter.PostAdapter
import com.muratdayan.lessonline.presentation.features.main.bottomsheets.answers.AnswersBottomSheetFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()

    private lateinit var postAdapter: PostAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postAdapter = PostAdapter(emptyList(), onAnswerIconClick = {
            val answersBottomSheet = AnswersBottomSheetFragment(it)
            answersBottomSheet.show(childFragmentManager,answersBottomSheet.tag)
        },onLikeIconClick = {post->
            toggleLike(post)
        },onProfilePhotoClick = {userId->
            val action = HomeFragmentDirections.actionHomeFragmentToOtherProfileFragment(userId)
            Navigation.findNavController(requireView()).navigate(action)
        },onBookmarkIconClick = {postId,_->
            homeViewModel.toggleBookmark(postId)
        }){postPhotoUri->
            binding.ivFullImage.visibility = View.VISIBLE
            binding.ibtnCloseFullImage.visibility = View.VISIBLE

            Glide.with(requireContext())
                .load(postPhotoUri)
                .into(binding.ivFullImage)
        }

        binding.ibtnCloseFullImage.setOnClickListener {
            binding.ivFullImage.visibility = View.GONE
            binding.ibtnCloseFullImage.visibility = View.GONE
        }


        binding.rvPosts.apply {
            adapter = postAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }


        lifecycleScope.launch {
            homeViewModel.postList.collectLatest {postListState->
                when(postListState){
                    is HomeViewModel.PostListState.Loading->{
                        startShimmer()
                    }
                    is HomeViewModel.PostListState.Success->{
                        stopShimmer()
                        postAdapter.updatePostList(postListState.postList)
                    }
                    is HomeViewModel.PostListState.Error->{
                        Log.d("HomeFragment","postliststate: ${postListState.message}")
                    }
                    else->{}
                }
            }
        }

        homeViewModel.fetchPosts()

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
            } else {
                // Kullanıcı daha önce beğenmemiş, beğeniyi ekle
                post.likedByUsers += currentUserId
                post.likeCount++
            }
            homeViewModel.updatePostInFirebase(post)
            postAdapter.notifyDataSetChanged()
        } else {
            // Kullanıcı oturum açmamış
        }
    }


    private fun startShimmer(){
        binding.sflForPost.visibility = View.VISIBLE
        binding.rvPosts.visibility = View.GONE
        binding.sflForPost.startShimmer()
    }

    private fun stopShimmer(){
        binding.sflForPost.visibility = View.GONE
        binding.rvPosts.visibility = View.VISIBLE
        binding.sflForPost.stopShimmer()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}