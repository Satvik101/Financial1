package com.fincalc.app.domain.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FormulaEngineTest {

    @Test
    fun `compound interest returns higher than principal`() {
        val result = FormulaEngine.compoundInterest(100000.0, 10.0, 5.0, 1)
        assertTrue(result.maturityAmount > 100000.0)
    }

    @Test
    fun `sip computes expected invested amount`() {
        val result = FormulaEngine.sip(5000.0, 12.0, 120)
        assertEquals(600000.0, result.investedAmount, 0.001)
        assertTrue(result.totalValue > result.investedAmount)
    }

    @Test
    fun `emi total payment greater than principal`() {
        val result = FormulaEngine.emi(1000000.0, 9.0, 120)
        assertTrue(result.totalPayment > 1000000.0)
        assertEquals(120, result.amortizationSchedule.size)
    }

    @Test
    fun `cagr positive for growth`() {
        val result = FormulaEngine.cagr(100000.0, 200000.0, 5)
        assertTrue(result.cagr > 0.0)
        assertEquals(5, result.yearlyGrowth.last().year)
    }
}
