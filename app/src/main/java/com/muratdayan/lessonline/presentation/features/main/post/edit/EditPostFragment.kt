package com.muratdayan.lessonline.presentation.features.main.post.edit

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.muratdayan.lessonline.R
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

    private val postAnswers = mutableListOf<String>()

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
                            binding.btnShare.visibility = View.VISIBLE
                            if (postAnswers.size == 4) {
                                val photoUri = uploadState.downloadUri
                                photoUri?.let {
                                    val comment = binding.etComment.text.toString()

                                    editPostViewModel.savePostToFirebase(
                                        photoUri,
                                        comment,
                                        postAnswers
                                    ) {
                                        findNavController().navigate(R.id.action_editPostFragment_to_homeFragment)
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