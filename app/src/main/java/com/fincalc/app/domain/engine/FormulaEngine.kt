package com.fincalc.app.domain.engine

import com.fincalc.app.domain.model.AgeGroup
import com.fincalc.app.domain.model.CagrResult
import com.fincalc.app.domain.model.CompoundInterestResult
import com.fincalc.app.domain.model.EmiResult
import com.fincalc.app.domain.model.FdResult
import com.fincalc.app.domain.model.FireResult
import com.fincalc.app.domain.model.InflationResult
import com.fincalc.app.domain.model.LoanComparisonResult
import com.fincalc.app.domain.model.LumpsumResult
import com.fincalc.app.domain.model.PpfResult
import com.fincalc.app.domain.model.PpfYearRow
import com.fincalc.app.domain.model.RetirementResult
import com.fincalc.app.domain.model.SavingsGoalResult
import com.fincalc.app.domain.model.SipLumpsumResult
import com.fincalc.app.domain.model.SipResult
import com.fincalc.app.domain.model.SlabTaxRow
import com.fincalc.app.domain.model.StepUpSipLumpsumResult
import com.fincalc.app.domain.model.StepUpSipResult
import com.fincalc.app.domain.model.TaxEstimatorResult
import com.fincalc.app.domain.model.TipSplitResult
import com.fincalc.app.domain.model.YearlyGrowth
import com.fincalc.app.domain.model.AmortizationRow
import kotlin.math.max
import kotlin.math.pow

object FormulaEngine {

    fun compoundInterest(
        principal: Double,
        annualRatePercent: Double,
        periodYears: Double,
        compoundsPerYear: Int
    ): CompoundInterestResult {
        val r = annualRatePercent / 100.0
        val maturity = principal * (1 + r / compoundsPerYear).pow(compoundsPerYear * periodYears)
        val interest = maturity - principal
        val yearly = (1..periodYears.toInt().coerceAtLeast(1)).map { year ->
            val value = principal * (1 + r / compoundsPerYear).pow(compoundsPerYear * year.toDouble())
            YearlyGrowth(year = year, invested = principal, value = value)
        }
        return CompoundInterestResult(principal, maturity, interest, yearly)
    }

    fun sip(
        monthlyInvestment: Double,
        annualRatePercent: Double,
        totalMonths: Int
    ): SipResult {
        val r = (annualRatePercent / 100.0) / 12.0
        val n = totalMonths.toDouble()
        val fv = if (r == 0.0) {
            monthlyInvestment * totalMonths
        } else {
            monthlyInvestment * (((1 + r).pow(n) - 1) / r) * (1 + r)
        }
        val invested = monthlyInvestment * totalMonths
        val returns = fv - invested
        val yearly = yearlySipGrowth(monthlyInvestment, annualRatePercent, totalMonths)
        return SipResult(invested, returns, fv, yearly)
    }

    fun stepUpSip(
        startingMonthlySip: Double,
        stepUpPercent: Double,
        annualRatePercent: Double,
        years: Int
    ): StepUpSipResult {
        var totalValue = 0.0
        var totalInvested = 0.0
        var currentSip = startingMonthlySip
        val monthlyRate = annualRatePercent / 1200.0
        val yearlySip = mutableListOf<Double>()
        val growth = mutableListOf<YearlyGrowth>()

        for (year in 1..years) {
            yearlySip += currentSip
            val monthsRemaining = (years - year + 1) * 12
            val yearlyContribution = currentSip * 12
            totalInvested += yearlyContribution

            val fvOfThisYear = if (monthlyRate == 0.0) {
                yearlyContribution
            } else {
                currentSip * (((1 + monthlyRate).pow(12.0) - 1) / monthlyRate) *
                    (1 + monthlyRate) * (1 + monthlyRate).pow((monthsRemaining - 12).toDouble())
            }
            totalValue += fvOfThisYear
            growth += YearlyGrowth(year, totalInvested, totalValue)
            currentSip *= (1 + stepUpPercent / 100.0)
        }

        val withoutStep = sip(startingMonthlySip, annualRatePercent, years * 12).yearlyGrowth
        return StepUpSipResult(
            totalInvested = totalInvested,
            estimatedReturns = totalValue - totalInvested,
            totalValue = totalValue,
            finalMonthlySip = currentSip,
            yearlySipAmounts = yearlySip,
            yearlyGrowth = growth,
            yearlyGrowthWithoutStepUp = withoutStep
        )
    }

