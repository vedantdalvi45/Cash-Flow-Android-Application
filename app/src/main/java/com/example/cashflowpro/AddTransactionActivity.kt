package com.example.cashflowpro

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
    private val dateFormat = SimpleDateFormat("dd MMM yyyy")
    private val timeFormat = SimpleDateFormat("hh:mm a")

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
        binding.layoutCategory.setOnClickListener {
            val sheet = CategoryPickerBottomSheet(CategoryStorage.loadCategories(this@AddTransactionActivity).filter { it.categoryType == "EXPENSE" })
            sheet.show(supportFragmentManager, "CategoryPicker")
        }
        binding.layoutPaymentMode.setOnClickListener {
            val sheet = PaymentModePickerBottomSheet()
            sheet.show(supportFragmentManager, "PaymentModePicker")
        }
    }
    private fun manipulateCalender(){
        binding.textViewDate.text = dateFormat.format(calendar.time)
        binding.textViewTime.text = timeFormat.format(calendar.time)

        binding.calenderSelector.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    binding.textViewTime.text = dateFormat.format(calendar.time)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.setOnDateSetListener { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                binding.textViewDate.text = dateFormat.format(calendar.time)
            }
            datePickerDialog.show()
        }
        binding.timeSelector.setOnClickListener {
            val timePickerDialog = TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    binding.textViewTime.text = timeFormat.format(calendar.time)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
            )
            timePickerDialog.show()
        }
    }
    private fun manipulateTransactionTabs(){
        binding.toggleTransactionType.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.button_expense -> { /* Handle Expense selection */
                        binding.textViewCategoryLabel.text = "Category"
                        binding.textViewCategoryValue.text = "Others"
                        binding.categoryIcon.setImageResource(R.drawable.icon_category)
                        binding.paymentModeLabel.text = "Payment Mode"
                        binding.textViewPaymentModeValue.text = "Cash"
                        binding.layoutCategory.setOnClickListener {
                            val sheet = CategoryPickerBottomSheet(CategoryStorage.loadCategories(this@AddTransactionActivity).filter { it.categoryType == "EXPENSE" })
                            sheet.show(supportFragmentManager, "CategoryPicker")
                        }
                    }
                    R.id.button_income -> { /* Handle Income selection */
                        binding.textViewCategoryLabel.text = "Category"
                        binding.textViewCategoryValue.text = "Others"
                        binding.categoryIcon.setImageResource(R.drawable.icon_category)
                        binding.paymentModeLabel.text = "Payment Mode"
                        binding.textViewPaymentModeValue.text = "Cash"
                        binding.layoutCategory.setOnClickListener {
                            val sheet = CategoryPickerBottomSheet(CategoryStorage.loadCategories(this@AddTransactionActivity).filter { it.categoryType == "INCOME" })
                            sheet.show(supportFragmentManager, "CategoryPicker")
                        }
                    }
                    R.id.button_transfer -> { /* Handle Transfer selection */
                        binding.textViewCategoryLabel.text = "From"
                        binding.textViewCategoryValue.text = "Cash"
                        binding.categoryIcon.setImageResource(R.drawable.money)
                        binding.paymentModeLabel.text = "To"
                        binding.textViewPaymentModeValue.text = "Cash"
                        binding.layoutCategory.setOnClickListener {

                        }
                    }
                }
            }
        }
    }
}
