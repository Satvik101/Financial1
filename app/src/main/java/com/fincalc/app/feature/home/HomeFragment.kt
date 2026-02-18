package com.fincalc.app.feature.home

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.fincalc.app.R
import com.fincalc.app.core.base.BaseFragment
import com.fincalc.app.databinding.FragmentHomeBinding
import com.fincalc.app.feature.calculator.CalculatorDestinationMapper
import com.fincalc.app.feature.home.adapter.CalculatorCardAdapter
import kotlinx.coroutines.launch

class HomeFragment : BaseFragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var adapter: CalculatorCardAdapter

    override fun setupUi(view: View) {
        _binding = FragmentHomeBinding.bind(view)
        adapter = CalculatorCardAdapter { item ->
            val destination = CalculatorDestinationMapper.destinationId(item.type)
            val bundle = Bundle().apply {
                putString("prefillInputJson", "")
            }
            findNavController().navigate(destination, bundle)
        }
        binding.rvCalculators.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvCalculators.adapter = adapter

        binding.etSearch.doAfterTextChanged { text ->
            viewModel.search(text?.toString().orEmpty())
        }
    }

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.cards.collect { adapter.submitList(it) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