    fun sipPlusLumpsum(
        lumpsum: Double,
        monthlySip: Double,
        annualRatePercent: Double,
        totalMonths: Int
    ): SipLumpsumResult {
        val monthlyRate = annualRatePercent / 1200.0
        val lumpsumGrowth = lumpsum * (1 + monthlyRate).pow(totalMonths.toDouble())
        val sipResult = sip(monthlySip, annualRatePercent, totalMonths)
        val total = lumpsumGrowth + sipResult.totalValue
        val invested = lumpsum + sipResult.investedAmount
        val yearly = (1..(totalMonths / 12).coerceAtLeast(1)).map { y ->
            val months = y * 12
            val lumpValue = lumpsum * (1 + monthlyRate).pow(months.toDouble())
            val sipValue = sip(monthlySip, annualRatePercent, months).totalValue
            YearlyGrowth(y, lumpsum + (monthlySip * months), lumpValue + sipValue)
        }
        return SipLumpsumResult(
            lumpsumGrowth = lumpsumGrowth,
            sipValue = sipResult.totalValue,
            totalValue = total,
            totalInvested = invested,
            totalReturns = total - invested,
            yearlyGrowth = yearly
        )
    }

    fun stepUpSipPlusLumpsum(
        lumpsum: Double,
        startingSip: Double,
        stepUpPercent: Double,
        annualRatePercent: Double,
        years: Int
    ): StepUpSipLumpsumResult {
        val sipPart = stepUpSip(startingSip, stepUpPercent, annualRatePercent, years)
        val lumpsumPart = lumpsum * (1 + annualRatePercent / 1200.0).pow((years * 12).toDouble())
        val totalValue = sipPart.totalValue + lumpsumPart
        val totalInvested = sipPart.totalInvested + lumpsum
        return StepUpSipLumpsumResult(
            lumpsumGrowth = lumpsumPart,
            stepUpSipValue = sipPart.totalValue,
            totalInvested = totalInvested,
            totalReturns = totalValue - totalInvested,
            totalValue = totalValue,
            yearlyGrowth = sipPart.yearlyGrowth.map { item ->
                val lumpAtYear = lumpsum * (1 + annualRatePercent / 100.0).pow(item.year.toDouble())
                item.copy(value = item.value + lumpAtYear, invested = item.invested + lumpsum)
            }
        )
    }

    fun lumpsum(
        amount: Double,
        annualRatePercent: Double,
        years: Double
    ): LumpsumResult {
        val fv = amount * (1 + annualRatePercent / 100.0).pow(years)
        val yearly = (1..years.toInt().coerceAtLeast(1)).map { y ->
            YearlyGrowth(y, amount, amount * (1 + annualRatePercent / 100.0).pow(y.toDouble()))
        }
        return LumpsumResult(amount, fv - amount, fv, yearly)
    }

    fun emi(
        loanAmount: Double,
        annualRatePercent: Double,
        totalMonths: Int
    ): EmiResult {
        val r = annualRatePercent / 1200.0
        val n = totalMonths.toDouble()
        val emi = if (r == 0.0) loanAmount / n else {
            loanAmount * r * (1 + r).pow(n) / ((1 + r).pow(n) - 1)
        }

        var balance = loanAmount
        var totalInterest = 0.0
        val schedule = mutableListOf<AmortizationRow>()
        for (month in 1..totalMonths) {
            val interest = balance * r
            val principal = emi - interest
            balance = (balance - principal).coerceAtLeast(0.0)
            totalInterest += interest
            schedule += AmortizationRow(month, emi, principal, interest, balance)
        }
        return EmiResult(
            monthlyEmi = emi,
            totalInterest = totalInterest,
            totalPayment = emi * totalMonths,
            amortizationSchedule = schedule
        )
    }

