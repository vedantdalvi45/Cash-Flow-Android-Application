package com.example.cashflowpro.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cashflowpro.R
import com.example.cashflowpro.data.model.Category
import com.example.cashflowpro.databinding.ItemCategoryGridBinding

class CategoryAdapterForTransaction: RecyclerView.Adapter<CategoryAdapterForTransaction.CategoryViewHolder>() {

    var onItemClick: ((Category) -> Unit)? = null

    inner class CategoryViewHolder(private val binder: ItemCategoryGridBinding) : RecyclerView.ViewHolder(binder.root) {
        fun bind(category: Category) {
            binder.txtName.text = category.name
            Glide.with(binder.root.context)
                .load(category.imageUrl)
                .placeholder(R.drawable.icon_category) // A default image while loading
                .error(R.drawable.icon_category) // An image to show if loading fails
                .into(binder.imgIcon)

            binder.root.setOnClickListener {
                onItemClick?.invoke(category)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binder = ItemCategoryGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binder)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(asyncListDiffer.currentList[position])
    }

    override fun getItemCount() = asyncListDiffer.currentList.size

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
