package com.example.cashflowpro.ui.more

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cashflowpro.data.api.RetrofitClient
import com.example.cashflowpro.data.model.Category
import com.example.cashflowpro.util.Resource
import com.example.cashflowpro.util.SessionManager
import kotlinx.coroutines.launch
import java.io.IOException


class CategoryViewModel(application: Application) : AndroidViewModel(application) {

    private val _categories = MutableLiveData<Resource<List<Category>>>()

    val categories: LiveData<Resource<List<Category>>> = _categories

    private val sessionManager = SessionManager(application)

    fun fetchCategories() {
        viewModelScope.launch {
            _categories.postValue(Resource.Loading())

            try {
                val jwtToken = sessionManager.fetchAuthToken()
                if (jwtToken == null) {
                    _categories.postValue(Resource.Error("User not logged in."))
                    return@launch
                }
                val response = RetrofitClient.instance.getAllCategories("Bearer $jwtToken")

                if (response.isSuccessful) {
                    response.body()?.let { categoryList ->
                        _categories.postValue(Resource.Success(categoryList))
                    } ?: run {
                        _categories.postValue(Resource.Error("Empty response body."))
                    }
                } else {
                    _categories.postValue(Resource.Error("Error ${response.code()}: ${response.message()}"))
                }
            } catch (e: IOException) {
                _categories.postValue(Resource.Error("Network error. Please check your connection."))
            } catch (e: Exception) {
                _categories.postValue(Resource.Error(e.message ?: "An unexpected error occurred."))
            }
        }
    }
}