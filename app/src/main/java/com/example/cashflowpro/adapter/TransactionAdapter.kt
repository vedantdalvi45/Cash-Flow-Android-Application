package com.example.cashflowpro.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cashflowpro.R
import com.example.cashflowpro.databinding.ListItemTransactionBinding
import com.example.cashflowpro.data.model.Transaction
import com.example.cashflowpro.util.CategoryStorage
import com.example.cashflowpro.util.PaymentModeStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionAdapter() :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    val formatter = SimpleDateFormat("hh:mma    dd MMM yy", Locale.US)

    inner class TransactionViewHolder(private val binding: ListItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: Transaction) {
            binding.textAmount.text = "₹ ${transaction.amount}"
            binding.textDescription.text = transaction.description
            binding.textDate.text = formatter.format(transaction.time).toString()


            if (transaction.type == "TRANSFER") {
                binding.iconCategory.setImageResource(R.drawable.ic_money_transfer)
                binding.layoutTransaction.setBackgroundColor(Color.parseColor("#23895CF2"))
            } else {
                var category = CategoryStorage.loadCategories(itemView.context)
                    .find { it.id == transaction.categoryId }
                Glide.with(binding.root.context)
                    .load(category!!.imageUrl)
                    .placeholder(R.drawable.icon_category) // A default image while loading
                    .error(R.drawable.ic_categories) // An image to show if loading fails
                    .into(binding.iconCategory)

            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding =
            ListItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) =
        holder.bind(asyncListDiffer.currentList[position])

    override fun getItemCount() = asyncListDiffer.currentList.size

    //AsyncListDiffer Implementation
    private val differCallback = object : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(
            oldItem: Transaction,
            newItem: Transaction
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Transaction,
            newItem: Transaction
        ): Boolean {
            return oldItem == newItem
        }
    }

    val asyncListDiffer = AsyncListDiffer(this, differCallback)
    fun submitList(list: List<Transaction>) {
        asyncListDiffer.submitList(list)
    }
}