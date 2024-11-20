package com.muratdayan.lessonline.presentation.features.main.home

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
import com.muratdayan.lessonline.databinding.FragmentHomeBinding
import com.muratdayan.lessonline.presentation.adapter.PostAdapter
import com.muratdayan.lessonline.presentation.features.main.bottomsheets.answers.AnswersBottomSheetFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment() {

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

        /*homeViewModel.adRequest.observe(viewLifecycleOwner){adRequest->
            binding.adView.loadAd(adRequest)
        }

        binding.adView.adListener = object : AdListener(){
            override fun onAdLoaded() {
                super.onAdLoaded()
                Toast.makeText(requireContext(),"reklam yüklendi",Toast.LENGTH_SHORT).show()
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                Log.e("HomeFragment",p0.message)
            }

        }*/

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
                val action = HomeFragmentDirections.actionHomeFragmentToOtherProfileFragment(userId)
                Navigation.findNavController(requireView()).navigate(action)
            }
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
            var isFirstLoad = true
            homeViewModel.postList.collectLatest {postListState->
                when(postListState){
                    is HomeViewModel.PostListState.Loading->{
                        if (isFirstLoad){
                            startShimmer()
                        }
                    }
                    is HomeViewModel.PostListState.Success->{
                        stopShimmer()
                        postAdapter.updatePostList(postListState.postList)
                        isFirstLoad = false

                        if (postListState.postList.isEmpty()) {
                            binding.tvEmptyList.visibility = View.VISIBLE
                        } else {
                            binding.tvEmptyList.visibility = View.GONE
                        }
                    }
                    is HomeViewModel.PostListState.Error->{
                        stopShimmer()
                        showError(postListState.message.toString())
                        Log.d("HomeFragment","postliststate: ${postListState.message}")
                    }
                    else->{}
                }
            }
        }

        homeViewModel.fetchPosts()

        binding.ibtnDirectMessages.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(R.id.action_homeFragment_to_chat_graph)
        }

        binding.swlPosts.setOnRefreshListener {
            refreshData()
        }

    }

    private fun refreshData() {
        homeViewModel.fetchPosts()
        binding.swlPosts.isRefreshing = false
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