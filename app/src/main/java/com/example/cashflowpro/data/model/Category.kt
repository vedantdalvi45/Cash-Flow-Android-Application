package com.example.cashflowpro.data.model

import com.google.gson.annotations.SerializedName

data class Category(
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val categoryType: String
)
