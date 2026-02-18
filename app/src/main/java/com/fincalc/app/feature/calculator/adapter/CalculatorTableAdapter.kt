package com.fincalc.app.feature.calculator.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.fincalc.app.R
import com.fincalc.app.databinding.ItemCalcTableRowBinding

class CalculatorTableAdapter : RecyclerView.Adapter<CalculatorTableAdapter.Holder>() {

    private val rows = mutableListOf<List<String>>()

    fun submitRows(newRows: List<List<String>>) {
        rows.clear()
        rows.addAll(newRows)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemCalcTableRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(rows[position], position)
    }

    override fun getItemCount(): Int = rows.size

    inner class Holder(private val binding: ItemCalcTableRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cols: List<String>, position: Int) {
            binding.tvCol1.text = cols.getOrElse(0) { "" }
            binding.tvCol2.text = cols.getOrElse(1) { "" }
            binding.tvCol3.text = cols.getOrElse(2) { "" }
            binding.tvCol4.text = cols.getOrElse(3) { "" }
            binding.tvCol5.text = cols.getOrElse(4) { "" }

            // Header row (first row) styling
            if (position == 0) {
                val headerBg = ContextCompat.getColor(binding.root.context, R.color.table_header_bg)
                binding.rowContainer.setBackgroundColor(headerBg)
                val headerText = ContextCompat.getColor(binding.root.context, R.color.text_primary)
                listOf(binding.tvCol1, binding.tvCol2, binding.tvCol3, binding.tvCol4, binding.tvCol5).forEach {
                    it.setTextColor(headerText)
                    it.setTypeface(it.typeface, android.graphics.Typeface.BOLD)
                    it.textSize = 11f
                }
            } else {
                // Alternating row colors
                val bgColor = if (position % 2 == 0) {
                    ContextCompat.getColor(binding.root.context, R.color.table_row_alt)
                } else {
                    ContextCompat.getColor(binding.root.context, R.color.surface)
                }
                binding.rowContainer.setBackgroundColor(bgColor)
            }
        }
    }
}
