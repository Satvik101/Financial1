package com.fincalc.app.feature.calculator

import com.fincalc.app.R
import com.fincalc.app.domain.model.CalculatorType

object CalculatorDestinationMapper {
    fun destinationId(type: CalculatorType): Int = when (type) {
        CalculatorType.COMPOUND_INTEREST -> R.id.compoundInterestFragment
        CalculatorType.SIP -> R.id.sipFragment
        CalculatorType.STEP_UP_SIP -> R.id.stepUpSipFragment
        CalculatorType.SIP_LUMPSUM -> R.id.sipLumpsumFragment
        CalculatorType.STEP_UP_SIP_LUMPSUM -> R.id.stepUpSipLumpsumFragment
        CalculatorType.LUMPSUM -> R.id.lumpsumFragment
        CalculatorType.EMI -> R.id.emiFragment
        CalculatorType.LOAN_COMPARISON -> R.id.loanComparisonFragment
        CalculatorType.SAVINGS_GOAL -> R.id.savingsGoalFragment
        CalculatorType.TIP_SPLIT -> R.id.tipSplitFragment
        CalculatorType.TAX_ESTIMATOR -> R.id.taxEstimatorFragment
        CalculatorType.RETIREMENT_PLANNER -> R.id.retirementFragment
        CalculatorType.FIRE_CALCULATOR -> R.id.fireFragment
        CalculatorType.INFLATION_ADJUSTER -> R.id.inflationFragment
        CalculatorType.FD -> R.id.fdFragment
        CalculatorType.PPF -> R.id.ppfFragment
        CalculatorType.CAGR -> R.id.cagrFragment
    }
}
