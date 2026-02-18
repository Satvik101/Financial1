## FinCalc — Package-by-Feature Folder Structure

```
app/
├── build.gradle.kts
├── proguard-rules.pro
├── src/
│   ├── main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/fincalc/app/
│   │   │
│   │   │   ── FinCalcApplication.kt                  // Application class (Room init, AdMob init)
│   │   │
│   │   │   ── core/                                   // ── CORE MODULE ──
│   │   │   ├── base/
│   │   │   │   ├── BaseFragment.kt                    // Common fragment setup (viewBinding, toolbar)
│   │   │   │   └── BaseViewModel.kt                   // Loading/error state, coroutine scope
│   │   │   ├── extensions/
│   │   │   │   ├── ContextExt.kt                      // Toast, dp→px, colour helpers
│   │   │   │   ├── DoubleExt.kt                       // toFormattedCurrency(), toAbbreviated()
│   │   │   │   ├── EditTextExt.kt                     // clearErrorOnType(), doubleValue()
│   │   │   │   ├── ViewExt.kt                         // show(), hide(), slideUp(), fadeIn()
│   │   │   │   └── FragmentExt.kt                     // navArgs helpers, collectFlow()
│   │   │   ├── formatter/
│   │   │   │   ├── CurrencyTextWatcher.kt             // Auto comma-formatting TextWatcher
│   │   │   │   ├── NumberFormatter.kt                 // Indian vs International formatting
│   │   │   │   └── CurrencySymbolProvider.kt          // ₹ / $ / € / £ from SharedPrefs
│   │   │   ├── result/
│   │   │   │   └── Resource.kt                        // Sealed class: Loading / Success / Error
│   │   │   ├── constants/
│   │   │   │   └── AppConstants.kt                    // AD_INTERVAL, DB_NAME, PREFS_NAME, etc.
│   │   │   └── utils/
│   │   │       ├── PdfExportUtil.kt                   // PdfDocument helper (header, table, chart bitmap)
│   │   │       ├── ShareUtil.kt                       // Canvas → Bitmap → share intent
│   │   │       ├── InputValidator.kt                  // Reusable validation rules
│   │   │       └── ChartStyler.kt                     // Default MPAndroidChart theme/colors
│   │   │
│   │   │   ── data/                                   // ── DATA LAYER ──
│   │   │   ├── local/
│   │   │   │   ├── db/
│   │   │   │   │   ├── FinCalcDatabase.kt             // @Database (entities, version, migrations)
│   │   │   │   │   ├── dao/
│   │   │   │   │   │   ├── HistoryDao.kt
│   │   │   │   │   │   └── GoalDao.kt
│   │   │   │   │   └── entity/
│   │   │   │   │       ├── HistoryEntity.kt           // id, calcType, inputsJson, resultJson, timestamp
│   │   │   │   │       └── GoalEntity.kt              // id, name, targetAmount, deadline, savedAmount
│   │   │   │   └── prefs/
│   │   │   │       └── AppPreferences.kt              // SharedPrefs wrapper (calcCount, currency, theme, numberFormat)
│   │   │   └── repository/
│   │   │       ├── HistoryRepository.kt
│   │   │       └── GoalRepository.kt
│   │   │
│   │   │   ── domain/                                 // ── DOMAIN / FORMULA ENGINE ──
│   │   │   ├── engine/
│   │   │   │   └── FormulaEngine.kt                   // Pure functions — every financial formula
│   │   │   ├── model/
│   │   │   │   ├── CalculatorType.kt                  // Enum of 17 calculator types + metadata
│   │   │   │   ├── CompoundInterestResult.kt
│   │   │   │   ├── SipResult.kt
│   │   │   │   ├── StepUpSipResult.kt
│   │   │   │   ├── SipLumpsumResult.kt
│   │   │   │   ├── StepUpSipLumpsumResult.kt
│   │   │   │   ├── LumpsumResult.kt
│   │   │   │   ├── EmiResult.kt
│   │   │   │   ├── AmortizationRow.kt                 // Month, emi, principal, interest, balance
│   │   │   │   ├── LoanComparisonResult.kt
│   │   │   │   ├── SavingsGoalResult.kt
│   │   │   │   ├── TipSplitResult.kt
│   │   │   │   ├── TaxEstimatorResult.kt
│   │   │   │   ├── RetirementResult.kt
│   │   │   │   ├── FireResult.kt
│   │   │   │   ├── InflationResult.kt
│   │   │   │   ├── FdResult.kt
│   │   │   │   ├── PpfResult.kt
│   │   │   │   ├── PpfYearRow.kt                      // Year, deposit, interest, balance
│   │   │   │   ├── CagrResult.kt
│   │   │   │   └── YearlyGrowth.kt                    // Reusable (year, invested, value)
│   │   │   └── usecase/                               // Optional — thin use-cases if logic grows
│   │   │       └── CalculateUseCase.kt                // Delegates to FormulaEngine + saves history
│   │   │
│   │   │   ── feature/                                // ── FEATURES (one package per screen) ──
│   │   │   │
│   │   │   ├── splash/
│   │   │   │   └── SplashFragment.kt
│   │   │   │
│   │   │   ├── home/
│   │   │   │   ├── HomeFragment.kt
│   │   │   │   ├── HomeViewModel.kt
│   │   │   │   └── adapter/
│   │   │   │       ├── CalculatorCardAdapter.kt       // RecyclerView grid adapter
│   │   │   │       └── CalculatorCardItem.kt          // UI model for each card
│   │   │   │
│   │   │   ├── calculator/                            // All 17 calculators live here
│   │   │   │   ├── compoundinterest/
│   │   │   │   │   ├── CompoundInterestFragment.kt
│   │   │   │   │   └── CompoundInterestViewModel.kt
│   │   │   │   ├── sip/
│   │   │   │   │   ├── SipFragment.kt
│   │   │   │   │   └── SipViewModel.kt
│   │   │   │   ├── stepupsip/
│   │   │   │   │   ├── StepUpSipFragment.kt
│   │   │   │   │   └── StepUpSipViewModel.kt
│   │   │   │   ├── siplumpsum/
│   │   │   │   │   ├── SipLumpsumFragment.kt
│   │   │   │   │   └── SipLumpsumViewModel.kt
│   │   │   │   ├── stepupsiplumpsum/
│   │   │   │   │   ├── StepUpSipLumpsumFragment.kt
│   │   │   │   │   └── StepUpSipLumpsumViewModel.kt
│   │   │   │   ├── lumpsum/
│   │   │   │   │   ├── LumpsumFragment.kt
│   │   │   │   │   └── LumpsumViewModel.kt
│   │   │   │   ├── emi/
│   │   │   │   │   ├── EmiFragment.kt
│   │   │   │   │   ├── EmiViewModel.kt
│   │   │   │   │   └── adapter/
│   │   │   │   │       └── AmortizationAdapter.kt     // Amortization table RecyclerView
│   │   │   │   ├── loancomparison/
│   │   │   │   │   ├── LoanComparisonFragment.kt
│   │   │   │   │   └── LoanComparisonViewModel.kt
│   │   │   │   ├── savingsgoal/
│   │   │   │   │   ├── SavingsGoalFragment.kt
│   │   │   │   │   └── SavingsGoalViewModel.kt
│   │   │   │   ├── tipsplit/
│   │   │   │   │   ├── TipSplitFragment.kt
│   │   │   │   │   └── TipSplitViewModel.kt
│   │   │   │   ├── taxestimator/
│   │   │   │   │   ├── TaxEstimatorFragment.kt
│   │   │   │   │   └── TaxEstimatorViewModel.kt
│   │   │   │   ├── retirement/
│   │   │   │   │   ├── RetirementFragment.kt
│   │   │   │   │   └── RetirementViewModel.kt
│   │   │   │   ├── fire/
│   │   │   │   │   ├── FireFragment.kt
│   │   │   │   │   └── FireViewModel.kt
│   │   │   │   ├── inflation/
│   │   │   │   │   ├── InflationFragment.kt
│   │   │   │   │   └── InflationViewModel.kt
│   │   │   │   ├── fd/
│   │   │   │   │   ├── FdFragment.kt
│   │   │   │   │   └── FdViewModel.kt
│   │   │   │   ├── ppf/
│   │   │   │   │   ├── PpfFragment.kt
│   │   │   │   │   └── PpfViewModel.kt
│   │   │   │   ├── cagr/
│   │   │   │   │   ├── CagrFragment.kt
│   │   │   │   │   └── CagrViewModel.kt
│   │   │   │   └── whatif/                            // What-If overlay / comparison mode
│   │   │   │       ├── WhatIfContainerFragment.kt     // Hosts Plan A + Plan B side-by-side or tabs
│   │   │   │       └── WhatIfViewModel.kt
│   │   │   │
│   │   │   ├── history/
│   │   │   │   ├── HistoryFragment.kt
│   │   │   │   ├── HistoryViewModel.kt
│   │   │   │   └── adapter/
│   │   │   │       ├── HistoryAdapter.kt
│   │   │   │       └── HistoryItem.kt
│   │   │   │
│   │   │   ├── goals/
│   │   │   │   ├── GoalsFragment.kt
│   │   │   │   ├── GoalsViewModel.kt
│   │   │   │   ├── AddGoalBottomSheet.kt
│   │   │   │   ├── UpdateGoalBottomSheet.kt
│   │   │   │   └── adapter/
│   │   │   │       ├── GoalAdapter.kt
│   │   │   │       └── GoalItem.kt
│   │   │   │
│   │   │   └── settings/
│   │   │       ├── SettingsFragment.kt
│   │   │       └── SettingsViewModel.kt
│   │   │
│   │   │   ── ads/                                    // ── AD MANAGER ──
│   │   │   └── AdManager.kt                           // Global calc counter, interstitial load/show
│   │   │
│   │   │   ── di/                                     // ── DEPENDENCY INJECTION (manual or Hilt) ──
│   │   │   └── AppModule.kt                           // Provides Room DB, DAOs, Repos, Prefs
│   │   │
│   │   │   ── navigation/                             // ── NAVIGATION ──
│   │   │   └── MainActivity.kt                        // Single Activity host
│   │   │
│   │   ├── res/
│   │   │   ├── anim/
│   │   │   │   ├── slide_in_right.xml
│   │   │   │   ├── slide_out_left.xml
│   │   │   │   ├── slide_in_left.xml
│   │   │   │   ├── slide_out_right.xml
│   │   │   │   ├── slide_up.xml
│   │   │   │   └── fade_in.xml
│   │   │   │
│   │   │   ├── color/
│   │   │   │   └── bottom_nav_color.xml
│   │   │   │
│   │   │   ├── drawable/
│   │   │   │   ├── ic_launcher_foreground.xml
│   │   │   │   ├── ic_launcher_background.xml
│   │   │   │   ├── splash_gradient.xml                // #0D1117 → #1A1A2E
│   │   │   │   ├── bg_card_rounded.xml                // 16dp corners
│   │   │   │   ├── bg_button_primary.xml
│   │   │   │   ├── bg_chip.xml
│   │   │   │   ├── ic_empty_history.xml               // Empty-state illustration
│   │   │   │   ├── ic_empty_goals.xml
│   │   │   │   ├── ic_compound_interest.xml
│   │   │   │   ├── ic_sip.xml
│   │   │   │   ├── ic_stepup_sip.xml
│   │   │   │   ├── ic_sip_lumpsum.xml
│   │   │   │   ├── ic_stepup_sip_lumpsum.xml
│   │   │   │   ├── ic_lumpsum.xml
│   │   │   │   ├── ic_emi.xml
│   │   │   │   ├── ic_loan_compare.xml
│   │   │   │   ├── ic_savings_goal.xml
│   │   │   │   ├── ic_tip_split.xml
│   │   │   │   ├── ic_tax.xml
│   │   │   │   ├── ic_retirement.xml
│   │   │   │   ├── ic_fire.xml
│   │   │   │   ├── ic_inflation.xml
│   │   │   │   ├── ic_fd.xml
│   │   │   │   ├── ic_ppf.xml
│   │   │   │   └── ic_cagr.xml
│   │   │   │
│   │   │   ├── layout/
│   │   │   │   ├── activity_main.xml                  // NavHostFragment + BottomNavigationView
│   │   │   │   ├── fragment_splash.xml
│   │   │   │   ├── fragment_home.xml                  // Greeting + SearchBar + RecyclerView (grid)
│   │   │   │   ├── item_calculator_card.xml           // Single card in home grid
│   │   │   │   ├── fragment_compound_interest.xml
│   │   │   │   ├── fragment_sip.xml
│   │   │   │   ├── fragment_step_up_sip.xml
│   │   │   │   ├── fragment_sip_lumpsum.xml
│   │   │   │   ├── fragment_step_up_sip_lumpsum.xml
│   │   │   │   ├── fragment_lumpsum.xml
│   │   │   │   ├── fragment_emi.xml
│   │   │   │   ├── item_amortization_row.xml
│   │   │   │   ├── fragment_loan_comparison.xml
│   │   │   │   ├── fragment_savings_goal.xml
│   │   │   │   ├── fragment_tip_split.xml
│   │   │   │   ├── fragment_tax_estimator.xml
│   │   │   │   ├── fragment_retirement.xml
│   │   │   │   ├── fragment_fire.xml
│   │   │   │   ├── fragment_inflation.xml
│   │   │   │   ├── fragment_fd.xml
│   │   │   │   ├── fragment_ppf.xml
│   │   │   │   ├── fragment_cagr.xml
│   │   │   │   ├── fragment_history.xml
│   │   │   │   ├── item_history.xml
│   │   │   │   ├── fragment_goals.xml
│   │   │   │   ├── item_goal.xml
│   │   │   │   ├── bottom_sheet_add_goal.xml
│   │   │   │   ├── bottom_sheet_update_goal.xml
│   │   │   │   ├── bottom_sheet_info.xml              // Calculator explanation sheet
│   │   │   │   ├── fragment_settings.xml
│   │   │   │   ├── fragment_what_if.xml
│   │   │   │   ├── layout_result_card.xml             // <include> — reusable result section
│   │   │   │   └── layout_action_row.xml              // <include> — Save/Share/Export buttons
│   │   │   │
│   │   │   ├── menu/
│   │   │   │   └── bottom_nav_menu.xml                // Home | History | Goals | Settings
│   │   │   │
│   │   │   ├── navigation/
│   │   │   │   └── nav_graph.xml                      // All fragment destinations + actions
│   │   │   │
│   │   │   ├── values/
│   │   │   │   ├── colors.xml                         // Brand colors + per-calculator accents
│   │   │   │   ├── strings.xml                        // All user-facing strings
│   │   │   │   ├── dimens.xml                         // Spacing, radii, text sizes
│   │   │   │   ├── themes.xml                         // Material 3 Light theme
│   │   │   │   ├── styles.xml                         // Button, Card, TextInput styles
│   │   │   │   └── arrays.xml                         // Compounding frequency options, etc.
│   │   │   │
│   │   │   ├── values-night/
│   │   │   │   ├── colors.xml                         // Dark palette (#0D1117, #161B22)
│   │   │   │   └── themes.xml                         // Material 3 Dark theme
│   │   │   │
│   │   │   └── xml/
│   │   │       ├── backup_rules.xml
│   │   │       └── network_security_config.xml        // Allow cleartext for AdMob debug only
│   │   │
│   │   └── assets/                                    // (empty — reserved for fonts if needed)
│   │
│   ├── test/java/com/fincalc/app/
│   │   └── domain/engine/
│   │       └── FormulaEngineTest.kt                   // Unit tests for every formula
│   │
│   └── androidTest/java/com/fincalc/app/
│       └── data/local/db/
│           ├── HistoryDaoTest.kt
│           └── GoalDaoTest.kt
│
├── gradle/
│   └── libs.versions.toml                             // Version catalog
│
├── build.gradle.kts                                   // Project-level
├── settings.gradle.kts
└── gradle.properties
```
