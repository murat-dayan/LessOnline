    package com.muratdayan.lessonline.presentation.features.main.bottomsheets.answers

    import android.os.Bundle
    import android.util.Log
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
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

            binding.ibtnSendAnswer.setOnClickListener {
                val answerText = binding.etAnswer.text.toString().trim()
                if (answerText.isNotEmpty()){
                    answerViewModel.addAnswer(postId,answerText)
                    binding.etAnswer.text.clear()
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