package com.fincalc.app.e2e

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBackUnconditionally
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.fincalc.app.R
import com.fincalc.app.navigation.MainActivity
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class CalculatorSmokeTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    private data class GenericScenario(
        val title: String,
        val a: String,
        val b: String,
        val c: String,
        val d: String? = null,
        val e: String? = null,
        val f: String? = null,
        val g: String? = null,
        val h: String? = null
    )

    @Test
    fun generic_calculators_calculate_without_crash() {
        val scenarios = listOf(
            GenericScenario("Compound Interest", "100000", "12", "5"),
            GenericScenario("SIP", "5000", "12", "10"),
            GenericScenario("Step-Up SIP", "5000", "10", "12", "10"),
            GenericScenario("SIP + Lumpsum", "100000", "3000", "12", "10"),
            GenericScenario("Step-Up SIP + Lumpsum", "100000", "3000", "10", "12", "10"),
            GenericScenario("Lumpsum", "200000", "11", "8"),
            GenericScenario("Loan Comparison", "1000000", "9", "10", "900000", "8.5", "12"),
            GenericScenario("Savings Goal", "5000000", "10", "15"),
            GenericScenario("Tax Estimator", "1800000", "150000", "80000", "50000"),
            GenericScenario("Retirement Planner", "30", "60", "85", "80000", "6", "5000000", "11", "7"),
            GenericScenario("FIRE Calculator", "1200000", "6", "15", "3.5", "3000000", "11"),
            GenericScenario("Inflation Adjuster", "100000", "6", "12"),
            GenericScenario("FD Calculator", "500000", "7.5", "5"),
            GenericScenario("PPF Calculator", "100000", "7.1", "15"),
            GenericScenario("CAGR Calculator", "100000", "250000", "8")
        )

        waitForHome()
        scenarios.forEach { runGenericScenario(it) }
    }

    @Test
    fun emi_calculates_without_crash() {
        waitForHome()
        openCalculatorFromHome("EMI")

        onView(withId(R.id.etLoan)).perform(scrollTo(), replaceText("1000000"), closeSoftKeyboard())
        onView(withId(R.id.etRate)).perform(scrollTo(), replaceText("9"), closeSoftKeyboard())
        onView(withId(R.id.etTenure)).perform(scrollTo(), replaceText("10"), closeSoftKeyboard())
        onView(withId(R.id.btnCalculate)).perform(scrollTo(), click())

        onView(withId(R.id.tvResult)).check(matches(withText(containsString("EMI:"))))

        pressBackUnconditionally()
        waitForHome()
    }

    @Test
    fun tip_split_calculates_without_crash() {
        waitForHome()
        openCalculatorFromHome("Tip Split")

        onView(withId(R.id.etBill)).perform(scrollTo(), replaceText("2500"), closeSoftKeyboard())
        onView(withId(R.id.btnCalculate)).perform(scrollTo(), click())

        onView(withId(R.id.tvPerPerson)).check(matches(withText(containsString("Per Person:"))))

        pressBackUnconditionally()
        waitForHome()
    }

    @Test
    fun compound_interest_what_if_comparison_works_without_crash() {
        waitForHome()
        openCalculatorFromHome("Compound Interest")

        onView(withId(R.id.etA)).perform(scrollTo(), replaceText("100000"), closeSoftKeyboard())
        onView(withId(R.id.etB)).perform(scrollTo(), replaceText("10"), closeSoftKeyboard())
        onView(withId(R.id.etC)).perform(scrollTo(), replaceText("5"), closeSoftKeyboard())
        onView(withId(R.id.btnCalculate)).perform(scrollTo(), click())

        onView(withId(R.id.switchCompare)).perform(scrollTo(), click())
        onView(withId(R.id.btnOpenCompare)).perform(scrollTo(), click())

        onView(withId(R.id.etA1)).perform(scrollTo(), replaceText("100000"), closeSoftKeyboard())
        onView(withId(R.id.etB1)).perform(scrollTo(), replaceText("10"), closeSoftKeyboard())
        onView(withId(R.id.etC1)).perform(scrollTo(), replaceText("5"), closeSoftKeyboard())

        onView(withId(R.id.etA2)).perform(scrollTo(), replaceText("120000"), closeSoftKeyboard())
        onView(withId(R.id.etB2)).perform(scrollTo(), replaceText("11"), closeSoftKeyboard())
        onView(withId(R.id.etC2)).perform(scrollTo(), replaceText("5"), closeSoftKeyboard())

        onView(withId(R.id.btnCompare)).perform(scrollTo(), click())

        onView(withId(R.id.tvComparison)).check(matches(withText(containsString("Plan"))))

        pressBackUnconditionally()
        pressBackUnconditionally()
        waitForHome()
    }

    private fun runGenericScenario(scenario: GenericScenario) {
        openCalculatorFromHome(scenario.title)

        fillGenericInputs(scenario)

        onView(withId(R.id.btnCalculate)).perform(scrollTo(), click())
        onView(withId(R.id.tvResult)).check(matches(not(withText("Calculation error"))))
        onView(withId(R.id.tvBreakdown)).check(matches(not(withText("Please check your inputs and try again"))))
        onView(withId(R.id.tvBreakdown)).check(matches(not(withText(""))))

        pressBackUnconditionally()
        waitForHome()
    }

    private fun fillGenericInputs(scenario: GenericScenario) {
        onView(withId(R.id.etA)).perform(scrollTo(), replaceText(scenario.a), closeSoftKeyboard())
        onView(withId(R.id.etB)).perform(scrollTo(), replaceText(scenario.b), closeSoftKeyboard())
        onView(withId(R.id.etC)).perform(scrollTo(), replaceText(scenario.c), closeSoftKeyboard())

        scenario.d?.let { onView(withId(R.id.etD)).perform(scrollTo(), replaceText(it), closeSoftKeyboard()) }
        scenario.e?.let { onView(withId(R.id.etE)).perform(scrollTo(), replaceText(it), closeSoftKeyboard()) }
        scenario.f?.let { onView(withId(R.id.etF)).perform(scrollTo(), replaceText(it), closeSoftKeyboard()) }
        scenario.g?.let { onView(withId(R.id.etG)).perform(scrollTo(), replaceText(it), closeSoftKeyboard()) }
        scenario.h?.let { onView(withId(R.id.etH)).perform(scrollTo(), replaceText(it), closeSoftKeyboard()) }
    }

    private fun openCalculatorFromHome(title: String) {
        onView(withId(R.id.etSearch)).perform(replaceText(title), closeSoftKeyboard())
        onView(withText(title)).perform(click())
    }

    private fun waitForHome() {
        onView(withId(R.id.rvCalculators)).check(matches(isDisplayed()))
    }
}
