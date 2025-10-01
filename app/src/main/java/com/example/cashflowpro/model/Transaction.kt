package com.example.cashflowpro.model

import java.util.Date

data class Transaction(
    val id: Long,
    val amount: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val date: Date = Date(timestamp),
    val category: Category,
    val paymentMethod: PaymentMode,
    val balance: Double,
    val transactionType: String,
    val tags: List<String>,
    val attachment : String
)