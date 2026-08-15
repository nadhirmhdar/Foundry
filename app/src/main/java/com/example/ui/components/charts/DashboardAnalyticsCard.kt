package com.example.ui.components.charts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ErpBottleneck
import com.example.ui.theme.*

enum class DashboardChartTab(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    TRENDS("Efficiency Delta", Icons.Default.TrendingUp),
    SYSTEM_BENCHMARKS("ERP Comparison", Icons.Default.BarChart),
    WASTE_BREAKDOWN("Waste Impact", Icons.Default.PieChart)
}

@Composable
fun DashboardAnalyticsCard(
    bottlenecks: List<ErpBottleneck>,
    selectedBottleneck: ErpBottleneck?,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(DashboardChartTab.TRENDS) }

    val trendPoints = remember(selectedBottleneck) {
        ChartDataProvider.getPerformanceTrendData(selectedBottleneck)
    }
    val comparativeBenchmarks = remember(bottlenecks) {
        ChartDataProvider.getErpSystemBenchmarks(bottlenecks)
    }
    val wasteSlices = remember(bottlenecks) {
        ChartDataProvider.getDomainWasteDistribution(bottlenecks)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = SophisticatedSurface),
        border = BorderStroke(1.dp, SophisticatedBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = SophisticatedSurfaceVariant,
                        border = BorderStroke(1.dp, SophisticatedBorder)
                    ) {
                        Box(modifier = Modifier.size(32.dp), contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Analytics,
                                contentDescription = "Analytics",
                                tint = SophisticatedLavender,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "ERP BENCHMARK & PERFORMANCE INTEL",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SophisticatedLavender,
                                letterSpacing = 0.8.sp,
                                fontSize = 10.sp
                            )
                        )
                        Text(
                            text = if (selectedBottleneck != null) selectedBottleneck.suggestedVentureIdea.name else "Cross-ERP Frontier Telemetry",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = SophisticatedTextPrimary,
                                fontSize = 15.sp
                            )
                        )
                    }
                }
            }

            // Segmented Tab Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SophisticatedDarkBg, RoundedCornerShape(100.dp))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                DashboardChartTab.values().forEach { tab ->
                    val isSelected = activeTab == tab
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = if (isSelected) SophisticatedActivePill else Color.Transparent,
                        border = if (isSelected) BorderStroke(1.dp, SophisticatedLavender.copy(alpha = 0.6f)) else null,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { activeTab = tab }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 7.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label,
                                tint = if (isSelected) SophisticatedLavender else SophisticatedTextSecondary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = tab.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) SophisticatedLavender else SophisticatedTextSecondary,
                                    fontSize = 10.sp
                                ),
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // Chart Canvas Content depending on selected tab
            when (activeTab) {
                DashboardChartTab.TRENDS -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Quarterly Throughput Ramp (Legacy vs Frontier)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SophisticatedTextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                            Text(
                                text = "Scrub line to inspect",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SophisticatedTextMuted,
                                    fontSize = 10.sp
                                )
                            )
                        }

                        ProcessPerformanceTrendChart(
                            points = trendPoints,
                            unit = "%",
                            showLegend = true
                        )
                    }
                }

                DashboardChartTab.SYSTEM_BENCHMARKS -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Legacy Enterprise ERP vs Frontier Process Efficiency Gain",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SophisticatedTextSecondary,
                                fontSize = 11.sp
                            )
                        )

                        ErpComparativeBarChart(
                            items = comparativeBenchmarks
                        )
                    }
                }

                DashboardChartTab.WASTE_BREAKDOWN -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Annual Enterprise Loss by Inefficiency Domain",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SophisticatedTextSecondary,
                                fontSize = 11.sp
                            )
                        )

                        DomainWasteDonutChart(
                            slices = wasteSlices,
                            centerValue = "$3.8B",
                            centerLabel = "Annual Industry Waste"
                        )
                    }
                }
            }
        }
    }
}
