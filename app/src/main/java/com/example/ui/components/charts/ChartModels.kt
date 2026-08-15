package com.example.ui.components.charts

import androidx.compose.ui.graphics.Color
import com.example.data.model.BottleneckDomain
import com.example.data.model.ErpBottleneck
import com.example.ui.theme.*

data class ChartPoint(
    val xLabel: String,
    val baselineValue: Float,  // e.g. Legacy ERP efficiency %
    val frontierValue: Float,  // e.g. Frontier Logic efficiency %
    val subtext: String = ""
)

data class ComparativeBarData(
    val label: String,
    val category: String,
    val legacyValue: Float,
    val frontierValue: Float,
    val unit: String = "%",
    val deltaPercent: Float = ((frontierValue - legacyValue) / legacyValue.coerceAtLeast(1f)) * 100f,
    val accentColor: Color = SophisticatedLavender
)

data class DonutSliceData(
    val label: String,
    val value: Float,
    val color: Color,
    val subtext: String = ""
)

object ChartDataProvider {

    fun getPerformanceTrendData(bottleneck: ErpBottleneck?): List<ChartPoint> {
        val gain = bottleneck?.potentialEfficiencyGainPercent?.toFloat() ?: 42.5f
        val base = 48f
        return listOf(
            ChartPoint(xLabel = "Q1 '25", baselineValue = base, frontierValue = base + (gain * 0.20f), subtext = "Initial Injection"),
            ChartPoint(xLabel = "Q2 '25", baselineValue = base + 1.2f, frontierValue = base + (gain * 0.42f), subtext = "Shadow Pipeline"),
            ChartPoint(xLabel = "Q3 '25", baselineValue = base + 0.8f, frontierValue = base + (gain * 0.65f), subtext = "API Parity"),
            ChartPoint(xLabel = "Q4 '25", baselineValue = base + 1.8f, frontierValue = base + (gain * 0.82f), subtext = "Production Sync"),
            ChartPoint(xLabel = "Q1 '26", baselineValue = base + 2.1f, frontierValue = base + (gain * 0.94f), subtext = "Full Autonomy"),
            ChartPoint(xLabel = "Q2 '26", baselineValue = base + 2.5f, frontierValue = base + gain, subtext = "Peak Throughput")
        )
    }

    fun getErpSystemBenchmarks(bottlenecks: List<ErpBottleneck>): List<ComparativeBarData> {
        return listOf(
            ComparativeBarData(
                label = "SAP S/4HANA",
                category = "Batch MRP & Scheduling",
                legacyValue = 52f,
                frontierValue = 89f,
                unit = "%",
                accentColor = SophisticatedLavender
            ),
            ComparativeBarData(
                label = "Oracle Cloud ERP",
                category = "Contract & Revenue Rec",
                legacyValue = 46f,
                frontierValue = 91f,
                unit = "%",
                accentColor = SophisticatedSecondary
            ),
            ComparativeBarData(
                label = "Infor CloudSuite",
                category = "MES Real-Time Dispatch",
                legacyValue = 58f,
                frontierValue = 94f,
                unit = "%",
                accentColor = SophisticatedLavenderLight
            ),
            ComparativeBarData(
                label = "Epic EHR / Supply",
                category = "Clinical Instrument Trace",
                legacyValue = 41f,
                frontierValue = 88f,
                unit = "%",
                accentColor = SophisticatedSoftAmber
            ),
            ComparativeBarData(
                label = "Dynamics 365",
                category = "Field Service Routing",
                legacyValue = 49f,
                frontierValue = 86f,
                unit = "%",
                accentColor = SophisticatedSuccessGreen
            )
        )
    }

    fun getDomainWasteDistribution(bottlenecks: List<ErpBottleneck>): List<DonutSliceData> {
        val totalWaste = bottlenecks.sumOf { it.annualIndustryWasteMillions }.toFloat().coerceAtLeast(3200f)
        val erpWaste = bottlenecks.filter { it.domain == BottleneckDomain.ERP_LOGIC }.sumOf { it.annualIndustryWasteMillions }.toFloat().coerceAtLeast(1150f)
        val bpaWaste = bottlenecks.filter { it.domain == BottleneckDomain.BPA_FRICTION }.sumOf { it.annualIndustryWasteMillions }.toFloat().coerceAtLeast(840f)
        val qcWaste = bottlenecks.filter { it.domain == BottleneckDomain.HUMAN_QC_LIMIT }.sumOf { it.annualIndustryWasteMillions }.toFloat().coerceAtLeast(920f)
        val crossWaste = bottlenecks.filter { it.domain == BottleneckDomain.CROSS_INDUSTRY }.sumOf { it.annualIndustryWasteMillions }.toFloat().coerceAtLeast(580f)

        return listOf(
            DonutSliceData(label = "ERP Logic Flaws", value = erpWaste, color = SophisticatedLavender, subtext = "$${erpWaste.toInt()}M/yr"),
            DonutSliceData(label = "BPA Gaps", value = bpaWaste, color = SophisticatedSecondary, subtext = "$${bpaWaste.toInt()}M/yr"),
            DonutSliceData(label = "Human QC Limits", value = qcWaste, color = SophisticatedSoftAmber, subtext = "$${qcWaste.toInt()}M/yr"),
            DonutSliceData(label = "Cross-Industry", value = crossWaste, color = SophisticatedSuccessGreen, subtext = "$${crossWaste.toInt()}M/yr")
        )
    }

    fun getArrGrowthTrend(): List<ChartPoint> {
        return listOf(
            ChartPoint(xLabel = "Year 1", baselineValue = 0.5f, frontierValue = 1.8f, subtext = "$1.8M ARR"),
            ChartPoint(xLabel = "Year 2", baselineValue = 1.2f, frontierValue = 5.4f, subtext = "$5.4M ARR"),
            ChartPoint(xLabel = "Year 3", baselineValue = 2.4f, frontierValue = 14.8f, subtext = "$14.8M ARR"),
            ChartPoint(xLabel = "Year 4", baselineValue = 4.1f, frontierValue = 32.5f, subtext = "$32.5M ARR"),
            ChartPoint(xLabel = "Year 5", baselineValue = 6.8f, frontierValue = 68.0f, subtext = "$68.0M ARR")
        )
    }
}
