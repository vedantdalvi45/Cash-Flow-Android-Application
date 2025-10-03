package com.example.cashflowpro.ui.fragments.navfragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.cashflowpro.R
import com.example.cashflowpro.databinding.FragmentMoreBinding
import com.example.cashflowpro.ui.more.CategoriesActivity


class MoreFragment : Fragment() {

    private lateinit var binding: FragmentMoreBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.categoryLayout.setOnClickListener {
            val intent = Intent(requireContext(), CategoriesActivity::class.java)
            startActivity(intent)
        }
    }
}