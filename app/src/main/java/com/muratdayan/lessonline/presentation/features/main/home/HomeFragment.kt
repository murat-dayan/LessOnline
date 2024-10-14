package com.muratdayan.lessonline.presentation.features.main.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.muratdayan.lessonline.R
import com.muratdayan.lessonline.databinding.FragmentHomeBinding
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

        postAdapter = PostAdapter(emptyList()){
            val answersBottomSheet = AnswersBottomSheetFragment(it)
            answersBottomSheet.show(childFragmentManager,answersBottomSheet.tag)
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}