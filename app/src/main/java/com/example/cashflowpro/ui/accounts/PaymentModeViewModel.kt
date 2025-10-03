package com.example.cashflowpro.ui.accounts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cashflowpro.data.api.RetrofitClient // Your singleton Retrofit instance
import com.example.cashflowpro.data.model.PaymentMode
import com.example.cashflowpro.util.Resource
import com.example.cashflowpro.util.SessionManager
import kotlinx.coroutines.launch
import java.io.IOException

class PaymentModeViewModel(application: Application) : AndroidViewModel(application) {

    private val _paymentModes = MutableLiveData<Resource<List<PaymentMode>>>()
    val paymentModes: LiveData<Resource<List<PaymentMode>>> = _paymentModes

    // Manually create the SessionManager instance
    private val sessionManager = SessionManager(application)

    // The fetch logic is now in a public function instead of init
    fun fetchPaymentModes() {
        viewModelScope.launch {
            _paymentModes.postValue(Resource.Loading())

            try {
                // Fetch the JWT token from the SessionManager
                val jwtToken = sessionManager.fetchAuthToken()
                if (jwtToken == null) {
                    _paymentModes.postValue(Resource.Error("User not logged in."))
                    return@launch // Stop if no token is found
                }

                // Call the API using the manually created Retrofit instance and pass the token
                val response = RetrofitClient.instance.getPaymentModes("Bearer $jwtToken")

                if (response.isSuccessful) {
                    response.body()?.let { paymentModesList ->
                        _paymentModes.postValue(Resource.Success(paymentModesList))
                    } ?: run {
                        _paymentModes.postValue(Resource.Error("Empty response body."))
                    }
                } else {
                    _paymentModes.postValue(Resource.Error("Error ${response.code()}: ${response.message()}"))
                }
            } catch (e: IOException) {
                // Handle specific network errors
                _paymentModes.postValue(Resource.Error("Network error. Please check your connection."))
            } catch (e: Exception) {
                // Handle all other unexpected errors
                _paymentModes.postValue(Resource.Error(e.message ?: "An unexpected error occurred."))
            }
        }
    }
}