package com.example.cashflowpro.ui.auth


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.cashflowpro.MainActivity
import com.example.cashflowpro.databinding.ActivityLoginBinding
import com.example.cashflowpro.util.Resource
import com.example.cashflowpro.util.SessionManager


const val TAG = "LoginActivity"
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this.applicationContext)

        // If a token already exists, go to MainActivity
        val token = sessionManager.fetchAuthToken()

        if (token != null) {
            Log.d(TAG, "onCreate: $token")
            if (sessionManager.isTokenExpired()) {
                Toast.makeText(this, "Your session has expired. Please log in again.", Toast.LENGTH_LONG).show()
                sessionManager.clearAuthToken()
            } else {
                navigateToMain()
            }
        }

        setupObservers()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.login(email, password)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun setupObservers() {
        viewModel.loginResult.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnLogin.isEnabled = false
                }

                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                    Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()

                    // Save token and navigate
                    resource.data?.let {
//                        Log.d("LoginActivity", "Auth Token: ${it.accessToken}")
                        sessionManager.saveAuthToken(it.accessToken)
                        navigateToMain()
                    }

                }

                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                    Toast.makeText(this, resource.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Prevents user from going back to Login screen
    }
}