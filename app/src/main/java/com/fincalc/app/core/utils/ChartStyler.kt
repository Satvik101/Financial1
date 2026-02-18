package com.fincalc.app.core.utils

import android.content.Context
import androidx.core.content.ContextCompat
import com.fincalc.app.R
import com.github.mikephil.charting.charts.Chart

object ChartStyler {

    fun style(chart: Chart<*>, context: Context) {
        chart.description.isEnabled = false
        chart.legend.isEnabled = true
        chart.setNoDataText("No data")
        chart.setNoDataTextColor(ContextCompat.getColor(context, R.color.white))
        chart.animateY(700)
    }
}
