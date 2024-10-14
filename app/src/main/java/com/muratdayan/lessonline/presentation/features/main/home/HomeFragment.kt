package com.muratdayan.lessonline.presentation.features.main.home

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
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

        postAdapter = PostAdapter(emptyList(),{
            val answersBottomSheet = AnswersBottomSheetFragment(it)
            answersBottomSheet.show(childFragmentManager,answersBottomSheet.tag)
        }){post->
            toggleLike(post)
        }


        binding.rvPosts.apply {
            adapter = postAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        lifecycleScope.launch {
            homeViewModel.postList.collectLatest {postList->
                postAdapter.updatePostList(postList)
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
                post.likedByUsers = post.likedByUsers + currentUserId
                post.likeCount++
            }
            homeViewModel.updatePostInFirebase(post)
            postAdapter.notifyDataSetChanged()
        } else {
            // Kullanıcı oturum açmamış
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}