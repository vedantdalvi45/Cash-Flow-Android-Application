package com.example.cashflowpro.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cashflowpro.data.api.RetrofitClient
import com.example.cashflowpro.data.model.auth.GenericResponse
import com.example.cashflowpro.data.model.auth.SignUpRequest
import com.example.cashflowpro.data.repository.AuthRepository
import com.example.cashflowpro.util.Resource
import kotlinx.coroutines.launch

class SignUpViewModel : ViewModel() {

    private val repository = AuthRepository(RetrofitClient.instance)

    private val _signUpResult = MutableLiveData<Resource<GenericResponse>>()
    val signUpResult: LiveData<Resource<GenericResponse>> = _signUpResult

    fun signup(firstName: String, lastName: String, email: String, password: String) {
        viewModelScope.launch {
            _signUpResult.postValue(Resource.Loading())
            try {
                val response = repository.signup(SignUpRequest(firstName, lastName, email, password))
                if (response.isSuccessful && response.body() != null) {
                    _signUpResult.postValue(Resource.Success(response.body()!!))
                } else {
                    // You can parse the error body here for a more specific message
                    _signUpResult.postValue(Resource.Error("Sign up failed: ${response.message()}"))
                }
            } catch (e: Exception) {
                _signUpResult.postValue(Resource.Error("An error occurred: ${e.message}"))
            }
        }
    }
}