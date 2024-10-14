package com.muratdayan.lessonline.presentation.features.main.profile.yourprofile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.muratdayan.lessonline.R
import com.muratdayan.lessonline.databinding.FragmentProfileBinding
import com.muratdayan.lessonline.presentation.adapter.BasicPostListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels()

    private lateinit var basicPostListAdapter: BasicPostListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        basicPostListAdapter = BasicPostListAdapter()
        binding.rvMyPosts.apply {
            layoutManager = StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL)
            adapter = basicPostListAdapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            profileViewModel.userPosts.collectLatest { posts->
                basicPostListAdapter.submitList(posts)
            }
        }


        lifecycleScope.launch {
            profileViewModel.userProfile.collectLatest { userProfile->
                if (userProfile != null){
                    binding.tvUsername.text = userProfile.username
                    binding.tvFollowers.text = userProfile.followers.size.toString()
                    binding.tvFollowing.text = userProfile.following.size.toString()
                    binding.tvBio.text = userProfile.bio
                }else{
                    binding.tvUsername.text = "User not found"
                    binding.tvFollowers.text = "0"
                    binding.tvFollowing.text = "0"
                }
            }
        }

        binding.ibtnMenu.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(R.id.action_profileFragment_to_settingsFragment)
        }

        profileViewModel.getUserProfile()
        profileViewModel.getPosts()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}