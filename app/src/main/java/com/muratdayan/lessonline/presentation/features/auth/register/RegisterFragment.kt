package com.muratdayan.lessonline.presentation.features.auth.register

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.muratdayan.core.presentation.BaseFragment
import com.muratdayan.core.util.doIfIsEmptyAndReturn
import com.muratdayan.lessonline.R
import com.muratdayan.lessonline.databinding.FragmentRegisterBinding
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
                        val navController = Navigation.findNavController(requireView())
                        navController.navigate(R.id.action_registerFragment_to_getProfileInfoFragment)
                    }

                    is SignUpState.Error -> {
                        hideLoading()
                        showToast("${signUpState.message}", isError = true)
                    }
                }

            }
        }

        binding.btnSignUp.setOnClickListener {
            val isUserNameEmpty = binding.etUsername.doIfIsEmptyAndReturn {
                showToast("username is empty",false)
            }
            val isEmailEmpty = binding.etEmail.doIfIsEmptyAndReturn {
                showToast("email is empty",false)
            }
            val isPasswordEmpty = binding.etPassword.doIfIsEmptyAndReturn {
                showToast("password is empty",false)
            }
            val isRepasswordEmpty = binding.etRepassword.doIfIsEmptyAndReturn {
                showToast("repassword is empty",false)
            }

            if (!isUserNameEmpty && !isEmailEmpty && !isPasswordEmpty && !isRepasswordEmpty) {
                val username = binding.etUsername.text.toString().trim()
                val email = binding.etEmail.text.toString().trim()
                val password = binding.etPassword.text.toString().trim()
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