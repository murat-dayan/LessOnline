package com.muratdayan.lessonline.presentation.features.main.premium

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.muratdayan.lessonline.R
import com.muratdayan.lessonline.databinding.FragmentPremiumBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PremiumFragment : Fragment() {

    private var _binding: FragmentPremiumBinding? = null
    private val binding get() = _binding!!

    private val premiumViewModel: PremiumViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentPremiumBinding.inflate(inflater,container,false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            premiumViewModel.purchaseCompleted.collectLatest { isCompleted ->
                if (isCompleted){
                    // Premium feature unlocked
                    Toast.makeText(requireContext(),"Premium feature unlocked",Toast.LENGTH_SHORT).show()
                }
                else{
                    // Premium feature not unlocked
                    Toast.makeText(requireContext(),"Premium feature not unlocked",Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.purchaseButton.setOnClickListener {
            premiumViewModel.initiatePurchase()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}