package com.example.data.model

data class FinancialYear(
    val yearLabel: String, // "Year 1", "Year 2", etc.
    val customersCount: Int,
    val arrMillions: Double,
    val grossMarginPercent: Double,
    val netBurnOrProfitMillions: Double
)

data class UnitEconomics(
    val targetEnterpriseAcvThousands: Double, // Average Contract Value in $k (e.g. $180k/yr)
    val customerAcquisitionCostThousands: Double, // CAC in $k (e.g. $45k)
    val customerLifetimeYears: Double, // e.g. 7.5 years
    val ltvThousands: Double, // LTV = ACV * Margin * Lifetime
    val ltvToCacRatio: Double, // e.g. 6.2x
    val paybackPeriodMonths: Int, // e.g. 8 months
    val netRevenueRetentionPercent: Double // e.g. 132%
)

data class CustomerRoiAnalysis(
    val annualClientCostSavingsMillions: Double,
    val implementationTimeWeeks: Int,
    val enterpriseRoiMultiple: Double, // e.g. 7.8x ROI
    val paybackDays: Int // e.g. 75 days
)

data class SensitivityScenario(
    val label: String, // "Conservative (8x)", "Base Case (12x)", "Aggressive (18x)"
    val arrMultiple: Double,
    val year3ArrMillions: Double,
    val year3ValuationMillions: Double,
    val year5ValuationMillions: Double
)

data class ValuationReport(
    val ventureName: String,
    val postMoneySeedValuationMillions: Double,
    val seriesATargetValuationMillions: Double,
    val year3ProjectedValuationMillions: Double,
    val year5ProjectedValuationMillions: Double,
    val valuationMethodologiesUsed: List<String>, // ["Forward ARR Multiple (12.0x)", "DCF with 14% WACC", "First Chicago VC Method"]
    val unitEconomics: UnitEconomics,
    val customerRoi: CustomerRoiAnalysis,
    val fiveYearFinancials: List<FinancialYear>,
    val sensitivityScenarios: List<SensitivityScenario>,
    val valuationSummaryNotes: String
)
