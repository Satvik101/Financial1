package com.fincalc.app.feature.goals

import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.fincalc.app.FinCalcApplication
import com.fincalc.app.R
import com.fincalc.app.core.base.BaseFragment
import com.fincalc.app.core.base.SimpleViewModelFactory
import com.fincalc.app.databinding.FragmentGoalsBinding
import com.fincalc.app.feature.goals.adapter.GoalAdapter
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class GoalsFragment : BaseFragment(R.layout.fragment_goals) {

    private var _binding: FragmentGoalsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GoalsViewModel by viewModels {
        val app = requireActivity().application as FinCalcApplication
        SimpleViewModelFactory { GoalsViewModel(app.appModule.goalRepository) }
    }

    private lateinit var adapter: GoalAdapter

    override fun setupUi(view: View) {
        _binding = FragmentGoalsBinding.bind(view)
        adapter = GoalAdapter { goal ->
            showAddProgressDialog(goal.id, goal.currentSavedAmount) { addAmount ->
                viewModel.addProgress(goal, addAmount)
            }
        }
        binding.rvGoals.layoutManager = LinearLayoutManager(requireContext())
        binding.rvGoals.adapter = adapter

        binding.btnAddGoal.setOnClickListener { showAddGoalDialog() }
    }

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.goals.collect {
                adapter.submitList(it)
                binding.tvEmptyGoals.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    private fun showAddGoalDialog() {
        val goalName = TextInputEditText(requireContext()).apply { hint = "Name" }
        val target = TextInputEditText(requireContext()).apply { hint = "Target Amount" }
        val deadline = TextInputEditText(requireContext()).apply { hint = "Deadline (epoch millis)" }
        val saved = TextInputEditText(requireContext()).apply { hint = "Current Saved" }

        val container = android.widget.LinearLayout(requireContext()).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(40, 20, 40, 20)
            addView(goalName)
            addView(target)
            addView(deadline)
            addView(saved)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Add Goal")
            .setView(container)
            .setPositiveButton("Save") { _, _ ->
                val name = goalName.text?.toString().orEmpty().ifBlank { "Goal" }
                val targetAmount = target.text?.toString()?.toDoubleOrNull() ?: 0.0
                val deadlineMillis = deadline.text?.toString()?.toLongOrNull() ?: System.currentTimeMillis()
                val currentSaved = saved.text?.toString()?.toDoubleOrNull() ?: 0.0
                if (targetAmount > 0) {
                    viewModel.addGoal(name, targetAmount, deadlineMillis, currentSaved)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showAddProgressDialog(goalId: Long, current: Double, onAdd: (Double) -> Unit) {
        val input = TextInputEditText(requireContext()).apply { hint = "Add amount" }
        AlertDialog.Builder(requireContext())
            .setTitle("Update Progress")
            .setView(input)
            .setPositiveButton("Update") { _, _ ->
                val add = input.text?.toString()?.toDoubleOrNull() ?: 0.0
                if (add > 0) onAdd(add)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
