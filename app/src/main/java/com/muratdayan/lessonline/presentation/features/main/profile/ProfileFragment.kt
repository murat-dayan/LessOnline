package com.muratdayan.lessonline.presentation.features.main.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.muratdayan.lessonline.R
import com.muratdayan.lessonline.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileViewModel.getUserProfile()

        lifecycleScope.launch {
            profileViewModel.userProfile.collectLatest { userProfile->
                if (userProfile != null){
                    binding.tvUsername.text = userProfile.username
                    binding.tvFollowers.text = userProfile.followers.size.toString()
                    binding.tvFollowing.text = userProfile.following.size.toString()
                }else{
                    binding.tvUsername.text = "User not found"
                    binding.tvFollowers.text = "0"
                    binding.tvFollowing.text = "0"
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}