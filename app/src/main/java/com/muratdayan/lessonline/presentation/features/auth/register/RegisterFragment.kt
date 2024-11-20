package com.muratdayan.lessonline.presentation.features.auth.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.muratdayan.core.presentation.BaseFragment
import com.muratdayan.core.util.doIfIsEmptyAndReturn
import com.muratdayan.core.util.goBack
import com.muratdayan.core.util.setUpPasswordVisibility
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
    private var isRePasswordVisible = false

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
                showToast("username is empty", false)
            }
            if (isUserNameEmpty) return@setOnClickListener
            val isEmailEmpty = binding.etEmail.doIfIsEmptyAndReturn {
                showToast("email is empty", false)
            }
            if (isEmailEmpty) return@setOnClickListener
            val isPasswordEmpty = binding.etPassword.doIfIsEmptyAndReturn {
                showToast("password is empty", false)
            }
            if (isPasswordEmpty) return@setOnClickListener
            val isRepasswordEmpty = binding.etRepassword.doIfIsEmptyAndReturn {
                showToast("repassword is empty", false)
            }
            if (isRepasswordEmpty) return@setOnClickListener


            val username = binding.etUsername.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val rePassword = binding.etRepassword.text.toString().trim()
            if (password != rePassword) {
                showToast("Passwords are not equal",false)
                return@setOnClickListener
            }else{
                registerViewModel.signUp(username, email, password)
            }

        }


        binding.tilPassword.setEndIconOnClickListener {
            isPasswordVisible = binding.etPassword.setUpPasswordVisibility(isPasswordVisible,binding.tilPassword,requireContext())
        }

        binding.tilRepassword.setEndIconOnClickListener {
            isRePasswordVisible = binding.etRepassword.setUpPasswordVisibility(isRePasswordVisible,binding.tilRepassword,requireContext())
        }

        binding.ibtnBack.setOnClickListener {
            Navigation.goBack(requireView())
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}