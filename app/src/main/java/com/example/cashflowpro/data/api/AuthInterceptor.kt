package com.example.cashflowpro.data.api

import android.content.Context
import android.content.Intent
import com.example.cashflowpro.ui.auth.LoginActivity
import com.example.cashflowpro.util.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val sessionManager = SessionManager(context)
        val token = sessionManager.fetchAuthToken()

        val requestBuilder = chain.request().newBuilder()

        // If token exists, add it to the request header
        token?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        val request = requestBuilder.build()
        val response = chain.proceed(request)

        // If the server returns a 401 Unauthorized error
        if (response.code == 401) {
            // Clear the session and redirect to the login screen
            sessionManager.clearAuthToken()
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }

        return response
    }
}