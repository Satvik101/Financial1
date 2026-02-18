package com.fincalc.app.feature.history

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fincalc.app.FinCalcApplication
import com.fincalc.app.R
import com.fincalc.app.core.base.BaseFragment
import com.fincalc.app.core.base.SimpleViewModelFactory
import com.fincalc.app.databinding.FragmentHistoryBinding
import com.fincalc.app.domain.model.CalculatorType
import com.fincalc.app.feature.calculator.CalculatorDestinationMapper
import com.fincalc.app.feature.history.adapter.HistoryAdapter
import kotlinx.coroutines.launch

class HistoryFragment : BaseFragment(R.layout.fragment_history) {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HistoryViewModel by viewModels {
        val app = requireActivity().application as FinCalcApplication
        SimpleViewModelFactory { HistoryViewModel(app.appModule.historyRepository) }
    }

    private lateinit var adapter: HistoryAdapter

    override fun setupUi(view: View) {
        _binding = FragmentHistoryBinding.bind(view)
        adapter = HistoryAdapter(
            onClick = { item ->
                val type = runCatching { CalculatorType.valueOf(item.calculatorType) }.getOrNull()
                if (type != null) {
                    val destination = CalculatorDestinationMapper.destinationId(type)
                    val bundle = Bundle().apply {
                        putString("prefillInputJson", item.inputJson)
                    }
                    findNavController().navigate(destination, bundle)
                }
            },
            onLongClick = { item ->
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, "${item.calculatorType}: ${item.resultValueLabel}")
                }
                startActivity(Intent.createChooser(shareIntent, "Share Calculation"))
            }
        )
        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = adapter

        binding.chipGroupHistory.setOnCheckedStateChangeListener { _, checkedIds ->
            val id = checkedIds.firstOrNull() ?: return@setOnCheckedStateChangeListener
            val filter = when (id) {
                R.id.chipSip -> "SIP"
                R.id.chipEmi -> "EMI"
                R.id.chipInterest -> "INTEREST"
                R.id.chipTax -> "TAX"
                R.id.chipOther -> "OTHER"
                else -> "ALL"
            }
            viewModel.setFilter(filter)
        }

        val swipe = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val item = adapter.getItemAt(viewHolder.adapterPosition)
                viewModel.delete(item.id)
            }
        })
        swipe.attachToRecyclerView(binding.rvHistory)
    }

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.history.collect {
                adapter.submitList(it)
                binding.tvEmpty.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
