package com.example.cashflowpro.util

import android.content.Context
import com.example.cashflowpro.data.model.Category
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object CategoryStorage {

    private const val PREF_NAME = "category_prefs"
    private const val KEY_CATEGORIES = "categories"

    private val gson = Gson()

    fun saveCategories(context: Context, categories: List<Category>) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = gson.toJson(categories)
        prefs.edit().putString(KEY_CATEGORIES, json).apply()
    }

    fun loadCategories(context: Context): List<Category> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_CATEGORIES, null)
        return if (json != null) {
            val type = object : TypeToken<List<Category>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    fun clearCategories(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_CATEGORIES).apply()
    }

    fun areListsEqual(list1: List<Category>, list2: List<Category>): Boolean {
        return list1.sortedBy { it.id } == list2.sortedBy { it.id }
    }
}
