package com.muratdayan.lessonline.presentation.features.auth.forgetpassword

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
import com.muratdayan.lessonline.databinding.FragmentForgetPasswordBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ForgetPasswordFragment : BaseFragment() {

    private var _binding: FragmentForgetPasswordBinding? = null
    private val binding get() = _binding!!

    private val forgetPasswordViewModel: ForgetPasswordViewModel by viewModels()

    private var hasFragmentOpened = false


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
            val isEmailEmpty = binding.etEmail.doIfIsEmptyAndReturn {
                showToast("email is empty",false)
            }
            if (!isEmailEmpty){
                val email = binding.etEmail.text.toString().trim()
                forgetPasswordViewModel.resetPassword(email)
            }
        }

        lifecycleScope.launch {
            forgetPasswordViewModel.resetResult.collectLatest { result->
                if (hasFragmentOpened){
                    if (result){
                        showToast("Email sent",false)
                        Navigation.goBack(requireView())
                    }else{
                        showToast("Email is not registered",true)
                    }
                }else{
                    hasFragmentOpened = true
                }
            }
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