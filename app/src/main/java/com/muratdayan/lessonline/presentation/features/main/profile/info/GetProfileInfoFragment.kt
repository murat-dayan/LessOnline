package com.muratdayan.lessonline.presentation.features.main.profile.info

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.muratdayan.core.presentation.BaseFragment
import com.muratdayan.core.util.Result
import com.muratdayan.lessonline.R
import com.muratdayan.lessonline.databinding.FragmentGetProfileInfoBinding
import com.muratdayan.lessonline.presentation.features.main.post.PhotoViewModel
import com.muratdayan.lessonline.presentation.util.UserRole
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GetProfileInfoFragment : BaseFragment() {

    private var _binding: FragmentGetProfileInfoBinding? = null
    private val binding get() = _binding!!

    private val getProfileInfoViewModel: GetProfileInfoViewModel by viewModels()
    private val photoViewModel: PhotoViewModel by viewModels()

    private lateinit var galleryLauncher: ActivityResultLauncher<String>

    private val galleryPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ){permissions->
        val allPermissionsGranted = permissions.values.all { it } // Eğer tüm izinler granted ise true döner

        if (allPermissionsGranted) {
            photoViewModel.openGallery(galleryLauncher)
        } else {
            Toast.makeText(requireContext(), "Can't open gallery without required permissions", Toast.LENGTH_SHORT).show()
        }
    }

    private var selectedRole: UserRole?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentGetProfileInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val roles = arrayOf(UserRole.STUDENT,UserRole.TEACHER)

        val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item,roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spRole.adapter = adapter

        binding.spRole.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedRole = parent?.getItemAtPosition(position) as UserRole
                //Toast.makeText(requireContext(),"$selectedRole",Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // Handle the case where nothing is selected
            }

        }

        lifecycleScope.launch {
            getProfileInfoViewModel.saveState.collectLatest { result->
                when(result){
                    is Result.Success -> {
                        hideLoading()
                        val navController = Navigation.findNavController(requireView())
                        val navOptions = NavOptions.Builder()
                            .setPopUpTo(R.id.getProfileInfoFragment,true)
                            .build()
                        navController.navigate(R.id.action_getProfileInfoFragment_to_homeFragment,null,navOptions)
                    }
                    is Result.Error -> {
                        hideLoading()
                        Toast.makeText(requireContext(),result.exception.message.toString(),Toast.LENGTH_SHORT).show()
                    }
                    is Result.Loading -> {
                        showLoading()
                    }

                    Result.Idle -> {

                    }
                }
            }
        }

        binding.btnSave.setOnClickListener {
            val name = binding.etUsername.text.toString().trim()
            val bio = binding.tvBio.text.toString().trim()

            if (name.isNotEmpty() && selectedRole != null){
                getProfileInfoViewModel.createUserProfile(name,bio,selectedRole)
            }else{
                Toast.makeText(requireContext(),"Please Fill ",Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvNotNow.setOnClickListener {
            val navController = Navigation.findNavController(requireView())
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.getProfileInfoFragment,true)
                .build()
            navController.navigate(R.id.action_getProfileInfoFragment_to_homeFragment,null,navOptions)
        }

        galleryLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ){uri->
            uri?.let {
                getProfileInfoViewModel.uploadProfilePhotoAndSaveUrl(it)
            }
        }

        binding.ivProfileGetInfo.setOnClickListener {
            photoViewModel.checkGalleryPermission(requireContext(),galleryPermissionLauncher,galleryLauncher)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            getProfileInfoViewModel.profilePhotouploadState.collectLatest {profilePhotouploadState->
                when(profilePhotouploadState){
                    is GetProfileInfoViewModel.UploadState.Idle -> {}
                    is GetProfileInfoViewModel.UploadState.Loading -> {
                        showLoading()
                    }
                    is GetProfileInfoViewModel.UploadState.Success -> {
                        hideLoading()
                        Glide.with(requireContext())
                            .load(profilePhotouploadState.uri)
                            .into(binding.ivProfileGetInfo)
                    }
                    is GetProfileInfoViewModel.UploadState.Error -> {
                        hideLoading()
                        showToast("Your profile photo could not be uploaded",false)
                    }
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}