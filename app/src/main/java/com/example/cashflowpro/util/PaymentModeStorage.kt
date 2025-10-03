package com.example.cashflowpro.util

import android.content.Context
import com.example.cashflowpro.data.model.PaymentMode
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object PaymentModeStorage {

    private const val PREF_NAME = "payment_mode_prefs"
    private const val KEY_PAYMENT_MODES = "payment_modes"

    private val gson = Gson()

    fun savePaymentModes(context: Context, paymentModes: List<PaymentMode>) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_PAYMENT_MODES, gson.toJson(paymentModes)).apply()
    }

    fun loadPaymentModes(context: Context): List<PaymentMode> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_PAYMENT_MODES, null)
        val type = object : TypeToken<List<PaymentMode>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun areListsEqual(list1: List<PaymentMode>, list2: List<PaymentMode>): Boolean =
        list1.size == list2.size && list1.sortedBy { it.id } == list2.sortedBy { it.id }
}