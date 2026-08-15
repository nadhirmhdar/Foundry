package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ErpBottleneck
import com.example.ui.components.ArchitectureDiagramView
import com.example.ui.components.DomainBadge
import com.example.ui.components.MetricCard
import com.example.ui.components.SeverityBadge
import com.example.ui.components.charts.ChartDataProvider
import com.example.ui.components.charts.ProcessPerformanceTrendChart
import com.example.ui.theme.*

@Composable
fun VentureArchitectScreen(
    bottleneck: ErpBottleneck?,
    isSaved: Boolean,
    onToggleSave: () -> Unit,
    onNavigateToPitchDeck: () -> Unit,
    onNavigateToValuation: () -> Unit,
    onCopyMarkdown: () -> Unit,
    onOpenAiDiagnosis: () -> Unit,
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Empty",
                    tint = SophisticatedTextMuted,
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = "No Bottleneck Selected",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = SophisticatedTextPrimary
                    )
                )
                Text(
                    text = "Select an ERP or process bottleneck from the Radar Scanner to view its venture architecture.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SophisticatedTextSecondary
                    )
                )
            }
        }
        return
    }

    val venture = bottleneck.suggestedVentureIdea
    val valReport = venture.valuationReport

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
    ) {
        // Venture Hero Header
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
                        .padding(22.dp),
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
                                text = venture.category.uppercase(),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SophisticatedLavender,
                                    letterSpacing = 0.8.sp,
                                    fontSize = 10.sp
                                )
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // PDF Deal Memo Preview Button
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

                            // Save to Vault Button
                            Surface(
                                shape = CircleShape,
                                color = SophisticatedSurfaceVariant,
                                border = BorderStroke(1.dp, SophisticatedBorder)
                            ) {
                                IconButton(
                                    onClick = onToggleSave,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkAdd,
                                        contentDescription = "Save to Vault",
                                        tint = if (isSaved) SophisticatedLavender else SophisticatedTextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = venture.name,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = SophisticatedTextPrimary,
                                fontSize = 24.sp
                            )
                        )
                        Text(
                            text = venture.tagline,
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = SophisticatedSecondary,
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SophisticatedDarkBg,
                        border = BorderStroke(1.dp, SophisticatedBorder)
                    ) {
                        Text(
                            text = "“${venture.oneSentencePitch}”",
                            modifier = Modifier.padding(14.dp),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = SophisticatedTextPrimary,
                                lineHeight = 20.sp,
                                fontSize = 13.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DomainBadge(domain = bottleneck.domain)
                        SeverityBadge(severity = bottleneck.severity)
                    }
                }
            }
        }

        // Key Value Metrics Strip
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    label = "Annual Loss",
                    value = "$${bottleneck.annualIndustryWasteMillions.toInt()}M",
                    subtext = "Industry Inefficiency",
                    highlightColor = SophisticatedCritical,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    label = "Efficiency Delta",
                    value = "+${bottleneck.potentialEfficiencyGainPercent}%",
                    subtext = "Throughput Unlocked",
                    highlightColor = SophisticatedLavender,
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
                    label = "Seed Raise",
                    value = "$${venture.pitchDeck.targetRaiseAmountMillions}M",
                    subtext = "Target Financing",
                    highlightColor = SophisticatedLavenderLight,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    label = "Seed Valuation",
                    value = "$${valReport.postMoneySeedValuationMillions.toInt()}M",
                    subtext = "Post-Money Model",
                    highlightColor = SophisticatedSecondary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Logic Contrast: Traditional Method vs Frontier Breakthrough
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
                        text = "DEEP LOGIC & PROCESS CONTRAST",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold,
                            color = SophisticatedTextSecondary,
                            fontSize = 10.sp
                        )
                    )

                    // Traditional Legacy Flaw
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SophisticatedDarkBg,
                        border = BorderStroke(1.dp, SophisticatedCritical.copy(alpha = 0.35f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .background(SophisticatedCritical, CircleShape)
                                )
                                Text(
                                    text = "TRADITIONAL ERP / PROCESS FLAW",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SophisticatedCritical,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                            Text(
                                text = bottleneck.traditionalFlaw,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SophisticatedTextPrimary,
                                    lineHeight = 18.sp,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }

                    // Frontier Unused Logic
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SophisticatedDarkBg,
                        border = BorderStroke(1.dp, SophisticatedLavender.copy(alpha = 0.35f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .background(SophisticatedLavender, CircleShape)
                                )
                                Text(
                                    text = "UNUSED FRONTIER LOGIC BREAKTHROUGH",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SophisticatedLavender,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                            Text(
                                text = bottleneck.frontierLogic,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SophisticatedTextPrimary,
                                    lineHeight = 18.sp,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }

                    // Adoption Friction & Bypass Strategy
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SophisticatedDarkBg,
                        border = BorderStroke(1.dp, SophisticatedSecondary.copy(alpha = 0.35f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .background(SophisticatedSecondary, CircleShape)
                                )
                                Text(
                                    text = "ZERO-FRICTION ADOPTION WEDGE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SophisticatedSecondary,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                            Text(
                                text = venture.frictionBypassStrategy,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SophisticatedTextPrimary,
                                    lineHeight = 18.sp,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // Comparative Process Performance Chart
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = SophisticatedSurface),
                border = BorderStroke(1.dp, SophisticatedBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "PROCESS EFFICIENCY GAIN TRAJECTORY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold,
                            color = SophisticatedTextSecondary,
                            fontSize = 10.sp
                        )
                    )

                    val trendPoints = remember(bottleneck) {
                        ChartDataProvider.getPerformanceTrendData(bottleneck)
                    }

                    ProcessPerformanceTrendChart(
                        points = trendPoints,
                        unit = "%",
                        title = "Quarterly Process Throughput Uplift"
                    )
                }
            }
        }

        // Product Architecture Flow
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
                        text = "3-TIER PRODUCT ARCHITECTURE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold,
                            color = SophisticatedTextSecondary,
                            fontSize = 10.sp
                        )
                    )

                    ArchitectureDiagramView(steps = venture.architectureSteps)
                }
            }
        }

        // Defensible Moat & Target Market
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
                        text = "DEFENSIBILITY & GO-TO-MARKET ICP",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold,
                            color = SophisticatedTextSecondary,
                            fontSize = 10.sp
                        )
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Proprietary Technical Moat",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = SophisticatedLavenderLight,
                                fontSize = 14.sp
                            )
                        )
                        Text(
                            text = venture.coreMoat,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SophisticatedTextSecondary,
                                lineHeight = 18.sp,
                                fontSize = 12.sp
                            )
                        )
                    }

                    HorizontalDivider(thickness = 0.5.dp, color = SophisticatedBorder)

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Target Buyer Persona (ICP)",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = SophisticatedSecondary,
                                fontSize = 14.sp
                            )
                        )
                        Text(
                            text = venture.targetIcp,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SophisticatedTextSecondary,
                                lineHeight = 18.sp,
                                fontSize = 12.sp
                            )
                        )
                    }

                    HorizontalDivider(thickness = 0.5.dp, color = SophisticatedBorder)

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Beachhead Market Segment",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = SophisticatedLavender,
                                fontSize = 14.sp
                            )
                        )
                        Text(
                            text = venture.beachheadMarket,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SophisticatedTextSecondary,
                                lineHeight = 18.sp,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }

        // Action Buttons Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onNavigateToPitchDeck,
                    modifier = Modifier.weight(1f).height(46.dp),
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SophisticatedLavender,
                        contentColor = SophisticatedLavenderDark
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.CoPresent,
                        contentDescription = "Pitch Deck",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Pitch Deck",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }

                OutlinedButton(
                    onClick = onNavigateToValuation,
                    modifier = Modifier.weight(1f).height(46.dp),
                    shape = RoundedCornerShape(100.dp),
                    border = BorderStroke(1.dp, SophisticatedBorderSubtle),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = SophisticatedLavender
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Valuation",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Valuation Model",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}
