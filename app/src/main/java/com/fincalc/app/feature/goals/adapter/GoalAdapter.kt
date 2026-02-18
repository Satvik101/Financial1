package com.fincalc.app.feature.goals.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fincalc.app.data.local.db.entity.GoalEntity
import com.fincalc.app.databinding.ItemGoalBinding

class GoalAdapter(
    private val onClick: (GoalEntity) -> Unit
) : RecyclerView.Adapter<GoalAdapter.Holder>() {

    private val items = mutableListOf<GoalEntity>()

    fun submitList(list: List<GoalEntity>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemGoalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class Holder(private val binding: ItemGoalBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GoalEntity) {
            val progress = ((item.currentSavedAmount / item.targetAmount) * 100).coerceIn(0.0, 100.0)
            val remaining = (item.targetAmount - item.currentSavedAmount).coerceAtLeast(0.0)
            binding.tvGoalName.text = item.name
            binding.tvGoalProgress.text = "Saved ₹${"%.0f".format(item.currentSavedAmount)} / ₹${"%.0f".format(item.targetAmount)} (${progress.toInt()}%)"
            binding.tvGoalNeed.text = "₹${"%.0f".format(remaining)} remaining"
            binding.progressGoal.progress = progress.toInt()
            binding.root.setOnClickListener { onClick(item) }
        }
    }
}
