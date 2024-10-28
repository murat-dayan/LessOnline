package com.muratdayan.lessonline.presentation.features.main.post.add

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.muratdayan.lessonline.databinding.FragmentAddPostBinding
import com.muratdayan.lessonline.presentation.features.main.post.PhotoViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddPostFragment : Fragment() {

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
            Toast.makeText(requireContext(),"permission need",Toast.LENGTH_SHORT).show()
        }
    }

    private val galleryPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){isGranted->
        if (isGranted){
            photoViewModel.openGallery(galleryLauncher)
        }else{
            Toast.makeText(requireContext(),"cant open gallery",Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentAddPostBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ){result->
            if (result.resultCode == PackageManager.PERMISSION_GRANTED){
                photoViewModel.startCamera(this,requireContext(),binding.previewView)
            }else{
                Toast.makeText(requireContext(),"Camera cant open",Toast.LENGTH_SHORT).show()
            }
        }

        galleryLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ){uri->
            uri?.let {
                photoViewModel.handleGalleryResult(it,requireContext())
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
                } ?: Toast.makeText(requireContext(),"Photo not Taken",Toast.LENGTH_SHORT).show()
            }
        }


    }





    override fun onDestroy() {
        super.onDestroy()
        _binding=null
        photoViewModel.shutdownExecutor()
    }
}