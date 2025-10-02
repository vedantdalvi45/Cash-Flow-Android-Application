package com.example.cashflowpro.ui.fragments.navfragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.cashflowpro.R
import com.example.cashflowpro.adapter.TransactionAdapter
import com.example.cashflowpro.databinding.FragmentHomeBinding
import com.example.cashflowpro.data.model.Category
import com.example.cashflowpro.data.model.PaymentMode
import com.example.cashflowpro.data.model.Transaction
import java.util.concurrent.TimeUnit
class HomeFragment : Fragment() {

    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var transactions: MutableList<Transaction>

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        val list = mutableListOf("This Month", "This Year", "All Time")
        val adapter = object : ArrayAdapter<String>(requireContext(), R.layout.custom_spinner_item, list) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                (view.findViewById<TextView>(android.R.id.text1))?.text = ""

                return view
            }
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.optionsSpinner.adapter = adapter

        binding.optionsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                binding.monthSelectorText.text = list[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recentTransactions()
    }

    private fun recentTransactions() {
        transactions = ArrayList()

        binding.transactionsRecyclerView.apply {
            transactionAdapter = TransactionAdapter()
            adapter =  transactionAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        addTransactions()
        transactionAdapter.submitList(transactions)

    }

    private fun addTransactions() {
        val hdfc_bank = PaymentMode( "Bank", "HDFC Bank", 50250.50)
        val sbi_bank = PaymentMode( "Bank", "SBI Bank", 75000.00)
        val shop_cash = PaymentMode( "Cash", "Shop Cash", 2500.00)

        val catShopping = Category(name = "Shopping", iconResId = R.drawable.analytics, categoryType = "Expense")
        val catTravel = Category(name = "Travel", iconResId = R.drawable.money, categoryType = "Expense")
        val catBills = Category(name = "Bills", iconResId = R.drawable.analytics, categoryType = "Expense")
        val catSalary = Category(name = "Salary", iconResId = R.drawable.money, categoryType = "Income")
        val catFreelance = Category(name = "Freelance", iconResId = R.drawable.bank, categoryType = "Income")
        val catTransfer = Category(name = "Transfer", iconResId = R.drawable.arrow, categoryType = "Transfer")
        transactions.addAll(listOf(
            Transaction(
                id = 1,
                amount = "250.00",
                description = "Lunch with colleagues",
                timestamp = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1),
                category = catShopping,
                paymentMethod = hdfc_bank,
                balance = 50250.50,
                transactionType = "Expense",
                tags = listOf("food", "office", "lunch"),
                attachment = ""
            ),
            // Income Transaction
            Transaction(
                id = 2,
                amount = "65000.00",
                description = "September Salary",
                timestamp = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(2), // 2 days ago
                category = catSalary,
                paymentMethod = hdfc_bank,
                balance = 50500.50, // Balance before the lunch expense above
                transactionType = "Income",
                tags = listOf("salary", "monthly"),
                attachment = "/path/to/payslip.pdf"
            ),
            // Shopping Expense Transaction
            Transaction(
                id = 3,
                amount = "3499.00",
                description = "New headphones",
                timestamp = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(3), // 3 days ago
                category = catShopping,
                paymentMethod = sbi_bank,
                balance = 75000.00,
                transactionType = "Expense",
                tags = listOf("electronics", "gadget"),
                attachment = "/path/to/invoice.jpg"
            ),
            // Transfer Transaction
            Transaction(
                id = 4,
                amount = "5000.00",
                description = "Moved to SBI account",
                timestamp = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(5), // 5 hours ago
                category = catTransfer,
                paymentMethod = hdfc_bank,
                balance = 45250.50,
                transactionType = "Transfer",
                tags = listOf("self", "bank transfer"),
                attachment = ""
            )

        ))

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}