package com.fincalc.app.feature.history.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fincalc.app.data.local.db.entity.HistoryEntity
import com.fincalc.app.databinding.ItemHistoryBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter(
    private val onClick: (HistoryEntity) -> Unit,
    private val onLongClick: (HistoryEntity) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.Holder>() {

    private val items = mutableListOf<HistoryEntity>()

    fun submitList(list: List<HistoryEntity>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    fun getItemAt(position: Int): HistoryEntity = items[position]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class Holder(private val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HistoryEntity) {
            binding.tvType.text = item.calculatorType
            binding.tvResult.text = item.resultValueLabel
            binding.tvDate.text = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US).format(Date(item.createdAt))
            binding.root.setOnClickListener { onClick(item) }
            binding.root.setOnLongClickListener {
                onLongClick(item)
                true
            }
        }
    }
}
