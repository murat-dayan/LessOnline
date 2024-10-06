package com.muratdayan.lessonline.presentation.features.main.post

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.Image
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.camera2.internal.annotation.CameraExecutor
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.muratdayan.lessonline.R
import com.muratdayan.lessonline.databinding.FragmentAddPostBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.ExecutorService

@AndroidEntryPoint
class AddPostFragment : Fragment() {

    private var _binding: FragmentAddPostBinding? = null
    private val binding get() = _binding!!

    private val addPostViewModel: AddPostViewModel by viewModels()

    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<String>


    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){isGranted->
        if (isGranted){
            addPostViewModel.startCamera(this, requireContext(),binding.previewView)
        }else{
            Toast.makeText(requireContext(),"permission need",Toast.LENGTH_SHORT).show()
        }
    }

    private val galleryPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){isGranted->
        if (isGranted){
            addPostViewModel.openGallery(galleryLauncher)
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
                addPostViewModel.startCamera(this,requireContext(),binding.previewView)
            }else{
                Toast.makeText(requireContext(),"Camera cant open",Toast.LENGTH_SHORT).show()
            }
        }

        galleryLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ){uri->
            uri?.let {
                addPostViewModel.handleGalleryResult(it,requireContext())
            }
        }

        binding.btnOpenGallery.setOnClickListener {
            addPostViewModel.checkGalleryPermission(requireContext(),galleryPermissionLauncher,galleryLauncher)
        }

        addPostViewModel.checkCameraPermission(this,requireContext(),cameraPermissionLauncher,binding.previewView)


    }





    override fun onDestroy() {
        super.onDestroy()
        _binding=null
        addPostViewModel.shutdownExecutor()
    }
}