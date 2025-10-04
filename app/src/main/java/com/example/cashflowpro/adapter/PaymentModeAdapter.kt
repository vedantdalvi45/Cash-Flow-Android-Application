package com.example.cashflowpro.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.cashflowpro.R
import com.example.cashflowpro.data.model.Category
import com.example.cashflowpro.data.model.PaymentMode
import com.example.cashflowpro.databinding.ItemAccountBinding

class PaymentModeAdapter : RecyclerView.Adapter<PaymentModeAdapter.AccountViewHolder>() {

    inner class AccountViewHolder(val binder: ItemAccountBinding) :
        RecyclerView.ViewHolder(binder.root) {
        fun bind(paymentMode: PaymentMode) {
            binder.tvAccountName.text = paymentMode.modeName
            binder.tvAccountBalance.text =
                if (paymentMode.balance == -1.0) "*****" else "₹${paymentMode.balance}"

            binder.paymentModeContainer.setOnClickListener {
                onItemClick?.invoke(paymentMode)
            }
        }
    }
    var onItemClick: ((PaymentMode) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val binding = ItemAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = AccountViewHolder(binding)
        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onItemClick?.invoke(asyncListDiffer.currentList[position])
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        holder.bind(asyncListDiffer.currentList[position])
    }

    override fun getItemCount() = asyncListDiffer.currentList.size

    private val differCallback = object : DiffUtil.ItemCallback<PaymentMode>() {
        override fun areItemsTheSame(oldItem: PaymentMode, newItem: PaymentMode): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PaymentMode, newItem: PaymentMode): Boolean {
            return oldItem == newItem // Data class 'equals' checks all properties
        }
    }

    private val asyncListDiffer = AsyncListDiffer(this, differCallback)

    fun submitList(newList: List<PaymentMode>) {
        asyncListDiffer.submitList(newList)
    }
}