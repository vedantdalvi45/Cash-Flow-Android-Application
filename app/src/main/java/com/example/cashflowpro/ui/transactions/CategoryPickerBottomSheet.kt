package com.example.cashflowpro.ui.transactions

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cashflowpro.R
import com.example.cashflowpro.adapter.CategoryAdapter
import com.example.cashflowpro.adapter.CategoryAdapterForTransaction
import com.example.cashflowpro.data.model.Category
import com.example.cashflowpro.databinding.BottomSheetCategoryPickerBinding
import com.example.cashflowpro.ui.more.CategoriesActivity
import com.example.cashflowpro.util.CategoryStorage
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

const val TAG = "CategoryPickerBottomSheet"

class CategoryPickerBottomSheet(val categories: List<Category>) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetCategoryPickerBinding? = null
    private val binding get() = _binding!!

    private var isGrid = true
    private lateinit var categoryAdapter: CategoryAdapterForTransaction

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetCategoryPickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let {
            setupRecyclerView(it)
        }

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.btnViewToggle.setOnClickListener {
            isGrid = !isGrid
            setLayoutManager()
            val iconRes = if (isGrid) R.drawable.ic_list else R.drawable.ic_categories
            binding.btnViewToggle.setImageResource(iconRes)
        }

        binding.btnEditCategories.setOnClickListener {
            val intent = Intent(requireContext(), CategoriesActivity::class.java)
            startActivity(intent)
            dismiss()
        }
    }

    private fun setupRecyclerView(context: Context) {
        categoryAdapter = CategoryAdapterForTransaction { selectedCategory ->
            Log.d(TAG, "Selected category: $selectedCategory")
            dismiss()
        }
        binding.recyclerViewCategories.adapter = categoryAdapter
        setLayoutManager()

        categoryAdapter.submitList(categories)
    }

    private fun setLayoutManager() {
        binding.recyclerViewCategories.layoutManager =
            if (isGrid) GridLayoutManager(requireContext(), 4)
            else LinearLayoutManager(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
