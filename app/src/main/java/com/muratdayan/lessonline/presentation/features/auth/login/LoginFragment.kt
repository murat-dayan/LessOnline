package com.muratdayan.lessonline.presentation.features.auth.login

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.muratdayan.lessonline.R
import com.muratdayan.lessonline.databinding.FragmentLoginBinding
import com.muratdayan.lessonline.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment (): BaseFragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by viewModels()
    private var isPasswordVisible = false

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginViewModel.initializeGoogleSignInClient(requireContext())

        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ){result->
            if (result.resultCode == RESULT_OK){
                val data = result.data
                loginViewModel.handleGoogleSignInResult(data)
            }
        }

        binding.tvLoginGoogle.setOnClickListener {
            loginViewModel.loginWithGoogle(activityResultLauncher)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            loginViewModel.loginState.collectLatest {loginState->
                when(loginState){
                    is LoginState.Nothing -> {
                        hideLoading()
                    }
                    is LoginState.Loading -> {
                        showLoading()
                    }
                    is LoginState.Success -> {
                        hideLoading()
                        val navController = Navigation.findNavController(requireView())
                        val navOptions = NavOptions.Builder()
                            .setPopUpTo(R.id.loginFragment,true)
                            .build()
                        if (loginState.isGoogleLogin){
                            if (loginState.isNewUser){
                                navController.navigate(R.id.action_loginFragment_to_getProfileInfoFragment)
                            }else{
                                navController.navigate(R.id.action_loginFragment_to_homeFragment,null,navOptions)
                            }
                        }else{
                            Navigation.findNavController(requireView()).navigate(R.id.action_loginFragment_to_homeFragment,null,navOptions)
                        }

                    }
                    is LoginState.Error -> {
                        hideLoading()
                        Toast.makeText(requireContext(), "Error: ${loginState.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()){
                loginViewModel.login(email, password)
            }
        }

        binding.tilPassword.setEndIconOnClickListener {
            setUpPasswordVisibility()
        }

        binding.tvSignUp.setOnClickListener {
            val navController = Navigation.findNavController(requireView())
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.loginFragment,true)
                .build()
            navController.navigate(R.id.action_loginFragment_to_registerFragment,null,navOptions)
        }

        binding.tvForgottenPassword.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(R.id.action_loginFragment_to_forgetPasswordFragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setUpPasswordVisibility(){
        if (isPasswordVisible){
            binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.tilPassword.endIconDrawable = ContextCompat.getDrawable(requireContext(),R.drawable.ic_close_eye)
        }else{
            binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.tilPassword.endIconDrawable = ContextCompat.getDrawable(requireContext(),R.drawable.ic_open_eye)
        }

        binding.etPassword.setSelection(binding.etPassword.text?.length ?: 0)
        isPasswordVisible = !isPasswordVisible
    }

}