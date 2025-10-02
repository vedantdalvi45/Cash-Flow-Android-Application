package com.example.cashflowpro.data.api

import com.example.cashflowpro.data.model.auth.GenericResponse
import com.example.cashflowpro.data.model.auth.LoginRequest
import com.example.cashflowpro.data.model.auth.LoginResponse
import com.example.cashflowpro.data.model.auth.SignUpRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("auth/login")
    suspend fun loginUser(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("auth/signup")
    suspend fun signupUser(@Body signUpRequest: SignUpRequest): Response<GenericResponse>

}