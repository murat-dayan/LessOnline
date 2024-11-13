package com.muratdayan.lessonline.presentation.features.main.profile.yourprofile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.muratdayan.lessonline.R
import com.muratdayan.lessonline.core.Result
import com.muratdayan.lessonline.databinding.FragmentProfileBinding
import com.muratdayan.lessonline.presentation.adapter.BasicPostListAdapter
import com.muratdayan.lessonline.presentation.base.BaseFragment
import com.muratdayan.lessonline.presentation.features.main.post.PhotoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : BaseFragment() {

    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels()
    private val photoViewModel:PhotoViewModel by viewModels()

    private lateinit var basicPostListAdapter: BasicPostListAdapter

    private lateinit var galleryLauncher: ActivityResultLauncher<String>

    private val galleryPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ){permissions->
        // permissions: Map<String, Boolean>
        val allPermissionsGranted = permissions.values.all { it } // Eğer tüm izinler granted ise true döner

        if (allPermissionsGranted) {
            // Tüm izinler verilmişse galeriyi açabilirsin
            photoViewModel.openGallery(galleryLauncher)
        } else {
            // En az bir izin verilmemişse, kullanıcıya uyarı göster
            Toast.makeText(requireContext(), "Can't open gallery without required permissions", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        basicPostListAdapter = BasicPostListAdapter{postId->
            val action = ProfileFragmentDirections.actionProfileFragmentToPostDetailFragment(postId)
            Navigation.findNavController(requireView()).navigate(action)
        }

        binding.rvMyPosts.apply {
            layoutManager = StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL)
            adapter = basicPostListAdapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            profileViewModel.userPosts.collectLatest { posts->
                basicPostListAdapter.submitList(posts)
                binding.tvPosts.text = posts.size.toString()
            }
        }

        galleryLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ){uri->
            uri?.let {
                profileViewModel.uploadProfilePhotoAndSaveUrl(it)
            }
        }

        binding.ivProfile.setOnClickListener {
            photoViewModel.checkGalleryPermission(requireContext(),galleryPermissionLauncher,galleryLauncher)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            profileViewModel.uploadState.collectLatest {uploadState->
                when(uploadState){
                    is ProfileViewModel.UploadState.Idle -> {}
                    is ProfileViewModel.UploadState.Loading -> {}
                    is ProfileViewModel.UploadState.Success -> {
                        Glide.with(requireContext())
                            .load(uploadState.uri)
                            .into(binding.ivProfile)
                    }
                    is ProfileViewModel.UploadState.Error -> {
                        Toast.makeText(requireContext(),uploadState.error,Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


        lifecycleScope.launch {
            profileViewModel.userProfile.collectLatest { result->
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
                        }else{
                            binding.tvUsername.text = "User not found"
                            binding.tvFollowers.text = "0"
                            binding.tvFollowing.text = "0"
                        }
                    }

                    Result.Idle -> {}
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