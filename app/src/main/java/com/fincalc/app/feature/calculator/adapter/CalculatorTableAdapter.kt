package com.fincalc.app.feature.calculator.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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
        holder.bind(rows[position])
    }

    override fun getItemCount(): Int = rows.size

    inner class Holder(private val binding: ItemCalcTableRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cols: List<String>) {
            binding.tvCol1.text = cols.getOrElse(0) { "" }
            binding.tvCol2.text = cols.getOrElse(1) { "" }
            binding.tvCol3.text = cols.getOrElse(2) { "" }
            binding.tvCol4.text = cols.getOrElse(3) { "" }
            binding.tvCol5.text = cols.getOrElse(4) { "" }
        }
    }
}
