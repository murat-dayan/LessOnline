package com.muratdayan.lessonline.presentation.features.main.profile.otherprofile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.muratdayan.core.presentation.BaseFragment
import com.muratdayan.lessonline.core.Result
import com.muratdayan.lessonline.databinding.FragmentOtherProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OtherProfileFragment : BaseFragment() {

    private var _binding : FragmentOtherProfileBinding?=null
    private val binding get() = _binding!!

    private val otherProfileViewModel:OtherProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentOtherProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: OtherProfileFragmentArgs by navArgs()
        val otherUserId = args.userId

        otherProfileViewModel.checkIfFollowing(otherUserId)
        otherProfileViewModel.getUserProfile(otherUserId)

        lifecycleScope.launch {
            otherProfileViewModel.userProfile.collectLatest { result->
                when(result){
                    is Result.Error -> {
                        hideLoading()
                        showError(result.exception.message.toString())
                    }
                    Result.Loading -> {
                        showLoading()
                    }
                    is Result.Success -> {
                        hideLoading()
                        val userProfile = result.data
                        if (userProfile != null){
                            binding.tvUsername.text = userProfile.username
                            binding.tvFollowers.text = userProfile.followers.size.toString()
                            binding.tvFollowing.text = userProfile.following.size.toString()
                            binding.tvBio.text = userProfile.bio
                            if (userProfile.profilePhotoUrl.isNotEmpty()){
                                Glide.with(requireContext())
                                    .load(userProfile.profilePhotoUrl)
                                    .into(binding.ivProfile)
                            }
                        }
                    }

                    Result.Idle -> {}
                }
            }
        }

        lifecycleScope.launch {
            otherProfileViewModel.isFollowing.collectLatest { isFollowing->
                updateFollowButton(isFollowing)
            }
        }

        lifecycleScope.launch {
            otherProfileViewModel.followMessage.collectLatest { message ->
                message?.let {
                    Toast.makeText(requireContext(),message,Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnFollow.setOnClickListener {
            val isFollowing =  otherProfileViewModel.isFollowing.value
            if (isFollowing){
                otherProfileViewModel.unFollowUser(otherUserId)
            }else{
                otherProfileViewModel.followUser(otherUserId)
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun updateFollowButton(isFollowing: Boolean) {
        if (isFollowing) {
            binding.btnFollow.text = "Unfollow"
        } else {
            binding.btnFollow.text = "Follow"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}