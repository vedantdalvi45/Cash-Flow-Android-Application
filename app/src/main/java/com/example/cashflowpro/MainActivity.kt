package com.example.cashflowpro

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.activity.OnBackPressedCallback
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.cashflowpro.databinding.ActivityMainBinding
import com.example.cashflowpro.ui.fragments.navfragments.AccountsFragment
import com.example.cashflowpro.ui.fragments.navfragments.AnalysisFragment
import com.example.cashflowpro.ui.fragments.navfragments.HomeFragment
import com.example.cashflowpro.ui.fragments.navfragments.MoreFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
                if (currentFragment !is HomeFragment) {
                    loadFragment(HomeFragment())
                    binding.bottomNavigation.selectedItemId = R.id.navigation_home
                } else {
                    finish()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        loadFragment(HomeFragment())

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.navigation_home -> HomeFragment()
                R.id.navigation_accounts -> AccountsFragment()
                R.id.navigation_analysis -> AnalysisFragment()
                R.id.navigation_more -> MoreFragment()
                else -> false
            }
            if (fragment is Fragment) {
                loadFragment(fragment)
            }
            fragment is Fragment
        }

        binding.addTransactionButton.setOnClickListener {
            startActivity(Intent(this, AddTransactionActivity::class.java))
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container,fragment)
            .commit()
    }

}
