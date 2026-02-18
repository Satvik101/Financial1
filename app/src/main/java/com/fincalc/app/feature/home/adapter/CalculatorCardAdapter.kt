package com.fincalc.app.feature.home.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fincalc.app.databinding.ItemCalculatorCardBinding

class CalculatorCardAdapter(
    private val onClick: (CalculatorCardItem) -> Unit
) : RecyclerView.Adapter<CalculatorCardAdapter.CardViewHolder>() {

    private val items = mutableListOf<CalculatorCardItem>()

    fun submitList(newItems: List<CalculatorCardItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val binding = ItemCalculatorCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class CardViewHolder(
        private val binding: ItemCalculatorCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CalculatorCardItem) {
            binding.tvName.text = item.name
            binding.tvDescription.text = item.description
            try {
                val color = Color.parseColor(item.accentColorHex)
                binding.accentStrip.setBackgroundColor(color)
                binding.root.strokeColor = color
            } catch (_: Exception) {
                // use default color
            }
            binding.root.setOnClickListener { onClick(item) }
        }
    }
}
