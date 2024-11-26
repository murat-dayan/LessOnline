package com.muratdayan.lessonline.presentation.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.muratdayan.lessonline.databinding.ItemFilterBinding
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter

class FilterAdapter(
    private val filters: List<GPUImageFilter>,
    private val originalBitmap: Bitmap,
    private val onFilterSelected: (GPUImageFilter) -> Unit

): RecyclerView.Adapter<FilterAdapter.FilterViewHolder>() {

    inner class FilterViewHolder(private val binding: ItemFilterBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(filter: GPUImageFilter){
            val gpuImage = GPUImage(binding.root.context)
            gpuImage.setFilter(filter)



            gpuImage.setImage(originalBitmap)
            val filteredBitmap = gpuImage.bitmapWithFilterApplied

            binding.filterImageView.setImageBitmap(filteredBitmap)

            binding.root.setOnClickListener {
                onFilterSelected(filter)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val binding = ItemFilterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FilterViewHolder(binding)
    }

    override fun getItemCount(): Int = filters.size

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        val filter = filters[position]
        holder.bind(filter)
    }
}