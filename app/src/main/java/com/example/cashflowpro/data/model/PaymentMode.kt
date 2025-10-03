package com.example.cashflowpro.data.model

import com.google.gson.annotations.SerializedName

data class PaymentMode(
    val id: Long,
    val modeName: String,
    val paymentType: String,
    val balance: Double
)
