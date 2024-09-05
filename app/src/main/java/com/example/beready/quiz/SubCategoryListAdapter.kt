package com.example.beready.quiz

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.beready.databinding.SubCategoryItemBinding


class SubCategoryListAdapter(
    private val subCategories: List<SubCategoryModel>,
    private val onItemClick: (SubCategoryModel) -> Unit
) : RecyclerView.Adapter<SubCategoryListAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: SubCategoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(subCategory: SubCategoryModel) {
            binding.subCategoryTitle.text = subCategory.setTitle
            binding.root.setOnClickListener {
                onItemClick(subCategory)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SubCategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(subCategories[position])
    }

    override fun getItemCount() = subCategories.size
}
