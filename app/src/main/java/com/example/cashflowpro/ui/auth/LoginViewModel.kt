package com.example.cashflowpro.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cashflowpro.data.api.RetrofitClient
import com.example.cashflowpro.data.model.auth.LoginRequest
import com.example.cashflowpro.data.model.auth.LoginResponse
import com.example.cashflowpro.data.repository.AuthRepository
import com.example.cashflowpro.util.Resource
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val repository = AuthRepository(RetrofitClient.instance)

    private val _loginResult = MutableLiveData<Resource<LoginResponse>>()
    val loginResult: LiveData<Resource<LoginResponse>> = _loginResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginResult.postValue(Resource.Loading())
            try {
                val response = repository.login(LoginRequest(email, password))
                if (response.isSuccessful && response.body() != null) {
                    _loginResult.postValue(Resource.Success(response.body()!!))
                } else {
                    _loginResult.postValue(Resource.Error("Login failed: ${response.message()}"))
                }
            } catch (e: Exception) {
                _loginResult.postValue(Resource.Error("An error occurred: ${e.message}"))
            }
        }
    }
}