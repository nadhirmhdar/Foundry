package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ComparativeEfficiencyRatios
import com.example.data.model.ErpBottleneck
import com.example.data.model.RealtimeMarketDataFeed
import com.example.ui.components.MetricCard
import com.example.ui.components.RealtimeMarketBenchmarkCard
import com.example.ui.components.SensitivityMatrixTable
import com.example.ui.components.charts.ChartPoint
import com.example.ui.components.charts.ProcessPerformanceTrendChart
import com.example.ui.theme.*
import com.example.ui.viewmodel.ValuationCalculatorState

@Composable
fun ValuationReportScreen(
    bottleneck: ErpBottleneck?,
    calculatorState: ValuationCalculatorState,
    onUpdateAcv: (Double) -> Unit,
    onUpdateCac: (Double) -> Unit,
    onUpdateYear3Arr: (Double) -> Unit,
    onUpdateMultiple: (Double) -> Unit,
    onUpdateRaise: (Double) -> Unit,
    onExportReport: () -> Unit,
    marketFeed: RealtimeMarketDataFeed? = null,
    comparativeRatios: ComparativeEfficiencyRatios? = null,
    isMarketFeedRefreshing: Boolean = false,
    onRefreshMarketBenchmarks: () -> Unit = {},
    onOpenPdfPreview: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (bottleneck == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Select a bottleneck from the Radar Scanner to view its valuation report.",
                style = MaterialTheme.typography.bodyMedium.copy(color = SophisticatedTextSecondary)
            )
        }
        return
    }

    val venture = bottleneck.suggestedVentureIdea
    val valReport = venture.valuationReport
    val unitEcon = valReport.unitEconomics
    val customerRoi = valReport.customerRoi

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
    ) {
        // Valuation Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SophisticatedSurface
                ),
                border = BorderStroke(1.dp, SophisticatedBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = SophisticatedSurfaceVariant,
                            border = BorderStroke(1.dp, SophisticatedBorder)
                        ) {
                            Text(
                                text = "VENTURE VALUATION REPORT",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SophisticatedLavender,
                                    letterSpacing = 0.8.sp,
                                    fontSize = 10.sp
                                )
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // PDF Memo Preview Button
                            Surface(
                                shape = CircleShape,
                                color = SophisticatedSurfaceVariant,
                                border = BorderStroke(1.dp, SophisticatedBorder)
                            ) {
                                IconButton(
                                    onClick = onOpenPdfPreview,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PictureAsPdf,
                                        contentDescription = "PDF Deal Memo",
                                        tint = SophisticatedLavender,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            // Share Markdown Report Button
                            Surface(
                                shape = CircleShape,
                                color = SophisticatedSurfaceVariant,
                                border = BorderStroke(1.dp, SophisticatedBorder)
                            ) {
                                IconButton(
                                    onClick = onExportReport,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Export Report",
                                        tint = SophisticatedSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    Text(
                        text = "${venture.name} Financial & Valuation Model",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = SophisticatedTextPrimary,
                            fontSize = 20.sp
                        )
                    )

                    Text(
                        text = "Synthesized using Forward ARR Multiples, Discounted Cash Flow (DCF), and Risk-Adjusted Venture Capital Methods based on verified B2B enterprise SaaS benchmarks.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SophisticatedTextSecondary,
                            lineHeight = 18.sp,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }

        // Live Real-Time Market Multiples & Efficiency Benchmarks
        if (marketFeed != null) {
            item {
                RealtimeMarketBenchmarkCard(
                    marketFeed = marketFeed,
                    comparativeRatios = comparativeRatios,
                    isRefreshing = isMarketFeedRefreshing,
                    onRefresh = onRefreshMarketBenchmarks
                )
            }
        }

        // Valuation Key Highlights Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    label = "Seed Post-Money",
                    value = "$${valReport.postMoneySeedValuationMillions.toInt()}M",
                    subtext = "Initial Funding Cap",
                    highlightColor = SophisticatedLavender,
                    accentBadge = "Seed",
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    label = "Series A Target",
                    value = "$${valReport.seriesATargetValuationMillions.toInt()}M",
                    subtext = "18-Month Target",
                    highlightColor = SophisticatedLavenderLight,
                    accentBadge = "Series A",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    label = "Year 3 Projected",
                    value = "$${valReport.year3ProjectedValuationMillions.toInt()}M",
                    subtext = "12.0x Forward ARR",
                    highlightColor = SophisticatedSecondary,
                    accentBadge = "Y3 Value",
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    label = "Year 5 Projected",
                    value = "$${valReport.year5ProjectedValuationMillions.toInt()}M",
                    subtext = "Enterprise Scale",
                    highlightColor = SophisticatedSuccessGreen,
                    accentBadge = "Y5 Scale",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Unit Economics Breakdown Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SophisticatedSurface
                ),
                border = BorderStroke(1.dp, SophisticatedBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "STARTUP UNIT ECONOMICS (B2B SAAS)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold,
                            color = SophisticatedTextSecondary,
                            fontSize = 10.sp
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = SophisticatedDarkBg,
                            border = BorderStroke(1.dp, SophisticatedBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "TARGET ACV",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        color = SophisticatedTextMuted,
                                        letterSpacing = 0.5.sp
                                    )
                                )
                                Text(
                                    text = "$${unitEcon.targetEnterpriseAcvThousands.toInt()}k",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = SophisticatedLavender,
                                        fontSize = 16.sp
                                    )
                                )
                                Text(
                                    text = "Annual contract",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 10.sp,
                                        color = SophisticatedTextSecondary
                                    )
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = SophisticatedDarkBg,
                            border = BorderStroke(1.dp, SophisticatedBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "LTV / CAC RATIO",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        color = SophisticatedTextMuted,
                                        letterSpacing = 0.5.sp
                                    )
                                )
                                Text(
                                    text = "${(unitEcon.ltvToCacRatio * 10).toInt() / 10.0}x",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = SophisticatedSecondary,
                                        fontSize = 16.sp
                                    )
                                )
                                Text(
                                    text = "Benchmark: > 3.0x",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 10.sp,
                                        color = SophisticatedTextSecondary
                                    )
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = SophisticatedDarkBg,
                            border = BorderStroke(1.dp, SophisticatedBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "CAC PAYBACK",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        color = SophisticatedTextMuted,
                                        letterSpacing = 0.5.sp
                                    )
                                )
                                Text(
                                    text = "${unitEcon.paybackPeriodMonths} Mo",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = SophisticatedSuccessGreen,
                                        fontSize = 16.sp
                                    )
                                )
                                Text(
                                    text = "Benchmark: < 12m",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 10.sp,
                                        color = SophisticatedTextSecondary
                                    )
                                )
                            }
                        }
                    }

                    // Secondary metrics row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Estimated CAC",
                                style = MaterialTheme.typography.bodySmall.copy(color = SophisticatedTextSecondary, fontSize = 11.sp)
                            )
                            Text(
                                text = "$${unitEcon.customerAcquisitionCostThousands.toInt()}k / client",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = SophisticatedTextPrimary
                                )
                            )
                        }

                        Column {
                            Text(
                                text = "Customer Lifetime Value (LTV)",
                                style = MaterialTheme.typography.bodySmall.copy(color = SophisticatedTextSecondary, fontSize = 11.sp)
                            )
                            Text(
                                text = "$${unitEcon.ltvThousands.toInt()}k",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = SophisticatedTextPrimary
                                )
                            )
                        }

                        Column {
                            Text(
                                text = "Net Revenue Retention",
                                style = MaterialTheme.typography.bodySmall.copy(color = SophisticatedTextSecondary, fontSize = 11.sp)
                            )
                            Text(
                                text = "${unitEcon.netRevenueRetentionPercent}%",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = SophisticatedLavender
                                )
                            )
                        }
                    }
                }
            }
        }

        // Customer ROI Economics (Enterprise Value Proposition)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SophisticatedSurface
                ),
                border = BorderStroke(1.dp, SophisticatedBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "ENTERPRISE BUYER ROI CASE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold,
                            color = SophisticatedTextSecondary,
                            fontSize = 10.sp
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = SophisticatedDarkBg,
                            border = BorderStroke(1.dp, SophisticatedBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "CLIENT SAVINGS",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        color = SophisticatedTextMuted,
                                        letterSpacing = 0.5.sp
                                    )
                                )
                                Text(
                                    text = "$${customerRoi.annualClientCostSavingsMillions}M/yr",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = SophisticatedCritical,
                                        fontSize = 16.sp
                                    )
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = SophisticatedDarkBg,
                            border = BorderStroke(1.dp, SophisticatedBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "ENTERPRISE ROI",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        color = SophisticatedTextMuted,
                                        letterSpacing = 0.5.sp
                                    )
                                )
                                Text(
                                    text = "${(customerRoi.enterpriseRoiMultiple * 10).toInt() / 10.0}x ROI",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = SophisticatedLavender,
                                        fontSize = 16.sp
                                    )
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = SophisticatedDarkBg,
                            border = BorderStroke(1.dp, SophisticatedBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "BUYER PAYBACK",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        color = SophisticatedTextMuted,
                                        letterSpacing = 0.5.sp
                                    )
                                )
                                Text(
                                    text = "< ${customerRoi.paybackDays} Days",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = SophisticatedSecondary,
                                        fontSize = 16.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5-Year Financial & ARR Projections Table
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SophisticatedSurface
                ),
                border = BorderStroke(1.dp, SophisticatedBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "5-YEAR VENTURE FINANCIAL TRAJECTORY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold,
                            color = SophisticatedTextSecondary,
                            fontSize = 10.sp
                        )
                    )

                    // Header Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SophisticatedDarkBg, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Period", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = SophisticatedTextMuted, fontSize = 10.sp))
                        Text("Clients", modifier = Modifier.weight(0.8f), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = SophisticatedTextMuted, fontSize = 10.sp))
                        Text("ARR", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = SophisticatedTextMuted, fontSize = 10.sp))
                        Text("Margin", modifier = Modifier.weight(0.9f), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = SophisticatedTextMuted, fontSize = 10.sp))
                        Text("EBITDA", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = SophisticatedTextMuted, fontSize = 10.sp))
                    }

                    valReport.fiveYearFinancials.forEachIndexed { index, year ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(year.yearLabel, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, color = SophisticatedTextPrimary, fontSize = 11.sp))
                            Text("${year.customersCount}", modifier = Modifier.weight(0.8f), style = MaterialTheme.typography.bodySmall.copy(color = SophisticatedTextSecondary, fontSize = 11.sp))
                            Text("$${year.arrMillions}M", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = SophisticatedLavender, fontSize = 11.sp))
                            Text("${year.grossMarginPercent.toInt()}%", modifier = Modifier.weight(0.9f), style = MaterialTheme.typography.bodySmall.copy(color = SophisticatedTextSecondary, fontSize = 11.sp))
                            Text(
                                text = if (year.netBurnOrProfitMillions >= 0) "+$${year.netBurnOrProfitMillions}M" else "-$${kotlin.math.abs(year.netBurnOrProfitMillions)}M",
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (year.netBurnOrProfitMillions >= 0) SophisticatedSuccessGreen else SophisticatedCritical,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        if (index < valReport.fiveYearFinancials.size - 1) {
                            HorizontalDivider(thickness = 0.5.dp, color = SophisticatedBorder)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    val arrChartPoints = valReport.fiveYearFinancials.map { year ->
                        ChartPoint(
                            xLabel = year.yearLabel,
                            baselineValue = (year.arrMillions * 0.3f).toFloat(),
                            frontierValue = year.arrMillions.toFloat(),
                            subtext = "$${year.arrMillions}M ARR"
                        )
                    }

                    ProcessPerformanceTrendChart(
                        points = arrChartPoints,
                        unit = "M",
                        title = "5-Year ARR Trajectory (\$M)",
                        showLegend = false
                    )
                }
            }
        }

        // Sensitivity Matrix Scenarios
        item {
            SensitivityMatrixTable(scenarios = valReport.sensitivityScenarios)
        }

        // Interactive Valuation Sandbox / Calculator
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SophisticatedSurface
                ),
                border = BorderStroke(1.dp, SophisticatedLavender.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "INTERACTIVE VALUATION SANDBOX",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.Bold,
                                color = SophisticatedLavender,
                                fontSize = 10.sp
                            )
                        )
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Sandbox",
                            tint = SophisticatedLavender,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Text(
                        text = "Tweak key SaaS parameters to dynamically simulate post-money valuation, LTV/CAC ratios, and venture returns:",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SophisticatedTextSecondary,
                            fontSize = 12.sp
                        )
                    )

                    // Target ACV Slider
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Target ACV", style = MaterialTheme.typography.bodySmall.copy(color = SophisticatedTextPrimary, fontSize = 12.sp))
                            Text("$${calculatorState.customAcvThousands.toInt()}k / yr", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = SophisticatedLavender, fontSize = 12.sp))
                        }
                        Slider(
                            value = calculatorState.customAcvThousands.toFloat(),
                            onValueChange = { onUpdateAcv(it.toDouble()) },
                            valueRange = 50f..500f,
                            colors = SliderDefaults.colors(
                                thumbColor = SophisticatedLavender,
                                activeTrackColor = SophisticatedLavender,
                                inactiveTrackColor = SophisticatedSurfaceVariant
                            )
                        )
                    }

                    // Exit ARR Multiple Slider
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Exit ARR Multiple", style = MaterialTheme.typography.bodySmall.copy(color = SophisticatedTextPrimary, fontSize = 12.sp))
                            Text("${(calculatorState.customExitMultiple * 10).toInt() / 10.0}x Forward ARR", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = SophisticatedSecondary, fontSize = 12.sp))
                        }
                        Slider(
                            value = calculatorState.customExitMultiple.toFloat(),
                            onValueChange = { onUpdateMultiple(it.toDouble()) },
                            valueRange = 6f..25f,
                            colors = SliderDefaults.colors(
                                thumbColor = SophisticatedSecondary,
                                activeTrackColor = SophisticatedSecondary,
                                inactiveTrackColor = SophisticatedSurfaceVariant
                            )
                        )
                    }

                    // Target Raise Slider
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Seed Capital Raise", style = MaterialTheme.typography.bodySmall.copy(color = SophisticatedTextPrimary, fontSize = 12.sp))
                            Text("$${(calculatorState.customTargetRaiseMillions * 10).toInt() / 10.0}M Raise", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = SophisticatedLavenderLight, fontSize = 12.sp))
                        }
                        Slider(
                            value = calculatorState.customTargetRaiseMillions.toFloat(),
                            onValueChange = { onUpdateRaise(it.toDouble()) },
                            valueRange = 1.0f..10.0f,
                            colors = SliderDefaults.colors(
                                thumbColor = SophisticatedLavenderLight,
                                activeTrackColor = SophisticatedLavenderLight,
                                inactiveTrackColor = SophisticatedSurfaceVariant
                            )
                        )
                    }

                    // Live Calculated Results Card
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = SophisticatedDarkBg,
                        border = BorderStroke(1.dp, SophisticatedBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "DYNAMIC SEED POST-MONEY",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        color = SophisticatedTextMuted,
                                        letterSpacing = 0.5.sp
                                    )
                                )
                                Text(
                                    text = "$${calculatorState.computedPostMoneySeedValuationMillions.toInt()}M",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SophisticatedLavender,
                                        fontSize = 22.sp
                                    )
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "DYNAMIC Y3 VALUATION",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        color = SophisticatedTextMuted,
                                        letterSpacing = 0.5.sp
                                    )
                                )
                                Text(
                                    text = "$${calculatorState.computedYear3ValuationMillions.toInt()}M",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SophisticatedSecondary,
                                        fontSize = 22.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
