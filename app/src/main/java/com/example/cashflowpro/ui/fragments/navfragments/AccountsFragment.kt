package com.example.cashflowpro.ui.fragments.navfragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cashflowpro.adapter.AccountAdapter
import com.example.cashflowpro.R
import com.example.cashflowpro.data.model.PaymentMode


class AccountsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var accountAdapter: AccountAdapter
    private lateinit var accountList: ArrayList<PaymentMode>

    private lateinit var addAccountTextView: TextView
    private lateinit var accountsCountTV: TextView

    private lateinit var linearLayoutManager: LinearLayoutManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_accounts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.accountsRecyclerView)
        linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager

        addAccountTextView = view.findViewById(R.id.tvAddAccount)
        accountsCountTV = view.findViewById<TextView>(R.id.accounts_count)

        accountList = ArrayList()
        prepareAccountListData()

        accountAdapter = AccountAdapter(accountList)
        recyclerView.adapter = accountAdapter

        addAccount()
    }


    private fun prepareAccountListData() {
        val hdfc_bank = PaymentMode( "Bank", "HDFC Bank", 50250.50)
        val sbi_bank = PaymentMode( "Bank", "SBI Bank", 75000.00)
        val shop_cash = PaymentMode( "Cash", "Shop Cash", 2500.00)
        accountList.addAll(listOf(hdfc_bank, sbi_bank))
        accountsCountTV.text = accountList.size.toString()
    }

    private fun addAccount() {
        addAccountTextView.setOnClickListener {

        }
    }
}


