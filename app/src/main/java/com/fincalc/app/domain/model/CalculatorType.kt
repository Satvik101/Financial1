package com.fincalc.app.domain.model

enum class CalculatorType(val title: String, val description: String, val accentColorHex: String) {
    COMPOUND_INTEREST("Compound Interest", "Compound growth over time", "#1A73E8"),
    SIP("SIP", "Monthly investment growth", "#34A853"),
    STEP_UP_SIP("Step-Up SIP", "SIP with yearly increase", "#009688"),
    SIP_LUMPSUM("SIP + Lumpsum", "Combined monthly + one-time", "#00BCD4"),
    STEP_UP_SIP_LUMPSUM("Step-Up SIP + Lumpsum", "Advanced mixed investment", "#3F51B5"),
    LUMPSUM("Lumpsum", "One-time investment growth", "#9C27B0"),
    EMI("EMI", "Loan EMI and schedule", "#EA4335"),
    LOAN_COMPARISON("Loan Comparison", "Compare two loans", "#FB8C00"),
    SAVINGS_GOAL("Savings Goal", "Monthly/lumpsum needed", "#FFC107"),
    TIP_SPLIT("Tip Split", "Split bill quickly", "#E91E63"),
    TAX_ESTIMATOR("Tax Estimator", "Old vs new regime", "#795548"),
    RETIREMENT_PLANNER("Retirement Planner", "Corpus planning", "#673AB7"),
    FIRE_CALCULATOR("FIRE Calculator", "Financial independence path", "#FF5722"),
    INFLATION_ADJUSTER("Inflation Adjuster", "Future value & purchasing power", "#607D8B"),
    FD("FD Calculator", "Fixed deposit maturity", "#CDDC39"),
    PPF("PPF Calculator", "Public provident fund growth", "#2E7D32"),
    CAGR("CAGR Calculator", "Annualized growth rate", "#546E7A")
}
