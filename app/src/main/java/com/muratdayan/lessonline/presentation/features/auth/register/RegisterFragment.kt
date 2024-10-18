package com.muratdayan.lessonline.presentation.features.auth.register

import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.muratdayan.lessonline.R
import com.muratdayan.lessonline.databinding.FragmentRegisterBinding
import com.muratdayan.lessonline.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : BaseFragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val registerViewModel: RegisterViewModel by viewModels()

    private var isPasswordVisible = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            registerViewModel.signUpState.collectLatest { signUpState ->
                when (signUpState) {
                    is SignUpState.Nothing -> {
                        hideLoading()
                    }
                    is SignUpState.Loading -> {
                        showLoading()
                    }

                    is SignUpState.Success -> {
                        hideLoading()
                        Navigation.findNavController(requireView()).navigate(R.id.action_registerFragment_to_getProfileInfoFragment)
                    }

                    is SignUpState.Error -> {
                        hideLoading()
                        Toast.makeText(requireContext(), "Error: ${signUpState.message}", Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }

        binding.btnSignUp.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val repassword = binding.etRepassword.text.toString().trim()

            if ((username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && repassword.isNotEmpty()) && (password == repassword)) {
                registerViewModel.signUp(username, email, password)
            }
        }


        binding.tilPassword.setEndIconOnClickListener {
            setUpPasswordVisibility(binding.etPassword, binding.tilPassword)
        }

        binding.tilRepassword.setEndIconOnClickListener {
            setUpPasswordVisibility(binding.etRepassword, binding.tilRepassword)
        }


    }

    private fun setUpPasswordVisibility(et: TextInputEditText, til: TextInputLayout) {
        if (isPasswordVisible) {
            et.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            til.endIconDrawable =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_close_eye)
        } else {
            et.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            til.endIconDrawable =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_open_eye)
        }

        et.setSelection(et.text?.length ?: 0)
        isPasswordVisible = !isPasswordVisible
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}