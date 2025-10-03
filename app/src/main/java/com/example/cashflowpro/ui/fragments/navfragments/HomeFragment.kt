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
//        addTransactions()
        transactionAdapter.submitList(transactions)

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}