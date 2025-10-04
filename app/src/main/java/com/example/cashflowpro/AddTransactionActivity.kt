package com.example.cashflowpro

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.example.cashflowpro.databinding.ActivityAddTransactionBinding
import com.example.cashflowpro.ui.transactions.CategoryPickerBottomSheet
import com.example.cashflowpro.ui.transactions.PaymentModePickerBottomSheet
import com.example.cashflowpro.util.CategoryStorage
import java.text.SimpleDateFormat
import java.util.Calendar

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTransactionBinding

    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
    private val timeFormat = SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        manipulateCalender()
        manipulateTransactionTabs()
        setupClickListeners()

        // Set initial state for Expense
        binding.toggleTransactionType.check(R.id.button_expense)
        setupForExpense()
    }

    private fun setupClickListeners() {
        binding.layoutPaymentMode.setOnClickListener {
            val sheet = PaymentModePickerBottomSheet()
            sheet.show(supportFragmentManager, "PaymentModePicker")
        }
    }

    private fun manipulateCalender() {
        binding.textViewDate.text = dateFormat.format(calendar.time)
        binding.textViewTime.text = timeFormat.format(calendar.time)

        binding.calenderSelector.setOnClickListener {
            val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                binding.textViewDate.text = dateFormat.format(calendar.time)
            }
            val datePickerDialog = DatePickerDialog(
                this,
                R.style.CustomDatePickerDialog,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        binding.timeSelector.setOnClickListener {
            val timeSetListener = android.app.TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                binding.textViewTime.text = timeFormat.format(calendar.time)
            }
            android.app.TimePickerDialog(
                this,
                R.style.CustomDatePickerDialog,
                timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
            ).show()
        }
    }

    private fun manipulateTransactionTabs() {
        binding.toggleTransactionType.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.button_expense -> setupForExpense()
                    R.id.button_income -> setupForIncome()
                    R.id.button_transfer -> setupForTransfer()
                }
            }
        }
    }

    private fun setupForExpense() {
        binding.textViewCategoryLabel.text = "Category"
        binding.textViewCategoryValue.text = "Others"
        binding.categoryIcon.setImageResource(R.drawable.ic_categories)
        binding.paymentModeLabel.text = "Payment Mode"
        binding.textViewPaymentModeValue.text = "Cash"
        binding.layoutCategory.setOnClickListener {
            val sheet = CategoryPickerBottomSheet(CategoryStorage.loadCategories(this@AddTransactionActivity).filter { it.categoryType == "EXPENSE" })
            sheet.show(supportFragmentManager, "CategoryPicker")
        }
    }

    private fun setupForIncome() {
        binding.textViewCategoryLabel.text = "Category"
        binding.textViewCategoryValue.text = "Others"
        binding.categoryIcon.setImageResource(R.drawable.ic_categories)
        binding.paymentModeLabel.text = "Payment Mode"
        binding.textViewPaymentModeValue.text = "Cash"
        binding.layoutCategory.setOnClickListener {
            val sheet = CategoryPickerBottomSheet(CategoryStorage.loadCategories(this@AddTransactionActivity).filter { it.categoryType == "INCOME" })
            sheet.show(supportFragmentManager, "CategoryPicker")
        }
    }

    private fun setupForTransfer() {
        binding.textViewCategoryLabel.text = "From"
        binding.textViewCategoryValue.text = "Cash"
        binding.categoryIcon.setImageResource(R.drawable.money)
        binding.paymentModeLabel.text = "To"
        binding.textViewPaymentModeValue.text = "Cash"
        binding.layoutCategory.setOnClickListener {
            // Open a bottom sheet to select the 'From' account, e.g., PaymentModePickerBottomSheet
            val sheet = PaymentModePickerBottomSheet()
            sheet.show(supportFragmentManager, "PaymentModePicker")
        }
        // The 'To' layout (layoutPaymentMode) should also open a picker.
        binding.layoutPaymentMode.setOnClickListener {
            val sheet = PaymentModePickerBottomSheet()
            sheet.show(supportFragmentManager, "PaymentModePicker")
        }
    }
}
