package com.muratdayan.core.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.muratdayan.core.R
import com.muratdayan.core.databinding.CoreCustomEmptyViewBinding

@SuppressLint("CustomViewStyleable")
class EmptyView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context,attrs,defStyleAttr){

    private val binding: CoreCustomEmptyViewBinding

    init {
        val inflater = LayoutInflater.from(context)
        binding = CoreCustomEmptyViewBinding.inflate(inflater, this, true)

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CoreCustomEmptyViewStyleable, defStyleAttr, 0)
            val message = typedArray.getString(R.styleable.CoreCustomEmptyViewStyleable_emptyMessage) ?: ""

            setMessage(message)
            typedArray.recycle()
        }
    }

    fun setMessage(message: String){
        binding.tvEmptyMessage.text = message
    }
}