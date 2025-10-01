package com.example.cashflowpro.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cashflowpro.R
import com.example.cashflowpro.model.PaymentMode
import com.example.cashflowpro.model.Transaction

class AccountAdapter(private val accountList: List<PaymentMode>) :
    RecyclerView.Adapter<AccountAdapter.AccountViewHolder>() {

    class AccountViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val accountName: TextView = itemView.findViewById(R.id.tvAccountName)
        val accountBalance: TextView = itemView.findViewById(R.id.tvAccountBalance)
        val arrowIcon: ImageView = itemView.findViewById(R.id.ivAccountArrow)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_account, parent, false)
        return AccountViewHolder(view)
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        val account = accountList[position]
        holder.accountName.text = account.name
        holder.accountBalance.text = account.balance.toString()
        holder.itemView.setOnClickListener {
        }
    }
    override fun getItemCount() = accountList.size
}