    fun compareLoans(
        amountA: Double,
        rateA: Double,
        monthsA: Int,
        amountB: Double,
        rateB: Double,
        monthsB: Int
    ): LoanComparisonResult {
        val a = emi(amountA, rateA, monthsA)
        val b = emi(amountB, rateB, monthsB)
        val better = if (a.totalPayment <= b.totalPayment) "Loan A" else "Loan B"
        return LoanComparisonResult(
            a.monthlyEmi,
            a.totalInterest,
            a.totalPayment,
            b.monthlyEmi,
            b.totalInterest,
            b.totalPayment,
            better
        )
    }

    fun savingsGoal(
        targetAmount: Double,
        annualRatePercent: Double,
        totalMonths: Int
    ): SavingsGoalResult {
        val monthlyRate = annualRatePercent / 1200.0
        val n = totalMonths.toDouble()
        val monthly = if (monthlyRate == 0.0) {
            targetAmount / totalMonths
        } else {
            targetAmount / ((((1 + monthlyRate).pow(n) - 1) / monthlyRate) * (1 + monthlyRate))
        }
        val lumpsum = targetAmount / (1 + monthlyRate).pow(n)
        val yearly = (1..(totalMonths / 12).coerceAtLeast(1)).map { year ->
            val months = year * 12
            val value = sip(monthly, annualRatePercent, months).totalValue
            YearlyGrowth(year, monthly * months, value)
        }
        return SavingsGoalResult(monthly, lumpsum, yearly)
    }

    fun tipSplit(
        billAmount: Double,
        tipPercent: Double,
        people: Int
    ): TipSplitResult {
        val tipAmount = billAmount * tipPercent / 100.0
        val total = billAmount + tipAmount
        return TipSplitResult(
            tipAmount = tipAmount,
            totalBill = total,
            perPersonAmount = total / people.coerceAtLeast(1)
        )
    }

    fun taxEstimator(
        annualIncome: Double,
        deduction80C: Double,
        hraExemption: Double,
        otherDeductions: Double,
        ageGroup: AgeGroup
    ): TaxEstimatorResult {
        val taxableOld = (annualIncome - deduction80C - hraExemption - otherDeductions).coerceAtLeast(0.0)
        val taxableNew = annualIncome.coerceAtLeast(0.0)

        val oldRows = computeOldRegimeTaxRows(taxableOld, ageGroup)
        val newRows = computeNewRegimeTaxRows(taxableNew)
        val oldTax = oldRows.sumOf { it.tax }
        val newTax = newRows.sumOf { it.tax }
        val recommendation = if (oldTax <= newTax) {
            "You save ₹${format2(newTax - oldTax)} with Old Regime"
        } else {
            "You save ₹${format2(oldTax - newTax)} with New Regime"
        }
        return TaxEstimatorResult(oldTax, newTax, recommendation, oldRows, newRows)
    }

