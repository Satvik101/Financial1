package com.fincalc.app.domain.model

data class YearlyGrowth(
    val year: Int,
    val invested: Double,
    val value: Double
)

data class CompoundInterestResult(
    val principal: Double,
    val maturityAmount: Double,
    val totalInterest: Double,
    val yearlyGrowth: List<YearlyGrowth>
)

data class SipResult(
    val investedAmount: Double,
    val estimatedReturns: Double,
    val totalValue: Double,
    val yearlyGrowth: List<YearlyGrowth>
)

data class StepUpSipResult(
    val totalInvested: Double,
    val estimatedReturns: Double,
    val totalValue: Double,
    val finalMonthlySip: Double,
    val yearlySipAmounts: List<Double>,
    val yearlyGrowth: List<YearlyGrowth>,
    val yearlyGrowthWithoutStepUp: List<YearlyGrowth>
)

data class SipLumpsumResult(
    val lumpsumGrowth: Double,
    val sipValue: Double,
    val totalValue: Double,
    val totalInvested: Double,
    val totalReturns: Double,
    val yearlyGrowth: List<YearlyGrowth>
)

data class StepUpSipLumpsumResult(
    val lumpsumGrowth: Double,
    val stepUpSipValue: Double,
    val totalInvested: Double,
    val totalReturns: Double,
    val totalValue: Double,
    val yearlyGrowth: List<YearlyGrowth>
)

data class LumpsumResult(
    val invested: Double,
    val returns: Double,
    val maturityValue: Double,
    val yearlyGrowth: List<YearlyGrowth>
)

data class AmortizationRow(
    val month: Int,
    val emi: Double,
    val principal: Double,
    val interest: Double,
    val balance: Double
)

data class EmiResult(
    val monthlyEmi: Double,
    val totalInterest: Double,
    val totalPayment: Double,
    val amortizationSchedule: List<AmortizationRow>
)

data class LoanComparisonResult(
    val emiA: Double,
    val totalInterestA: Double,
    val totalPaymentA: Double,
    val emiB: Double,
    val totalInterestB: Double,
    val totalPaymentB: Double,
    val betterDeal: String
)

data class SavingsGoalResult(
    val requiredMonthlyInvestment: Double,
    val requiredLumpsumToday: Double,
    val yearlyGrowth: List<YearlyGrowth>
)

data class TipSplitResult(
    val tipAmount: Double,
    val totalBill: Double,
    val perPersonAmount: Double
)

enum class AgeGroup { BELOW_60, BETWEEN_60_80, ABOVE_80 }

data class SlabTaxRow(
    val slabLabel: String,
    val taxableAmount: Double,
    val tax: Double
)

data class TaxEstimatorResult(
    val taxOldRegime: Double,
    val taxNewRegime: Double,
    val recommendation: String,
    val slabBreakdownOld: List<SlabTaxRow>,
    val slabBreakdownNew: List<SlabTaxRow>
)

data class RetirementResult(
    val corpusNeeded: Double,
    val gapAmount: Double,
    val requiredMonthlySip: Double,
    val accumulationTimeline: List<YearlyGrowth>,
    val distributionTimeline: List<YearlyGrowth>
)

data class FireResult(
    val fireNumber: Double,
    val currentTrajectoryValueAtFire: Double,
    val gap: Double,
    val monthlySavingNeeded: Double,
    val isFireAchievable: Boolean,
    val currentPath: List<YearlyGrowth>,
    val requiredPath: List<YearlyGrowth>
)

data class InflationResult(
    val futureValueOfCurrentAmount: Double,
    val requiredFutureAmountForSamePurchasingPower: Double,
    val yearlyPurchasingPowerCurve: List<YearlyGrowth>
)

data class FdResult(
    val maturityAmount: Double,
    val totalInterest: Double,
    val yearlyGrowth: List<YearlyGrowth>
)

data class PpfYearRow(
    val year: Int,
    val deposit: Double,
    val interest: Double,
    val closingBalance: Double
)

data class PpfResult(
    val totalInvested: Double,
    val totalInterest: Double,
    val maturityValue: Double,
    val breakdown: List<PpfYearRow>
)

data class CagrResult(
    val cagr: Double,
    val absoluteReturnPercent: Double,
    val yearlyGrowth: List<YearlyGrowth>
)

data class WhatIfResult<T>(
    val planA: T,
    val planB: T,
    val comparisonSummary: String
)
