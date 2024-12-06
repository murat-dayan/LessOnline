package com.muratdayan.lessonline.presentation.features.main.post.add

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.muratdayan.core.presentation.BaseFragment
import com.muratdayan.lessonline.databinding.FragmentAddPostBinding
import com.muratdayan.lessonline.presentation.features.main.post.PhotoViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddPostFragment : BaseFragment() {

    private var _binding: FragmentAddPostBinding? = null
    private val binding get() = _binding!!

    private val photoViewModel: PhotoViewModel by viewModels()

    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<String>

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){isGranted->
        if (isGranted){
            photoViewModel.startCamera(this, requireContext(),binding.previewView)
        }else{
            showToast("Can't open camera without required permissions",false)
        }
    }

    private val galleryPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ){permissions->
        val allPermissionsGranted = permissions.values.all { it }

        if (allPermissionsGranted) {

            photoViewModel.openGallery(galleryLauncher)
        } else {
            showToast("Can't open gallery without required permissions",false)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentAddPostBinding.inflate(inflater,container,false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ){result->
            if (result.resultCode == PackageManager.PERMISSION_GRANTED){
                photoViewModel.startCamera(this,requireContext(),binding.previewView)
            }else{
                showToast("permission need",true)
            }
        }

        galleryLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ){uri->
            uri?.let {
                val action = AddPostFragmentDirections.actionAddPostFragmentToEditPostFragment(it.toString())
                Navigation.findNavController(requireView()).navigate(action)
            }
        }

        binding.btnOpenGallery.setOnClickListener {
            photoViewModel.checkGalleryPermission(requireContext(),galleryPermissionLauncher,galleryLauncher)
        }

        photoViewModel.checkCameraPermission(this,requireContext(),cameraPermissionLauncher,binding.previewView)

        binding.fabTakePhoto.setOnClickListener {
            photoViewModel.takePhoto(requireContext()){uri ->
                uri?.let {
                    val action = AddPostFragmentDirections.actionAddPostFragmentToEditPostFragment(it.toString())
                    Navigation.findNavController(requireView()).navigate(action)
                } ?: showToast("Photo could not be taken",true)
            }
        }


    }





    override fun onDestroy() {
        super.onDestroy()
        _binding=null
        photoViewModel.shutdownExecutor()
    }
}