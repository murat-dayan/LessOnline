package com.muratdayan.lessonline.presentation.features.main.savedpost

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.muratdayan.core.util.goBack
import com.muratdayan.lessonline.databinding.FragmentSavedPostsBinding
import com.muratdayan.lessonline.presentation.adapter.BasicPostListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SavedPostsFragment : Fragment() {

    private var _binding: FragmentSavedPostsBinding? = null
    private val binding get() = _binding!!

    private val savedPostsViewModel: SavedPostsViewModel by viewModels()

    private lateinit var basicPostListAdapter: BasicPostListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentSavedPostsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        basicPostListAdapter = BasicPostListAdapter(){postId->
            val action = SavedPostsFragmentDirections.actionSavedPostsFragmentToPostDetailFragment(postId)
            Navigation.findNavController(requireView()).navigate(action)
        }

        binding.rvSavedPosts.apply {
            layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
            adapter = basicPostListAdapter
        }

        lifecycleScope.launch {
            savedPostsViewModel.savedPostList.collectLatest {postList->
                Log.d("SavedPostsFragment","postList: $postList")
                basicPostListAdapter.submitList(postList)
            }
        }

        savedPostsViewModel.fetchSavedPosts()

        binding.ibtnBack.setOnClickListener {
            Navigation.goBack(requireView())
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}