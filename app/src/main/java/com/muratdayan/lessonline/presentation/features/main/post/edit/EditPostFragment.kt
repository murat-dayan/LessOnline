package com.muratdayan.lessonline.presentation.features.main.post.edit

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.muratdayan.core.presentation.BaseFragment
import com.muratdayan.core.util.goBack
import com.muratdayan.lessonline.R
import com.muratdayan.lessonline.databinding.FragmentEditPostBinding
import com.muratdayan.lessonline.presentation.adapter.FilterAdapter
import dagger.hilt.android.AndroidEntryPoint
import jp.co.cyberagent.android.gpuimage.filter.GPUImageEmbossFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGrayscaleFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSepiaToneFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSketchFilter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditPostFragment : BaseFragment() {

    private var _binding: FragmentEditPostBinding? = null
    private val binding get() = _binding!!

    private val editPostViewModel: EditPostViewModel by viewModels()

    private val postAnswers = mutableListOf<String>()

    private var croppedImageUri: Uri? = null

    private lateinit var selectedFilter: GPUImageFilter

    private val cropImageLauncher = registerForActivityResult(CropImageContract()){result->
        if (result.isSuccessful){
            croppedImageUri = result.uriContent
            val originalBitmap:Bitmap = BitmapFactory.decodeStream(croppedImageUri?.let {
                requireContext().contentResolver.openInputStream(
                    it
                )
            })
            binding.ivPhoto.setImage(originalBitmap)
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
            val originalBitmap:Bitmap = BitmapFactory.decodeStream(requireContext().contentResolver.openInputStream(uri))
            binding.ivPhoto.setImage(originalBitmap)

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
            binding.ibtnFilter.setOnClickListener {
                binding.llFilters.visibility = View.VISIBLE
                setUpFilters(uri)
            }
        }

        binding.ibtnBack.setOnClickListener {
            Navigation.goBack(requireView())
        }

        binding.ibtnCloseFilters.setOnClickListener {
            binding.llFilters.visibility = View.GONE
        }


    }

    private fun setUpFilters(originalUri: Uri){

        val originalBitmap = BitmapFactory.decodeStream(requireContext().contentResolver.openInputStream(originalUri))

        val filters = listOf(
            GPUImageFilter(),
            GPUImageSepiaToneFilter(),
            GPUImageGrayscaleFilter(),
            GPUImageSketchFilter(),
            GPUImageEmbossFilter()
        )

        val filterAdapter = FilterAdapter(filters, originalBitmap =originalBitmap ){filter->
            binding.ivPhoto.filter = filter
            selectedFilter = filter
        }

        binding.rvFiltres.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter = filterAdapter
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}