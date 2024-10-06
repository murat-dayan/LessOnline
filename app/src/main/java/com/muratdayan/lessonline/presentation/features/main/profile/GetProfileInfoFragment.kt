package com.muratdayan.lessonline.presentation.features.main.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.muratdayan.lessonline.R
import com.muratdayan.lessonline.databinding.FragmentGetProfileInfoBinding


class GetProfileInfoFragment : Fragment() {

    private var _binding: FragmentGetProfileInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentGetProfileInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.tvNotNow.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_getProfileInfoFragment_to_homeFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}