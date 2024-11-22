package com.muratdayan.lessonline.presentation.features.main.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.internal.ViewUtils.hideKeyboard
import com.google.android.material.textfield.TextInputLayout
import com.muratdayan.core.presentation.BaseFragment
import com.muratdayan.lessonline.databinding.FragmentSearchBinding
import com.muratdayan.lessonline.presentation.adapter.BasicPostListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : BaseFragment() {

    private var _binding : FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var basicPostListAdapter: BasicPostListAdapter

    private val searchViewModel : SearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentSearchBinding.inflate(inflater,container,false)
        return binding.root
    }

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tilSearch.endIconMode = TextInputLayout.END_ICON_NONE

        basicPostListAdapter = BasicPostListAdapter(){postId->
            val action = SearchFragmentDirections.actionSearchFragmentToPostDetailFragment(postId)
            Navigation.findNavController(requireView()).navigate(action)
        }

        binding.rvSearchPosts.apply {
            layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
            adapter = basicPostListAdapter
        }

        lifecycleScope.launch {
            searchViewModel.searchedPostList.collectLatest {searchedPostList->
                basicPostListAdapter.submitList(searchedPostList)
            }
        }

        lifecycleScope.launch {
            searchViewModel.postList.collectLatest { postList ->
                basicPostListAdapter.submitList(postList)

                if(postList.isEmpty()){
                    binding.evSearchedPosts.visibility = View.VISIBLE
                }else{
                    binding.evSearchedPosts.visibility = View.GONE
                }
            }
        }

        binding.etSearch.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val query = s.toString()
                if (s != null) {
                    if(s.isNotBlank()){
                        binding.tilSearch.endIconMode = TextInputLayout.END_ICON_CUSTOM
                    }
                }
                searchViewModel.searchPosts(query)
            }

            override fun afterTextChanged(p0: Editable?) {
                binding.tilSearch.setEndIconOnClickListener {
                    binding.etSearch.text?.clear()
                    binding.tilSearch.endIconMode = TextInputLayout.END_ICON_NONE

                }
            }
        })

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.etSearch.text.toString().trim() // Kullanıcıdan gelen metni temizle
                if (query.isNotBlank()) {
                    searchViewModel.searchPosts(query) // Arama işlemini başlat
                    binding.tilSearch.endIconMode = TextInputLayout.END_ICON_NONE
                    hideKeyboard(requireView()) // Klavyeyi kapat
                    true // Olay işlenmiş olarak işaretle
                } else {
                    binding.tilSearch.endIconMode = TextInputLayout.END_ICON_NONE
                    hideKeyboard(requireView()) // Klavyeyi kapat
                    false // Olayı işlenmemiş bırak
                }
            } else {
                false // Diğer action'ları işleme
            }
        }

        searchViewModel.fetchPosts()

        binding.tilSearch.setEndIconOnClickListener {
            binding.etSearch.text?.clear()
            binding.tilSearch.endIconMode = TextInputLayout.END_ICON_NONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}