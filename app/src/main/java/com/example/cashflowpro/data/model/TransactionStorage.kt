package com.example.cashflowpro.util

import android.content.Context
import com.example.cashflowpro.data.model.Transaction
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.sql.Time
import java.util.Date

object TransactionStorage {

    private const val PREF_NAME = "transaction_prefs"
    private const val KEY_TRANSACTIONS = "transactions"

    private val gson: Gson =
        GsonBuilder()
            .registerTypeAdapter(Date::class.java, DateTypeAdapter())
            .registerTypeAdapter(Time::class.java, TimeTypeAdapter())
            .create()

    fun saveTransactions(context: Context, transactions: List<Transaction>) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = gson.toJson(transactions)
        prefs.edit().putString(KEY_TRANSACTIONS, json).apply()
    }

    fun loadTransactions(context: Context): List<Transaction> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_TRANSACTIONS, null)
        return if (json != null) {
            val type = object : TypeToken<List<Transaction>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    fun clearTransactions(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_TRANSACTIONS).apply()
    }

    fun areListsEqual(list1: List<Transaction>, list2: List<Transaction>): Boolean {
        return list1.sortedBy { it.id } == list2.sortedBy { it.id }
    }
}

private class DateTypeAdapter : JsonSerializer<Date>, JsonDeserializer<Date> {
    override fun serialize(src: Date?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src?.time)
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Date {
        return Date(json?.asLong ?: 0)
    }
}

private class TimeTypeAdapter : JsonSerializer<Time>, JsonDeserializer<Time> {
    override fun serialize(src: Time?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src?.time)
    }
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Time {
        return Time(json?.asLong ?: 0)
    }
}
