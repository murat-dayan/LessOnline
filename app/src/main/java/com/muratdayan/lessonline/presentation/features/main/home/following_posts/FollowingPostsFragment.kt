package com.muratdayan.lessonline.presentation.features.main.home.following_posts

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.muratdayan.core.presentation.BaseFragment
import com.muratdayan.lessonline.R
import com.muratdayan.lessonline.databinding.FragmentFollowingPostsBinding
import com.muratdayan.lessonline.presentation.adapter.PostAdapter
import com.muratdayan.lessonline.presentation.features.main.bottomsheets.answers.AnswersBottomSheetFragment
import com.muratdayan.lessonline.presentation.features.main.home.HomeViewModel
import com.muratdayan.lessonline.presentation.features.main.home.PostListState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FollowingPostsFragment(
    val onPhotoClick:(String)->Unit
) : BaseFragment() {

    private var _binding : FragmentFollowingPostsBinding? = null
    private val binding get() = _binding!!

    private val followingPostsViewModel: FollowingPostsViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentFollowingPostsBinding.inflate(inflater,container,false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        followingPostsViewModel.fetchFollowingPosts()

        postAdapter = PostAdapter(
            emptyList(),
            onAnswerIconClick = {postId, position->
                val answersBottomSheet = AnswersBottomSheetFragment(postId){isAnswerChanged->
                    if (isAnswerChanged){
                        postAdapter.notifyItemChanged(position)
                    }
                }
                answersBottomSheet.show(childFragmentManager,answersBottomSheet.tag)
            },onLikeIconClick = {post,position->
                homeViewModel.toggleLikePost(post){
                    postAdapter.notifyItemChanged(position)
                }
            },onProfilePhotoClick = {userId->
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

                if (userId == currentUserId) {
                    Navigation.findNavController(requireView()).navigate(R.id.action_homeFragment_to_profileFragment)
                } else {
                    // Diğer kullanıcının profiline tıklanmışsa OtherProfileFragment'a yönlendir
                    onPhotoClick(userId)
                }
            },onBookmarkIconClick = {postId,_->
                homeViewModel.toggleBookmark(postId){
                    followingPostsViewModel.fetchFollowingPosts()
                }
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


        binding.rvFollowingPosts.apply {
            adapter = postAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        lifecycleScope.launch {
            var isFirstLoad = true
            followingPostsViewModel.followingPostList.collectLatest {postListState->
                when(postListState){
                    is PostListState.Loading->{
                        if (isFirstLoad){
                            startShimmer()
                        }
                    }
                    is PostListState.Success->{
                        stopShimmer()
                        postAdapter.updatePostList(postListState.postList)
                        isFirstLoad = false

                        if (postListState.postList.isEmpty()) {
                            binding.evFollowingPosts.visibility = View.VISIBLE
                        } else {
                            binding.evFollowingPosts.visibility = View.GONE
                        }
                    }
                    is PostListState.Error->{
                        stopShimmer()
                        Log.d("HomeFragment","postliststate: ${postListState.message}")
                    }
                    else->{}
                }
            }
        }

        binding.swlPosts.setOnRefreshListener {
            refreshData()
        }


    }

    private fun refreshData() {
        followingPostsViewModel.fetchFollowingPosts()
        binding.swlPosts.isRefreshing = false
    }

    private fun startShimmer(){
        binding.sflForPost.visibility = View.VISIBLE
        binding.rvFollowingPosts.visibility = View.GONE
        binding.sflForPost.startShimmer()
    }

    private fun stopShimmer(){
        binding.sflForPost.visibility = View.GONE
        binding.rvFollowingPosts.visibility = View.VISIBLE
        binding.sflForPost.stopShimmer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}