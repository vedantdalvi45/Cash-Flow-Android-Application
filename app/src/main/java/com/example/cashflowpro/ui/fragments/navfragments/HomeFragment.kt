package com.example.cashflowpro.ui.fragments.navfragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cashflowpro.R
import com.example.cashflowpro.adapter.TransactionAdapter
import com.example.cashflowpro.databinding.FragmentHomeBinding
import com.example.cashflowpro.util.TransactionStorage
import java.util.Calendar


class HomeFragment : Fragment() {
    val TAG: String = "HomeFragment"
    private lateinit var transactionAdapter: TransactionAdapter

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

//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.optionsSpinner.adapter = adapter

        binding.optionsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                binding.monthSelectorText.text = list[position]
                if (position == 0) {
                    val transactionList = TransactionStorage.loadTransactions(requireContext())
                    val calendar = Calendar.getInstance()
                    val currentMonth = calendar.get(Calendar.MONTH)
                    val currentYear = calendar.get(Calendar.YEAR)

                    val monthlySpending = transactionList.filter {
                        val transactionCalendar = Calendar.getInstance().apply { time = it.time }
                        transactionCalendar.get(Calendar.MONTH) == currentMonth && transactionCalendar.get(Calendar.YEAR) == currentYear
                    }.sumOf { it.amount }

                    val monthlyIncome = transactionList.filter {
                        val transactionCalendar = Calendar.getInstance().apply { time = it.time }
                        transactionCalendar.get(Calendar.MONTH) == currentMonth && transactionCalendar.get(Calendar.YEAR) == currentYear
                        && it.type == "INCOME"
                    }.sumOf { it.amount }
                    binding.spendingAmmountTv.text = "$monthlySpending"
                    binding.incomeAmmountTv.text = "$monthlyIncome"
                    // Handle "This Month" option
                } else if (position == 1) {
                    val transactionList = TransactionStorage.loadTransactions(requireContext())
                    val calendar = Calendar.getInstance()
                    val currentYear = calendar.get(Calendar.YEAR)

                    val yearlySpending = transactionList.filter {
                        val transactionCalendar = Calendar.getInstance().apply { time = it.time }
                        transactionCalendar.get(Calendar.YEAR) == currentYear
                    }.sumOf { it.amount }

                    val yearlyIncome = transactionList.filter {
                        val transactionCalendar = Calendar.getInstance().apply { time = it.time }
                        transactionCalendar.get(Calendar.YEAR) == currentYear
                        && it.type == "INCOME"
                    }.sumOf { it.amount }

                    binding.spendingAmmountTv.text = "$yearlySpending"
                    binding.incomeAmmountTv.text = "$yearlyIncome"
                    // Handle "This Year" option
                } else {
                    val transactionList = TransactionStorage.loadTransactions(requireContext())
                    val totalSpending = transactionList.sumOf { it.amount }
                    binding.spendingAmmountTv.text = "$totalSpending"

                    val totalIncome = transactionList.filter { it.type == "INCOME" }.sumOf { it.amount }
                    binding.incomeAmmountTv.text = "$totalIncome"
                    // Handle "All Time" option
                }
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

        binding.transactionsRecyclerView.apply {
            transactionAdapter = TransactionAdapter(requireContext())
            adapter =  transactionAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        Log.d(TAG, "recentTransactions: ${TransactionStorage.loadTransactions(requireContext())}")
        var list = TransactionStorage.loadTransactions(requireContext()).toMutableList()
        transactionAdapter.submitList()
    }

    override fun onResume() {
        super.onResume()
        transactionAdapter.submitList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}