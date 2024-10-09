package com.muratdayan.lessonline.presentation.features.main.post

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.muratdayan.lessonline.R
import com.muratdayan.lessonline.databinding.FragmentEditPostBinding


class EditPostFragment : Fragment() {

    private var _binding: FragmentEditPostBinding? = null
    private val binding get() = _binding!!

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
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}