    fun retirementPlanner(
        currentAge: Int,
        retirementAge: Int,
        lifeExpectancy: Int,
        currentMonthlyExpenses: Double,
        inflationPercent: Double,
        currentSavings: Double,
        preRetirementReturnPercent: Double,
        postRetirementReturnPercent: Double
    ): RetirementResult {
        val yearsToRetire = (retirementAge - currentAge).coerceAtLeast(1)
        val yearsAfterRetire = (lifeExpectancy - retirementAge).coerceAtLeast(1)
        val inflatedExpenseAtRetirement = currentMonthlyExpenses * (1 + inflationPercent / 100.0).pow(yearsToRetire.toDouble())

        val realReturnPost = ((1 + postRetirementReturnPercent / 100.0) / (1 + inflationPercent / 100.0)) - 1
        val annualExpenseAtRetirement = inflatedExpenseAtRetirement * 12
        val corpusNeeded = if (realReturnPost <= 0.0) {
            annualExpenseAtRetirement * yearsAfterRetire
        } else {
            annualExpenseAtRetirement * (1 - (1 + realReturnPost).pow(-yearsAfterRetire.toDouble())) / realReturnPost
        }

        val futureSavings = currentSavings * (1 + preRetirementReturnPercent / 100.0).pow(yearsToRetire.toDouble())
        val gap = (corpusNeeded - futureSavings).coerceAtLeast(0.0)
        val sipNeeded = savingsGoal(gap, preRetirementReturnPercent, yearsToRetire * 12).requiredMonthlyInvestment

        val accumulation = (0..yearsToRetire).map { y ->
            val invested = if (y == 0) currentSavings else currentSavings + sipNeeded * y * 12
            val value = if (y == 0) currentSavings else {
                currentSavings * (1 + preRetirementReturnPercent / 100.0).pow(y.toDouble()) +
                    sip(sipNeeded, preRetirementReturnPercent, y * 12).totalValue
            }
            YearlyGrowth(y, invested, value)
        }

        val distribution = (0..yearsAfterRetire).map { y ->
            val remaining = (corpusNeeded - y * annualExpenseAtRetirement).coerceAtLeast(0.0)
            YearlyGrowth(y, corpusNeeded, remaining)
        }

        return RetirementResult(corpusNeeded, gap, sipNeeded, accumulation, distribution)
    }

    fun fireCalculator(
        currentAnnualExpenses: Double,
        inflationPercent: Double,
        yearsToFire: Int,
        safeWithdrawalRatePercent: Double,
        currentInvestments: Double,
        expectedReturnPercent: Double
    ): FireResult {
        val expenseAtFire = currentAnnualExpenses * (1 + inflationPercent / 100.0).pow(yearsToFire.toDouble())
        val swr = (safeWithdrawalRatePercent / 100.0).coerceAtLeast(0.0001)
        val fireNumber = expenseAtFire / swr

        val currentTrajectory = currentInvestments * (1 + expectedReturnPercent / 100.0).pow(yearsToFire.toDouble())
        val gap = (fireNumber - currentTrajectory).coerceAtLeast(0.0)
        val monthlyNeeded = savingsGoal(gap, expectedReturnPercent, yearsToFire * 12).requiredMonthlyInvestment

        val currentPath = (0..yearsToFire).map { y ->
            YearlyGrowth(y, currentInvestments, currentInvestments * (1 + expectedReturnPercent / 100.0).pow(y.toDouble()))
        }
        val requiredPath = (0..yearsToFire).map { y ->
            val value = currentInvestments * (1 + expectedReturnPercent / 100.0).pow(y.toDouble()) +
                sip(monthlyNeeded, expectedReturnPercent, y * 12).totalValue
            YearlyGrowth(y, currentInvestments + monthlyNeeded * 12 * y, value)
        }

        return FireResult(
            fireNumber = fireNumber,
            currentTrajectoryValueAtFire = currentTrajectory,
            gap = gap,
            monthlySavingNeeded = monthlyNeeded,
            isFireAchievable = gap <= 1.0,
            currentPath = currentPath,
            requiredPath = requiredPath
        )
    }

    fun inflationAdjuster(
        currentAmount: Double,
        inflationPercent: Double,
        years: Int
    ): InflationResult {
        val factor = (1 + inflationPercent / 100.0).pow(years.toDouble())
        val futureValue = currentAmount / factor
        val required = currentAmount * factor
        val curve = (0..years).map { y ->
            val value = currentAmount / (1 + inflationPercent / 100.0).pow(y.toDouble())
            YearlyGrowth(y, currentAmount, value)
        }
        return InflationResult(
            futureValueOfCurrentAmount = futureValue,
            requiredFutureAmountForSamePurchasingPower = required,
            yearlyPurchasingPowerCurve = curve
        )
    }

