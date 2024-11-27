package com.muratdayan.lessonline.presentation.features.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.google.android.material.tabs.TabLayoutMediator
import com.muratdayan.core.presentation.BaseFragment
import com.muratdayan.lessonline.R
import com.muratdayan.lessonline.databinding.FragmentHomeBinding
import com.muratdayan.lessonline.presentation.features.main.home.adapter.HomeViewPagerAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpPagerWithTabs()

        /*homeViewModel.adRequest.observe(viewLifecycleOwner){adRequest->
            binding.adView.loadAd(adRequest)
        }

        binding.adView.adListener = object : AdListener(){
            override fun onAdLoaded() {
                super.onAdLoaded()
                Toast.makeText(requireContext(),"reklam yüklendi",Toast.LENGTH_SHORT).show()
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                Log.e("HomeFragment",p0.message)
            }

        }*/


        binding.ibtnDirectMessages.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(R.id.action_homeFragment_to_chat_graph)
        }

    }

    private fun setUpPagerWithTabs(){
        val adapter = HomeViewPagerAdapter(this){userId->
            val action = HomeFragmentDirections.actionHomeFragmentToOtherProfileFragment(userId)
            Navigation.findNavController(requireView()).navigate(action)
        }
        binding.vpHome.adapter = adapter

        TabLayoutMediator(binding.tlHome,binding.vpHome){tab,position->
            tab.text = when(position){
                0-> "All Posts"
                1-> "Following Posts"
                else -> null
            }
        }.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}