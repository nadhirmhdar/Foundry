package com.example.data.model

data class MarketIndexQuote(
    val symbol: String,
    val name: String,
    val value: Double,
    val changePercent: Double,
    val isPositive: Boolean = changePercent >= 0
)

data class SectorEfficiencyBenchmark(
    val sectorName: String,
    val industryMedianOeePercent: Double,
    val frontierBenchmarkOeePercent: Double,
    val costOfQualityLossPercent: Double,
    val orderToCashDays: Double,
    val inventoryCarryingCostPercent: Double,
    val cloudErpAdoptionPercent: Double,
    val medianEvToRevenueMultiple: Double
)

data class ComparativeEfficiencyRatios(
    val selectedBottleneckId: String,
    val sectorName: String,
    val currentLegacyOeePercent: Double,
    val sectorMedianOeePercent: Double,
    val frontierProjectedOeePercent: Double,
    val oeeDeltaPercent: Double, // Gain above sector median
    val annualWasteDeltaMillions: Double,
    val paybackPeriodDays: Int,
    val valuationMultipleDelta: Double, // Frontier Multiple vs Legacy Multiple
    val operationalEfficiencyIndex: Double // Scaled 0 - 100
)

data class RealtimeMarketDataFeed(
    val timestamp: Long,
    val formattedTimestamp: String,
    val marketStatus: String, // e.g. "LIVE API FEED" or "FALLBACK CACHED FEED"
    val latencyMs: Long,
    val indexes: List<MarketIndexQuote>,
    val sectorBenchmarks: List<SectorEfficiencyBenchmark>,
    val globalEnterpriseSoftwareMultiple: Double,
    val medianEnterpriseAcvThousands: Double,
    val ruleOf40MedianPercent: Double
)
