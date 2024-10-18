package com.muratdayan.lessonline.presentation.features.main.post.edit

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.muratdayan.lessonline.R
import com.muratdayan.lessonline.databinding.FragmentEditPostBinding
import com.muratdayan.lessonline.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class EditPostFragment : BaseFragment() {

    private var _binding: FragmentEditPostBinding? = null
    private val binding get() = _binding!!

    private val editPostViewModel: EditPostViewModel by viewModels()

    private val postAnswers = mutableListOf<String>()

    private var croppedImageUri: Uri? = null

    private val cropImageLauncher = registerForActivityResult(CropImageContract()){result->
        if (result.isSuccessful){
            croppedImageUri = result.uriContent
            binding.ivPhoto.setImageURI(croppedImageUri)
        }else{
            val exception = result.error
            Toast.makeText(requireContext(),exception?.message,Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: EditPostFragmentArgs by navArgs()
        val uriString = args.uri



        uriString.let {
            val uri = Uri.parse(uriString)
            binding.ivPhoto.setImageURI(uri)

            binding.ibtnEditPhoto.setOnClickListener {
                cropImageLauncher.launch(
                    CropImageContractOptions(
                        uri = uri,
                        cropImageOptions = CropImageOptions(
                            guidelines = CropImageView.Guidelines.ON
                        )
                    )
                )
            }

            // Collect upload state
            lifecycleScope.launch {
                editPostViewModel.uploadState.collectLatest { uploadState ->
                    when (uploadState) {
                        is UploadAndSaveState.Loading -> {
                            showLoading()
                        }

                        is UploadAndSaveState.Success -> {
                            hideLoading()
                            if (postAnswers.size == 4) {
                                val photoUri = uploadState.downloadUri
                                photoUri?.let {
                                    val comment = binding.etComment.text.toString()

                                    editPostViewModel.savePostToFirebase(
                                        photoUri,
                                        comment,
                                        postAnswers
                                    ) {
                                        val navController = Navigation.findNavController(requireView())
                                        val navOptions = NavOptions.Builder()
                                            .setPopUpTo(R.id.nav_graph,true)
                                            .build()
                                        navController.navigate(R.id.action_editPostFragment_to_homeFragment,null,navOptions)
                                    }
                                }
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Please add 4 answers",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        is UploadAndSaveState.Error -> {
                            hideLoading()
                            Log.e("EditPostFragment", "Error: ${uploadState.message}")
                        }

                        else -> {}
                    }
                }
            }

            binding.btnAddAnswer.setOnClickListener {
                val newAnswer = binding.etNewAnswer.text.toString()
                if (newAnswer.isNotEmpty() && postAnswers.size < 4) {
                    postAnswers.add(newAnswer)
                    binding.etNewAnswer.text.clear()

                    val tvPostAnswer = TextView(requireContext()).apply {
                        text = newAnswer
                        textSize = 16f
                        setPadding(8, 8, 8, 8)
                        setOnClickListener {
                            val index = binding.llAnswers.indexOfChild(this)
                            if (index != -1) {
                                postAnswers.removeAt(index)
                                binding.llAnswers.removeView(this)
                            }
                        }
                    }
                    binding.llAnswers.addView(tvPostAnswer)
                } else {
                    Toast.makeText(requireContext(), "You can add up to 4 answers", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            binding.btnShare.setOnClickListener {
                if (croppedImageUri != null){
                    editPostViewModel.uploadPhotoToFirebaseStorage(croppedImageUri!!)
                }else{
                    editPostViewModel.uploadPhotoToFirebaseStorage(uri)
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}