package com.fincalc.app.feature.calculator.whatif

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.fincalc.app.R
import com.fincalc.app.core.base.BaseFragment
import com.fincalc.app.databinding.FragmentWhatIfBinding
import com.fincalc.app.domain.model.CalculatorType
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.launch

class WhatIfContainerFragment : BaseFragment(R.layout.fragment_what_if) {

    private var _binding: FragmentWhatIfBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WhatIfViewModel by viewModels()

    override fun setupUi(view: View) {
        _binding = FragmentWhatIfBinding.bind(view)

        binding.btnCompare.setOnClickListener {
            val type = CalculatorType.valueOf(arguments?.getString("calculatorType") ?: CalculatorType.SIP.name)
            val a1 = binding.etA1.text?.toString()?.toDoubleOrNull() ?: 0.0
            val b1 = binding.etB1.text?.toString()?.toDoubleOrNull() ?: 0.0
            val c1 = binding.etC1.text?.toString()?.toDoubleOrNull() ?: 0.0
            val a2 = binding.etA2.text?.toString()?.toDoubleOrNull() ?: 0.0
            val b2 = binding.etB2.text?.toString()?.toDoubleOrNull() ?: 0.0
            val c2 = binding.etC2.text?.toString()?.toDoubleOrNull() ?: 0.0
            viewModel.compare(type, a1, b1, c1, a2, b2, c2)
        }
    }

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.comparison.collect { binding.tvComparison.text = it }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.planValues.collect { (a, b) ->
                if (a <= 0.0 && b <= 0.0) return@collect
                val setA = LineDataSet(listOf(Entry(1f, a.toFloat()), Entry(2f, a.toFloat())), "Plan A")
                val setB = LineDataSet(listOf(Entry(1f, b.toFloat()), Entry(2f, b.toFloat())), "Plan B")
                setA.color = android.graphics.Color.parseColor("#1A73E8")
                setB.color = android.graphics.Color.parseColor("#34A853")
                binding.lineChartCompare.data = LineData(setA, setB)
                binding.lineChartCompare.description.isEnabled = false
                binding.lineChartCompare.invalidate()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
