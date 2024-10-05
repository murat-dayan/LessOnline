package com.muratdayan.lessonline.presentation.features.auth.login

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
import com.muratdayan.lessonline.R
import com.muratdayan.lessonline.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by viewModels()
    private var isPasswordVisible = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            loginViewModel.loginState.collectLatest {loginState->
                when(loginState){
                    is LoginState.Nothing -> {}
                    is LoginState.Loading -> {
                        binding.pbLogin.visibility = View.VISIBLE
                        binding.btnLogin.visibility = View.INVISIBLE
                    }
                    is LoginState.Success -> {
                        binding.pbLogin.visibility = View.GONE
                        binding.btnLogin.visibility = View.VISIBLE
                        Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                        //Navigation.findNavController(requireView()).navigate(R.id.action_loginFragment_to_homeFragment)

                    }
                    is LoginState.Error -> {
                        binding.pbLogin.visibility = View.GONE
                        binding.btnLogin.visibility = View.VISIBLE
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
            Navigation.findNavController(requireView()).navigate(R.id.action_loginFragment_to_registerFragment)
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