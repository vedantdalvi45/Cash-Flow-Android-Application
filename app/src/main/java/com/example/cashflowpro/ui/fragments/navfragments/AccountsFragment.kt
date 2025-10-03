package com.example.cashflowpro.ui.fragments.navfragments

import android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cashflowpro.adapter.PaymentModeAdapter
import com.example.cashflowpro.data.model.PaymentMode
import com.example.cashflowpro.databinding.FragmentAccountsBinding
import com.example.cashflowpro.ui.accounts.PaymentModeViewModel
import com.example.cashflowpro.util.PaymentModeStorage
import com.example.cashflowpro.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.Executor

const val TAG = "AccountsFragment"

@AndroidEntryPoint
class AccountsFragment : Fragment() {

    private var _binding: FragmentAccountsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PaymentModeViewModel by viewModels()

    private lateinit var paymentModeAdapterForBank: PaymentModeAdapter
    private lateinit var paymentModeAdapterForCash: PaymentModeAdapter

    private val paymentModeListGlobal = mutableListOf<PaymentMode>()

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViewForBank()
        setupRecyclerViewForCash()
        observePaymentModes()

        val savedPaymentModes = PaymentModeStorage.loadPaymentModes(requireContext())
        if (savedPaymentModes.isNotEmpty()) {
            paymentModeListGlobal.clear()
            paymentModeListGlobal.addAll(savedPaymentModes)
            paymentModeAdapterForBank.submitList(paymentModeListGlobal.filter { it.paymentType == "BANK" }
                .map { it.copy(balance = -1.0) })
            paymentModeAdapterForCash.submitList(paymentModeListGlobal.filter { it.paymentType == "CASH" }
                .map { it.copy(balance = -1.0) })
            // Update counts when loading from storage
            binding.accountsCountB.text =
                paymentModeListGlobal.count { it.paymentType == "BANK" }.toString()
            binding.accountsCountC.text =
                paymentModeListGlobal.count { it.paymentType == "CASH" }.toString()


            binding.accountsRecyclerViewForBank.visibility = View.VISIBLE
            binding.accountsRecyclerViewForCash.visibility = View.VISIBLE
        }

        viewModel.fetchPaymentModes()

        setupBiometrics()

        binding.balanceToggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                authenticate()
            } else {
                updateBalanceVisibility(isVisible = false)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerViewForBank() {
        paymentModeAdapterForBank = PaymentModeAdapter() // Initialize the adapter
        binding.accountsRecyclerViewForBank.apply {
            adapter = paymentModeAdapterForBank
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupRecyclerViewForCash() {
        paymentModeAdapterForCash = PaymentModeAdapter() // Initialize the adapter
        binding.accountsRecyclerViewForCash.apply {
            adapter = paymentModeAdapterForCash
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observePaymentModes() {
        viewModel.paymentModes.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    Log.d(TAG, "observePaymentModes: Loading...")
                    // 4. Handle loading UI state
                }

                is Resource.Success -> {
                    if (!PaymentModeStorage.areListsEqual(resource.data!!, paymentModeListGlobal)) {
                        Log.d(
                            TAG,
                            "observePaymentModes: New data detected. Updating UI and storage."
                        )
                        paymentModeListGlobal.clear()
                        paymentModeListGlobal.addAll(resource.data)
                        PaymentModeStorage.savePaymentModes(requireContext(), resource.data)

                        paymentModeAdapterForBank.submitList(paymentModeListGlobal.filter { it.paymentType == "BANK" }
                            .map { it.copy(balance = -1.0) })
                        paymentModeAdapterForCash.submitList(paymentModeListGlobal.filter { it.paymentType == "CASH" }
                            .map { it.copy(balance = -1.0) })

                    } else
                        Log.d(
                            TAG, "observePaymentModes: No changes detected. Skipping update. " +
                                "${paymentModeListGlobal.filter { it.paymentType == "BANK" }}"
                        )

                    binding.accountsCountB.text =
                        paymentModeListGlobal.filter { it.paymentType == "BANK" }.size.toString()
                    binding.accountsCountC.text =
                        paymentModeListGlobal.filter { it.paymentType == "CASH" }.size.toString()


                }

                is Resource.Error -> {
                    Log.d(TAG, "observePaymentModes: Error - ${resource.message}")
                    // Optionally show an error message TextView
                }
            }
        }
    }

    private fun setupBiometrics() {
        executor = ContextCompat.getMainExecutor(requireContext())

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                handleAuthenticationResult(success = true)
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                Toast.makeText(context, "Authentication error: $errString", Toast.LENGTH_SHORT)
                    .show()
                handleAuthenticationResult(success = false)
            }
        }

        biometricPrompt = BiometricPrompt(this, executor, callback)

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Authentication Required")
            .setSubtitle("Confirm your identity to view the balance")
            .setAllowedAuthenticators(BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build()
    }

    private fun authenticate() {
        val biometricManager = BiometricManager.from(requireContext())
        if (biometricManager.canAuthenticate(BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_SUCCESS) {
            biometricPrompt.authenticate(promptInfo)
        } else {
            Toast.makeText(context, "No screen lock or fingerprint is set up.", Toast.LENGTH_SHORT)
                .show()
            handleAuthenticationResult(success = false)
        }
    }

    private fun handleAuthenticationResult(success: Boolean) {
        if (success && binding.balanceToggle.isChecked) {
            updateBalanceVisibility(isVisible = true)
        } else {
            binding.balanceToggle.isChecked = false
            updateBalanceVisibility(isVisible = false)
        }
    }

    private fun updateBalanceVisibility(isVisible: Boolean) {
        if (isVisible) {
            binding.balanceTextView.text = "₹${paymentModeListGlobal.sumOf { it.balance }}"
            paymentModeAdapterForBank.submitList(paymentModeListGlobal.filter { it.paymentType == "BANK" }
                .map { it.copy() })
            paymentModeAdapterForCash.submitList(paymentModeListGlobal.filter { it.paymentType == "CASH" }
                .map { it.copy() })
        } else {
            binding.balanceTextView.text = "*****"
            paymentModeAdapterForBank.submitList(paymentModeListGlobal.filter { it.paymentType == "BANK" }
                .map { it.copy(balance = -1.0) })
            paymentModeAdapterForCash.submitList(paymentModeListGlobal.filter { it.paymentType == "CASH" }
                .map { it.copy(balance = -1.0) })
        }
    }
}