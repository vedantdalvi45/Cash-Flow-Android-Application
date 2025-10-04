package com.example.cashflowpro

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import androidx.core.view.WindowCompat
import com.example.cashflowpro.data.model.Category
import com.example.cashflowpro.data.model.PaymentMode
import com.example.cashflowpro.data.model.Transaction
import com.example.cashflowpro.databinding.ActivityAddTransactionBinding
import com.example.cashflowpro.ui.transactions.CategoryPickerBottomSheet
import com.example.cashflowpro.ui.transactions.PaymentModePickerBottomSheet
import com.example.cashflowpro.util.CategoryStorage
import com.example.cashflowpro.util.TransactionStorage
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Calendar
import java.util.Date

const val TAG = "AddTransactionActivity"

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTransactionBinding

    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
    private val timeFormat = SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())

    private var category: Category? = null
    private var fromPaymentMode: PaymentMode? = null
    private var toPaymentMode: PaymentMode? = null
    private var type: String = "EXPENSE"

    private var selectedDate: Date = Date.from(Instant.now())


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
        setupPaymentModeClickListeners()

        // Set initial state for Expense
        binding.toggleTransactionType.check(R.id.button_expense)
        setupForExpense()

        saveTransaction()
    }

    private fun setupPaymentModeClickListeners() {
        binding.layoutPaymentMode.setOnClickListener {
            val sheet = PaymentModePickerBottomSheet(
                onPaymentModeSelected = { selectedPaymentMode ->
                    binding.textViewPaymentModeValue.text = selectedPaymentMode.modeName
                    toPaymentMode = selectedPaymentMode
                }
            )
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
                selectedDate = calendar.time
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
            val timeSetListener =
                android.app.TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    binding.textViewTime.text = timeFormat.format(calendar.time)
                    selectedDate = calendar.time
                }
            android.app.TimePickerDialog(
                this,
                R.style.CustomDatePickerDialog,
                timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
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
        Glide.with(this)
            .load(R.drawable.ic_categories)
            .into(binding.categoryIcon)
        binding.paymentModeLabel.text = "Payment Mode"
        binding.textViewPaymentModeValue.text = "Cash"
        binding.layoutCategory.setOnClickListener {
            val sheet = CategoryPickerBottomSheet(
                CategoryStorage.loadCategories(this@AddTransactionActivity)
                    .filter { it.categoryType == "EXPENSE" },
                onCategorySelected = { selectedCategory ->
                    binding.textViewCategoryValue.text = selectedCategory.name
                    Glide.with(this)
                        .load(selectedCategory.imageUrl)
                        .into(binding.categoryIcon)
                    category = selectedCategory
                })
            sheet.show(supportFragmentManager, "CategoryPicker")
        }
        type = "EXPENSE"
    }

    private fun setupForIncome() {
        binding.textViewCategoryLabel.text = "Category"
        binding.textViewCategoryValue.text = "Others"
        Glide.with(this)
            .load(R.drawable.ic_categories)
            .into(binding.categoryIcon)
        binding.paymentModeLabel.text = "Payment Mode"
        binding.textViewPaymentModeValue.text = "Cash"
        binding.layoutCategory.setOnClickListener {
            val sheet = CategoryPickerBottomSheet(
                CategoryStorage.loadCategories(this@AddTransactionActivity)
                    .filter { it.categoryType == "INCOME" },
                onCategorySelected = { selectedCategory ->
                    binding.textViewCategoryValue.text = selectedCategory.name
                    Glide.with(this)
                        .load(selectedCategory.imageUrl)
                        .into(binding.categoryIcon)
                    category = selectedCategory
                })
            sheet.show(supportFragmentManager, "CategoryPicker")
        }
        type = "INCOME"
    }

    private fun setupForTransfer() {
        binding.textViewCategoryLabel.text = "From"
        binding.textViewCategoryValue.text = "Cash"
        Glide.with(this)
            .load(R.drawable.money)
            .into(binding.categoryIcon)
        binding.paymentModeLabel.text = "To"
        binding.textViewPaymentModeValue.text = "Cash"
        binding.layoutCategory.setOnClickListener {
            // Open a bottom sheet to select the 'From' account, e.g., PaymentModePickerBottomSheet
            val sheet = PaymentModePickerBottomSheet(
                onPaymentModeSelected = { selectedPaymentMode ->
                    binding.textViewCategoryValue.text = selectedPaymentMode.modeName
                    fromPaymentMode = selectedPaymentMode
                }
            )
            sheet.show(supportFragmentManager, "PaymentModePicker")
        }
//
        type = "TRANSFER"
    }

    fun saveTransaction() {
        binding.fabSaveTransaction.setOnClickListener {
            if (binding.editTextAmount.text.isNullOrEmpty()) {
                Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (category == null && type != "TRANSFER") {
                Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (type == "TRANSFER" && fromPaymentMode == null) {
                Toast.makeText(this, "Please select a payment mode", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (toPaymentMode == null) {
                Toast.makeText(this, "Please select a payment mode", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
//            Log.d(TAG, "saveTransaction type: ${this.type}")
//            Log.d(TAG, "saveTransaction amount: ${binding.editTextAmount.text.toString()}")
//            Log.d(TAG, "saveTransaction description: ${binding.editTextNote.text.toString()}")
//            Log.d(TAG, "saveTransaction time: ${calendar.time}")
//            Log.d(TAG, "saveTransaction categoryId: ${category?.id}")
//            Log.d(TAG, "saveTransaction fromPaymentModeId: ${fromPaymentMode?.id}")
//            Log.d(TAG, "saveTransaction toPaymentModeId: ${toPaymentMode?.id}")
            val transaction = Transaction(
                type = this.type.toString(),
                amount = binding.editTextAmount.text.toString().toBigDecimal(),
                description = binding.editTextNote.text.toString(),
                time = selectedDate,
                categoryId = category?.id,
                fromPaymentModeId = fromPaymentMode?.id,
                toPaymentModeId = toPaymentMode?.id,
            )

            val transactionList =
                TransactionStorage.loadTransactions(this@AddTransactionActivity).toMutableList()
            transactionList.add(transaction)
            TransactionStorage.clearTransactions(this@AddTransactionActivity) // This would delete all previous transactions
            TransactionStorage.saveTransactions(this@AddTransactionActivity, transactionList)

            finish()
        }
    }
}
