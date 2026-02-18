package com.fincalc.app.feature.calculator

import com.fincalc.app.domain.model.CalculatorType

object CalculatorInfoProvider {

    fun info(type: CalculatorType): String = when (type) {
        CalculatorType.COMPOUND_INTEREST -> "Shows how principal grows with periodic compounding over time.\nUseful for long-term investment projection.\nHigher frequency increases maturity."
        CalculatorType.SIP -> "Calculates future value of fixed monthly investments.\nUses monthly compounding assumptions.\nIdeal for disciplined investing plans."
        CalculatorType.STEP_UP_SIP -> "Increases SIP amount every year by step-up %.\nCompares compounding impact of increasing contributions.\nUseful for income growth aligned investing."
        CalculatorType.SIP_LUMPSUM -> "Combines one-time amount and monthly SIP.\nProjects joint future value at expected return.\nShows contribution and gain split."
        CalculatorType.STEP_UP_SIP_LUMPSUM -> "Combines lumpsum + growing SIP contribution.\nUseful for aggressive long-term goals.\nHighlights total invested vs returns."
        CalculatorType.LUMPSUM -> "Projects one-time investment growth using annual compounding.\nHelps compare tenure/return scenarios.\nSimple maturity and gain split."
        CalculatorType.EMI -> "Computes monthly EMI for a loan.\nProvides amortization schedule month-by-month.\nShows total payment and total interest."
        CalculatorType.LOAN_COMPARISON -> "Compares two loan structures side-by-side.\nEvaluates EMI and total payment impact.\nHighlights better deal quickly."
        CalculatorType.SAVINGS_GOAL -> "Reverse calculator to reach target amount.\nReturns required monthly SIP or lumpsum today.\nUseful for goal-based planning."
        CalculatorType.TIP_SPLIT -> "Calculates tip, total bill, and per-person split.\nSupports quick tip presets and slider.\nGreat for dining bill sharing."
        CalculatorType.TAX_ESTIMATOR -> "Estimates tax in old vs new regimes.\nAccounts for deductions in old regime only.\nRecommends lower tax option."
        CalculatorType.RETIREMENT_PLANNER -> "Estimates required retirement corpus.\nIncludes inflation and pre/post retirement returns.\nCalculates monthly SIP needed for the gap."
        CalculatorType.FIRE_CALCULATOR -> "Computes FIRE number from expenses and SWR.\nProjects gap versus current trajectory.\nShows monthly savings needed to achieve FIRE."
        CalculatorType.INFLATION_ADJUSTER -> "Shows future purchasing power under inflation.\nComputes required future amount for same buying power.\nUseful for realistic target setting."
        CalculatorType.FD -> "Calculates FD maturity with compounding frequency.\nShows total interest earned over tenure.\nSuitable for fixed return projections."
        CalculatorType.PPF -> "Projects year-wise PPF balance growth.\nUses annual deposit + annual interest credit.\nShows maturity and yearly breakdown."
        CalculatorType.CAGR -> "Computes annualized return between initial and final values.\nStandard metric for multi-year growth comparison.\nAlso shows absolute return %."
    }
}
