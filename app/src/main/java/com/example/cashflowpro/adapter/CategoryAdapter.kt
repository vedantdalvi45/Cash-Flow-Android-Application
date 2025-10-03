package com.example.cashflowpro.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.example.cashflowpro.R
import com.example.cashflowpro.data.model.Category
import com.example.cashflowpro.databinding.ItemCategoryBinding

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class CategoryAdapter : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(private val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {
            binding.tvCategoryName.text = category.name

            // Use Glide to load the image from the URL
            Glide.with(binding.root.context)
                .load(category.imageUrl)
                .placeholder(R.drawable.icon_category) // A default image while loading
                .error(R.drawable.icon_category) // An image to show if loading fails
                .into(binding.ivCategoryIcon)

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding =
            ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }


    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(asyncListDiffer.currentList[position])
    }


    override fun getItemCount(): Int = asyncListDiffer.currentList.size


    private val differCallback = object : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem // Data class 'equals' checks all properties
        }
    }

    private val asyncListDiffer = AsyncListDiffer(this, differCallback)

    fun submitList(newList: List<Category>) {
        asyncListDiffer.submitList(newList)
        notifyDataSetChanged()
    }

}
