    package com.muratdayan.lessonline.presentation.features.main.bottomsheets.answers

    import android.R
    import android.os.Bundle
    import android.util.Log
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.AdapterView
    import android.widget.ArrayAdapter
    import androidx.fragment.app.viewModels
    import androidx.lifecycle.lifecycleScope
    import androidx.recyclerview.widget.LinearLayoutManager
    import com.google.android.material.bottomsheet.BottomSheetBehavior
    import com.google.android.material.bottomsheet.BottomSheetDialogFragment
    import com.muratdayan.lessonline.databinding.BottomSheetFragmentAnswersBinding
    import com.muratdayan.lessonline.presentation.adapter.AnswerAdapter
    import dagger.hilt.android.AndroidEntryPoint
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.flow.collectLatest
    import kotlinx.coroutines.launch

    @AndroidEntryPoint
    class AnswersBottomSheetFragment(
        private val postId:String
    ) : BottomSheetDialogFragment(){

        private var _binding: BottomSheetFragmentAnswersBinding?=null
        private val binding  get() = _binding!!

        private val answerViewModel: AnswerViewModel by viewModels()

        private lateinit var answerAdapter: AnswerAdapter
        private var selectedAnswer: String?=null

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View{
            _binding = BottomSheetFragmentAnswersBinding.inflate(inflater,container,false)
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            val bottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
            bottomSheetBehavior.peekHeight = 500 // Yüksekliği ihtiyacınıza göre ayarlayın
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            answerViewModel.getAnswers(postId)
            answerViewModel.getPostAnswers(postId)

            answerAdapter = AnswerAdapter()
            binding.rvAnswers.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = answerAdapter
            }

            lifecycleScope.launch(Dispatchers.Main) {
                answerViewModel.answers.collectLatest {answerList->
                    Log.d("Answers", "Answer list size: ${answerList.size}")
                    answerAdapter.submitList(answerList)
                }
            }

            lifecycleScope.launch {
                answerViewModel.postAnswers.collectLatest {postAnswers->
                    postAnswers?.let {
                        val adapter = ArrayAdapter(requireContext(),
                            R.layout.simple_spinner_item,it)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.spAnswer.adapter = adapter
                        Log.d("Answers", "Post answers size: $it")
                    }
                }
            }

            binding.ibtnSendAnswer.setOnClickListener {
                selectedAnswer?.let {
                    answerViewModel.addAnswer(postId,it)
                }
            }

            binding.spAnswer.onItemSelectedListener = object  : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedAnswer = parent?.getItemAtPosition(position) as String
                    Log.d("Answers", "Selected answer: $selectedAnswer")
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    // Handle the case where nothing is selected
                }

            }

            lifecycleScope.launch {
                answerViewModel.addResult.collectLatest {
                    if (it){
                        answerViewModel.getAnswers(postId)
                    }
                }
            }

            

        }

        override fun onDestroy() {
            super.onDestroy()
            _binding = null
        }
    }