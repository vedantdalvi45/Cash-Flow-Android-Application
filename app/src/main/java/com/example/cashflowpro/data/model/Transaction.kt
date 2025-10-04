package com.example.cashflowpro.data.model

import java.math.BigDecimal
import java.time.Instant
import java.util.Date

data class Transaction(
    val id: Long? = null,
    val type: String,
    val amount: BigDecimal,
    val description: String?,
    var time: Date,

    // IDs of related entities
    val categoryId: Long?,
    val fromPaymentModeId: Long? = null,
    val toPaymentModeId: Long?,
    val tagIds: Set<Long>? = null
)