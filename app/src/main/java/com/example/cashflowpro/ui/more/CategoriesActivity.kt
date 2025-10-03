package com.example.cashflowpro.ui.more

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.cashflowpro.R
import com.example.cashflowpro.adapter.CategoryAdapter
import com.example.cashflowpro.data.model.Category
import com.example.cashflowpro.databinding.ActivityCategoriesBinding
import com.example.cashflowpro.util.CategoryStorage
import com.example.cashflowpro.util.Resource

const val TAG = "CategoriesActivity"

class CategoriesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoriesBinding
    private val viewModel: CategoryViewModel by viewModels()
    private lateinit var categoryAdapter: CategoryAdapter

    private val categoryListGlobal = mutableListOf<Category>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeViewModel()

        // Load offline data
        val savedCategories = CategoryStorage.loadCategories(this)
        if (savedCategories.isNotEmpty()) {
            categoryListGlobal.clear()
            categoryListGlobal.addAll(savedCategories)
            updateResultText(binding.toggleButtonGroup.checkedButtonId)
            binding.recyclerViewCategories.visibility = View.VISIBLE
            showShimmer(false)
        }

        // Always fetch the latest from ViewModel
        viewModel.fetchCategories()

        binding.toggleButtonGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                updateResultText(checkedId)
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        categoryAdapter = CategoryAdapter()
        binding.recyclerViewCategories.apply {
            adapter = categoryAdapter
            layoutManager = GridLayoutManager(this@CategoriesActivity, 2)
        }
    }

    private fun observeViewModel() {
        viewModel.categories.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    showShimmer(true)
                }
                is Resource.Success -> {
                    resource.data?.let { newList ->
                        if (!CategoryStorage.areListsEqual(newList, categoryListGlobal)) {
                            Log.d(TAG, "New data detected. Updating UI and storage.")
                            categoryListGlobal.clear()
                            categoryListGlobal.addAll(newList)
                            CategoryStorage.saveCategories(this, newList)
                            updateResultText(binding.toggleButtonGroup.checkedButtonId)
                        } else {
                            Log.d(TAG, "No changes detected. Skipping update.")
                        }
                        showShimmer(false)
                        binding.recyclerViewCategories.visibility = View.VISIBLE
                    }
                }
                is Resource.Error -> {
                    showShimmer(false)
                    Log.e(TAG, "Error fetching categories: ${resource.message}")
                }
            }
        }
    }

    private fun updateResultText(checkedId: Int) {
        when (checkedId) {
            R.id.btnExpense -> {
                categoryAdapter.submitList(categoryListGlobal.filter { it.categoryType == "EXPENSE" })
            }
            R.id.btnIncome -> {
                categoryAdapter.submitList(categoryListGlobal.filter { it.categoryType == "INCOME" })
            }
        }
    }

    private fun showShimmer(isLoading: Boolean) {
        if (isLoading) {
            binding.shimmerViewContainer.startShimmer()
            binding.shimmerViewContainer.visibility = View.VISIBLE
            binding.recyclerViewCategories.visibility = View.GONE
        } else {
            binding.shimmerViewContainer.stopShimmer()
            binding.shimmerViewContainer.visibility = View.GONE
            binding.recyclerViewCategories.visibility = View.VISIBLE
        }
    }
}
