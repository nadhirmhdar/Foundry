package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ComparativeEfficiencyRatios
import com.example.data.model.ErpBottleneck
import com.example.data.model.MarketIndexQuote
import com.example.data.model.RealtimeMarketDataFeed
import com.example.ui.theme.*

@Composable
fun RealtimeMarketBenchmarkCard(
    marketFeed: RealtimeMarketDataFeed,
    comparativeRatios: ComparativeEfficiencyRatios?,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotate"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("realtime_market_benchmark_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SophisticatedSurface),
        border = BorderStroke(1.dp, SophisticatedLavender.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Bar with Live API Feed status and Refresh
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Pulsing Live Indicator
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                if (isRefreshing) SophisticatedGold else SophisticatedEmerald,
                                CircleShape
                            )
                    )

                    Text(
                        text = "LIVE INDUSTRY BENCHMARKS & MULTIPLES",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SophisticatedLavender,
                            fontSize = 11.sp,
                            letterSpacing = 0.6.sp
                        )
                    )

                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = SophisticatedSurfaceVariant,
                        border = BorderStroke(1.dp, SophisticatedBorderSubtle)
                    ) {
                        Text(
                            text = "${marketFeed.latencyMs}ms • ${marketFeed.formattedTimestamp}",
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                color = SophisticatedTextSecondary
                            )
                        )
                    }
                }

                IconButton(
                    onClick = onRefresh,
                    enabled = !isRefreshing,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh Market Benchmarks",
                        tint = SophisticatedLavender,
                        modifier = Modifier
                            .size(16.dp)
                            .then(if (isRefreshing) Modifier.rotate(rotation) else Modifier)
                    )
                }
            }

            // Real-Time Indexes Ticker Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                marketFeed.indexes.forEach { index ->
                    MarketIndexPill(index = index)
                }
            }

            // Active Comparative Efficiency Ratios (if bottleneck is active)
            if (comparativeRatios != null) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SophisticatedDarkBg,
                    border = BorderStroke(1.dp, SophisticatedBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Comparative Efficiency Ratios // ${comparativeRatios.sectorName}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = SophisticatedTextSecondary,
                                    fontSize = 10.sp
                                ),
                                maxLines = 1
                            )

                            Text(
                                text = if (isExpanded) "Less ▲" else "Deep Ratios ▼",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SophisticatedLavender,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.clickable { isExpanded = !isExpanded }
                            )
                        }

                        // OEE Benchmark Comparison Bar
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Overall Equipment Effectiveness (OEE)",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = SophisticatedTextPrimary,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 11.sp
                                    )
                                )
                                Text(
                                    text = "Sector Med: ${comparativeRatios.sectorMedianOeePercent}%  ➜  Frontier: ${comparativeRatios.frontierProjectedOeePercent}%",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = SophisticatedEmerald,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                )
                            }

                            // Dual-bar progress
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(CircleShape)
                                    .background(SophisticatedSurfaceVariant)
                            ) {
                                // Sector median bar
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(comparativeRatios.sectorMedianOeePercent.toFloat() / 100f)
                                        .fillMaxHeight()
                                        .background(SophisticatedTextMuted)
                                )
                                // Frontier gain bar
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(comparativeRatios.frontierProjectedOeePercent.toFloat() / 100f)
                                        .fillMaxHeight()
                                        .background(SophisticatedEmerald)
                                )
                            }
                        }

                        // 3 Quick Ratio Metrics
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RatioChip(
                                label = "OEE Delta Gain",
                                value = "+${comparativeRatios.oeeDeltaPercent}%",
                                valueColor = SophisticatedEmerald,
                                modifier = Modifier.weight(1f)
                            )
                            RatioChip(
                                label = "Valuation Multiple",
                                value = "+${comparativeRatios.valuationMultipleDelta}x",
                                valueColor = SophisticatedLavender,
                                modifier = Modifier.weight(1f)
                            )
                            RatioChip(
                                label = "Payback Speed",
                                value = "${comparativeRatios.paybackPeriodDays} Days",
                                valueColor = SophisticatedGold,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (isExpanded) {
                            HorizontalDivider(color = SophisticatedBorder, thickness = 1.dp)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                RatioChip(
                                    label = "Annual Loss Displaced",
                                    value = "$${comparativeRatios.annualWasteDeltaMillions}M/yr",
                                    valueColor = SophisticatedRose,
                                    modifier = Modifier.weight(1f)
                                )
                                RatioChip(
                                    label = "Efficiency Composite",
                                    value = "${comparativeRatios.operationalEfficiencyIndex} / 100",
                                    valueColor = SophisticatedEmerald,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MarketIndexPill(index: MarketIndexQuote) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = SophisticatedDarkBg,
        border = BorderStroke(1.dp, SophisticatedBorderSubtle)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column {
                Text(
                    text = index.symbol,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = SophisticatedTextPrimary,
                        fontSize = 11.sp
                    )
                )
                Text(
                    text = "${index.value}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SophisticatedTextSecondary,
                        fontSize = 10.sp
                    )
                )
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (index.isPositive) SophisticatedEmerald.copy(alpha = 0.15f) else SophisticatedRose.copy(alpha = 0.15f),
                border = BorderStroke(
                    1.dp,
                    if (index.isPositive) SophisticatedEmerald.copy(alpha = 0.4f) else SophisticatedRose.copy(alpha = 0.4f)
                )
            ) {
                Text(
                    text = (if (index.isPositive) "+" else "") + "${index.changePercent}%",
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (index.isPositive) SophisticatedEmerald else SophisticatedRose,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun RatioChip(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = SophisticatedSurfaceVariant,
        border = BorderStroke(1.dp, SophisticatedBorderSubtle),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    color = SophisticatedTextSecondary
                ),
                maxLines = 1
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = valueColor
                ),
                maxLines = 1
            )
        }
    }
}
