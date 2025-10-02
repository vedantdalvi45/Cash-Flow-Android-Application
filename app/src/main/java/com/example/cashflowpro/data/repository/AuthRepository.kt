package com.example.cashflowpro.data.repository

import com.example.cashflowpro.data.api.ApiService
import com.example.cashflowpro.data.model.auth.LoginRequest
import com.example.cashflowpro.data.model.auth.SignUpRequest

class AuthRepository(private val apiService: ApiService) {

    suspend fun login(loginRequest: LoginRequest) = apiService.loginUser(loginRequest)

    suspend fun signup(signUpRequest: SignUpRequest) = apiService.signupUser(signUpRequest)

}