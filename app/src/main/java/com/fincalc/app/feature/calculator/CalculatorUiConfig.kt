package com.fincalc.app.feature.calculator

import android.view.View
import com.fincalc.app.domain.model.CalculatorType

data class CalculatorUiConfig(
    val hints: List<String>,
    val showFrequency: Boolean = false,
    val showPeriodToggle: Boolean = false,
    val showTipControls: Boolean = false,
    val showAgeGroup: Boolean = false,
    val showTable: Boolean = false
)

object CalculatorUiConfigFactory {
    fun forType(type: CalculatorType): CalculatorUiConfig = when (type) {
        CalculatorType.COMPOUND_INTEREST -> CalculatorUiConfig(
            hints = listOf("Principal (₹)", "Rate (% p.a.)", "Time"),
            showFrequency = true,
            showPeriodToggle = true
        )
        CalculatorType.SIP -> CalculatorUiConfig(
            hints = listOf("Monthly SIP (₹)", "Return (% p.a.)", "Time"),
            showPeriodToggle = true
        )
        CalculatorType.STEP_UP_SIP -> CalculatorUiConfig(
            hints = listOf("Start SIP (₹)", "Step-Up (% p.a.)", "Expected Return (% p.a.)", "Years")
        )
        CalculatorType.SIP_LUMPSUM -> CalculatorUiConfig(
            hints = listOf("Lumpsum (₹)", "Monthly SIP (₹)", "Return (% p.a.)", "Time"),
            showPeriodToggle = true
        )
        CalculatorType.STEP_UP_SIP_LUMPSUM -> CalculatorUiConfig(
            hints = listOf("Lumpsum (₹)", "Start SIP (₹)", "Step-Up (% p.a.)", "Return (% p.a.)", "Years")
        )
        CalculatorType.LUMPSUM -> CalculatorUiConfig(
            hints = listOf("Investment (₹)", "Return (% p.a.)", "Time"),
            showPeriodToggle = true
        )
        CalculatorType.EMI -> CalculatorUiConfig(
            hints = listOf("Loan Amount (₹)", "Rate (% p.a.)", "Tenure"),
            showPeriodToggle = true,
            showTable = true
        )
        CalculatorType.LOAN_COMPARISON -> CalculatorUiConfig(
            hints = listOf("Loan A Amount", "Loan A Rate", "Loan A Tenure", "Loan B Amount", "Loan B Rate", "Loan B Tenure"),
            showPeriodToggle = true
        )
        CalculatorType.SAVINGS_GOAL -> CalculatorUiConfig(
            hints = listOf("Target Amount (₹)", "Return (% p.a.)", "Time"),
            showPeriodToggle = true
        )
        CalculatorType.TIP_SPLIT -> CalculatorUiConfig(
            hints = listOf("Bill Amount (₹)"),
            showTipControls = true
        )
        CalculatorType.TAX_ESTIMATOR -> CalculatorUiConfig(
            hints = listOf("Annual Income (₹)", "80C Deduction (₹)", "HRA Exemption (₹)", "Other Deductions (₹)"),
            showAgeGroup = true,
            showTable = true
        )
        CalculatorType.RETIREMENT_PLANNER -> CalculatorUiConfig(
            hints = listOf("Current Age", "Retirement Age", "Life Expectancy", "Current Monthly Expenses", "Inflation %", "Current Savings", "Pre-Ret Return %", "Post-Ret Return %")
        )
        CalculatorType.FIRE_CALCULATOR -> CalculatorUiConfig(
            hints = listOf("Current Annual Expenses", "Inflation %", "Years to FIRE", "SWR %", "Current Investments", "Expected Return %")
        )
        CalculatorType.INFLATION_ADJUSTER -> CalculatorUiConfig(
            hints = listOf("Current Amount", "Inflation %", "Years")
        )
        CalculatorType.FD -> CalculatorUiConfig(
            hints = listOf("Deposit Amount", "Rate %", "Tenure"),
            showFrequency = true,
            showPeriodToggle = true
        )
        CalculatorType.PPF -> CalculatorUiConfig(
            hints = listOf("Yearly Deposit", "Rate %", "Years"),
            showTable = true
        )
        CalculatorType.CAGR -> CalculatorUiConfig(
            hints = listOf("Initial Value", "Final Value", "Years")
        )
    }

    fun fieldVisibility(hintsCount: Int, fieldIndex: Int): Int {
        return if (fieldIndex < hintsCount) View.VISIBLE else View.GONE
    }
}
