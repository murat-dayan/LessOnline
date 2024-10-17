package com.muratdayan.lessonline.presentation.features.main.profile.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.muratdayan.lessonline.R
import com.muratdayan.lessonline.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding?=null
    private val binding get() = _binding!!

    private val settingsViewModel:SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentSettingsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvSignOut.setOnClickListener {
            settingsViewModel.logout()
            val navController = Navigation.findNavController(requireView())
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.nav_graph,true)
                .build()
            navController.navigate(R.id.action_settingsFragment_to_loginFragment,null,navOptions)
        }

        binding.ibtnBack.setOnClickListener {
            Navigation.findNavController(requireView()).navigateUp()
        }

        binding.ibtnSavedPosts.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(R.id.action_settingsFragment_to_savedPostsFragment)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}