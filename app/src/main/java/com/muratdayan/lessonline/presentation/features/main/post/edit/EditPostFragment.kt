package com.muratdayan.lessonline.presentation.features.main.post.edit

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.muratdayan.lessonline.databinding.FragmentEditPostBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditPostFragment : Fragment() {

    private var _binding: FragmentEditPostBinding? = null
    private val binding get() = _binding!!

    private val editPostViewModel: EditPostViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
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

            // Collect upload state
            lifecycleScope.launch {
                editPostViewModel.uploadState.collectLatest { uploadState ->
                    when (uploadState) {
                        is UploadAndSaveState.Loading -> {
                            binding.pbShare.visibility = View.VISIBLE
                            binding.btnShare.visibility = View.INVISIBLE
                        }
                        is UploadAndSaveState.Success -> {
                            binding.pbShare.visibility = View.GONE
                            binding.btnShare.visibility = View.INVISIBLE
                            val photoUri = uploadState.downloadUri
                            photoUri?.let {
                                val comment = binding.etComment.text.toString()
                                editPostViewModel.savePostToFirebase(photoUri, comment)
                            }
                            // Uncomment the next line if you want to navigate after saving
                            //val navController = findNavController()
                            //navController.navigate(EditPostFragmentDirections.actionEditPostFragmentToHomeFragment())
                        }
                        is UploadAndSaveState.Error -> {
                            binding.pbShare.visibility = View.GONE
                            binding.btnShare.visibility = View.INVISIBLE
                            Log.e("EditPostFragment", "Error: ${uploadState.message}")
                        }
                        else -> {}
                    }
                }
            }

            binding.btnShare.setOnClickListener {
                editPostViewModel.uploadPhotoToFirebaseStorage(uri)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}