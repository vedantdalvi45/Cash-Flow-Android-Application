package com.example.cashflowpro.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.cashflowpro.databinding.ListItemTransactionBinding
import com.example.cashflowpro.model.Transaction
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionAdapter() :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    inner class TransactionViewHolder(private val binding: ListItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: Transaction) {
            binding.textAmount.text = "₹ ${transaction.amount}"
            binding.textDescription.text = transaction.description
            binding.textDate.text = dateFormat.format(transaction.date)
            // binding.iconCategory.setImageResource(...) // Set category icon based on transaction type or category
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding =
            ListItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) =
        holder.bind(transactionList[position])

    override fun getItemCount() = transactionList.size

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

    private val asyncListDiffer = AsyncListDiffer(this, differCallback)
    var transactionList: MutableList<Transaction>
        get() =asyncListDiffer.currentList
        set(value) = asyncListDiffer.submitList(value)

    fun submitList(list : MutableList<Transaction>){
        asyncListDiffer.submitList(list)
    }
}