    fun fdCalculator(
        deposit: Double,
        annualRatePercent: Double,
        years: Double,
        compoundsPerYear: Int
    ): FdResult {
        val ci = compoundInterest(deposit, annualRatePercent, years, compoundsPerYear)
        return FdResult(ci.maturityAmount, ci.totalInterest, ci.yearlyGrowth)
    }

    fun ppfCalculator(
        yearlyDeposit: Double,
        annualRatePercent: Double = 7.1,
        years: Int = 15
    ): PpfResult {
        var balance = 0.0
        var invested = 0.0
        val rows = mutableListOf<PpfYearRow>()

        for (year in 1..years) {
            invested += yearlyDeposit
            val opening = balance + yearlyDeposit
            val interest = opening * annualRatePercent / 100.0
            balance = opening + interest
            rows += PpfYearRow(year, yearlyDeposit, interest, balance)
        }

        return PpfResult(
            totalInvested = invested,
            totalInterest = balance - invested,
            maturityValue = balance,
            breakdown = rows
        )
    }

    fun cagr(
        initialValue: Double,
        finalValue: Double,
        years: Int
    ): CagrResult {
        val cagr = (finalValue / initialValue).pow(1.0 / years) - 1
        val absReturn = ((finalValue - initialValue) / initialValue) * 100
        val yearly = (0..years).map { y ->
            val value = initialValue * (1 + cagr).pow(y.toDouble())
            YearlyGrowth(y, initialValue, value)
        }
        return CagrResult(cagr * 100, absReturn, yearly)
    }

    private fun yearlySipGrowth(monthly: Double, annualRatePercent: Double, totalMonths: Int): List<YearlyGrowth> {
        val years = max(1, totalMonths / 12)
        return (1..years).map { year ->
            val months = year * 12
            val result = sip(monthly, annualRatePercent, months)
            YearlyGrowth(year, result.investedAmount, result.totalValue)
        }
    }

    private fun computeOldRegimeTaxRows(taxableIncome: Double, ageGroup: AgeGroup): List<SlabTaxRow> {
        val basicExemption = when (ageGroup) {
            AgeGroup.BELOW_60 -> 250000.0
            AgeGroup.BETWEEN_60_80 -> 300000.0
            AgeGroup.ABOVE_80 -> 500000.0
        }

        val rows = mutableListOf<SlabTaxRow>()
        var remaining = taxableIncome

        fun addRow(label: String, slabAmount: Double, rate: Double) {
            if (remaining <= 0) return
            val taxable = minOf(remaining, slabAmount)
            val tax = taxable * rate
            rows += SlabTaxRow(label, taxable, tax)
            remaining -= taxable
        }

        addRow("0 - ${format0(basicExemption)}", basicExemption, 0.0)
        addRow("Next 5L", 500000.0 - basicExemption, 0.05)
        addRow("Next 5L", 500000.0, 0.20)
        addRow("Above 10L", Double.MAX_VALUE, 0.30)
        return rows
    }

    private fun computeNewRegimeTaxRows(taxableIncome: Double): List<SlabTaxRow> {
        val slabs = listOf(
            Triple("0 - 3L", 300000.0, 0.0),
            Triple("3L - 6L", 300000.0, 0.05),
            Triple("6L - 9L", 300000.0, 0.10),
            Triple("9L - 12L", 300000.0, 0.15),
            Triple("12L - 15L", 300000.0, 0.20),
            Triple("Above 15L", Double.MAX_VALUE, 0.30)
        )

        var remaining = taxableIncome
        val rows = mutableListOf<SlabTaxRow>()
        for ((label, amount, rate) in slabs) {
            if (remaining <= 0) break
            val taxable = minOf(remaining, amount)
            rows += SlabTaxRow(label, taxable, taxable * rate)
            remaining -= taxable
        }
        return rows
    }

    private fun format2(value: Double): String = "%.2f".format(value)
    private fun format0(value: Double): String = "%.0f".format(value)
}
