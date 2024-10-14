package com.muratdayan.lessonline.presentation.features.auth.forgetpassword

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.muratdayan.lessonline.R
import com.muratdayan.lessonline.databinding.FragmentForgetPasswordBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class ForgetPasswordFragment : Fragment() {

    private var _binding: FragmentForgetPasswordBinding? = null
    private val binding get() = _binding!!

    private val forgetPasswordViewModel: ForgetPasswordViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentForgetPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnResetPassword.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            if (email.isNotEmpty()){
                forgetPasswordViewModel.resetPassword(email)
            }else{
                Toast.makeText(requireContext(),"fill Fields",Toast.LENGTH_SHORT).show()
            }
        }

        lifecycleScope.launch {
            forgetPasswordViewModel.resetResult.collectLatest { result->
                if (result){
                    Navigation.findNavController(requireView()).navigateUp()
                }else{
                    Toast.makeText(requireContext(),"Error",Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}