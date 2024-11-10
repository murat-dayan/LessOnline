package com.muratdayan.lessonline.presentation.features.main.profile.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.muratdayan.lessonline.R
import com.muratdayan.lessonline.core.Result
import com.muratdayan.lessonline.databinding.FragmentGetProfileInfoBinding
import com.muratdayan.lessonline.presentation.base.BaseFragment
import com.muratdayan.lessonline.presentation.util.UserRole
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GetProfileInfoFragment : BaseFragment() {

    private var _binding: FragmentGetProfileInfoBinding? = null
    private val binding get() = _binding!!

    private val getProfileInfoViewModel: GetProfileInfoViewModel by viewModels()

    private var selectedRole: UserRole?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentGetProfileInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val roles = arrayOf(UserRole.STUDENT,UserRole.TEACHER)

        val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item,roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spRole.adapter = adapter

        binding.spRole.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedRole = parent?.getItemAtPosition(position) as UserRole
                //Toast.makeText(requireContext(),"$selectedRole",Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // Handle the case where nothing is selected
            }

        }

        lifecycleScope.launch {
            getProfileInfoViewModel.saveState.collectLatest { result->
                when(result){
                    is Result.Success -> {
                        hideLoading()
                        val navController = Navigation.findNavController(requireView())
                        val navOptions = NavOptions.Builder()
                            .setPopUpTo(R.id.getProfileInfoFragment,true)
                            .build()
                        navController.navigate(R.id.action_getProfileInfoFragment_to_homeFragment,null,navOptions)
                    }
                    is Result.Error -> {
                        hideLoading()
                        Toast.makeText(requireContext(),result.exception.message.toString(),Toast.LENGTH_SHORT).show()
                    }
                    is Result.Loading -> {
                        showLoading()
                    }

                    Result.Idle -> {

                    }
                }
            }
        }

        binding.btnSave.setOnClickListener {
            val name = binding.etUsername.text.toString().trim()
            val bio = binding.tvBio.text.toString().trim()

            if (name.isNotEmpty() && selectedRole != null){
                getProfileInfoViewModel.createUserProfile(name,bio,selectedRole)
            }else{
                Toast.makeText(requireContext(),"Please Fill ",Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvNotNow.setOnClickListener {
            val navController = Navigation.findNavController(requireView())
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.getProfileInfoFragment,true)
                .build()
            navController.navigate(R.id.action_getProfileInfoFragment_to_homeFragment,null,navOptions)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}