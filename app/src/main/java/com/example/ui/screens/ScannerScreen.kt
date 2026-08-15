package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BottleneckDomain
import com.example.data.model.ComparativeEfficiencyRatios
import com.example.data.model.ErpBottleneck
import com.example.data.model.ProblemScope
import com.example.data.model.RealtimeMarketDataFeed
import com.example.ui.components.DomainBadge
import com.example.ui.components.RealtimeMarketBenchmarkCard
import com.example.ui.components.SeverityBadge
import com.example.ui.components.charts.DashboardAnalyticsCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerScreen(
    bottlenecks: List<ErpBottleneck>,
    selectedBottleneck: ErpBottleneck?,
    selectedDomain: BottleneckDomain?,
    searchQuery: String,
    isLiveScanning: Boolean,
    onSelectBottleneck: (ErpBottleneck) -> Unit,
    onDomainFilterChange: (BottleneckDomain?) -> Unit,
    onSearchChange: (String) -> Unit,
    onToggleLiveScanner: () -> Unit,
    onOpenAiDiagnosis: () -> Unit,
    onNavigateToArchitect: () -> Unit,
    marketFeed: RealtimeMarketDataFeed? = null,
    comparativeRatios: ComparativeEfficiencyRatios? = null,
    isMarketFeedRefreshing: Boolean = false,
    onRefreshMarketBenchmarks: () -> Unit = {},
    onOpenPdfPreview: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val filteredBottlenecks = bottlenecks.filter { item ->
        val matchesDomain = selectedDomain == null || item.domain == selectedDomain
        val matchesSearch = searchQuery.isBlank() ||
                item.title.contains(searchQuery, ignoreCase = true) ||
                item.targetIndustry.contains(searchQuery, ignoreCase = true) ||
                item.affectedErpSystems.any { it.contains(searchQuery, ignoreCase = true) } ||
                item.suggestedVentureIdea.name.contains(searchQuery, ignoreCase = true)
        matchesDomain && matchesSearch
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
    ) {
        // Sophisticated Dark Hero Trace Header
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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = SophisticatedSurfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .scale(if (isLiveScanning) pulseScale else 1.0f)
                                        .background(
                                            if (isLiveScanning) SophisticatedLavender else SophisticatedTextMuted,
                                            CircleShape
                                        )
                                )
                                Text(
                                    text = if (isLiveScanning) "ACTIVE SCAN" else "SCANNER PAUSED",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        letterSpacing = 1.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = SophisticatedLavender
                                    )
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "REF: ERP-9042",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    color = SophisticatedTextSecondary
                                )
                            )
                            IconButton(
                                onClick = onToggleLiveScanner,
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(SophisticatedSurfaceVariant, CircleShape)
                            ) {
                                Icon(
                                    imageVector = if (isLiveScanning) Icons.Default.Sensors else Icons.Default.SensorsOff,
                                    contentDescription = "Toggle Scanner",
                                    tint = if (isLiveScanning) SophisticatedLavender else SophisticatedTextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = "Production Efficiency Trace",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Light,
                            color = SophisticatedTextPrimary,
                            fontSize = 20.sp
                        )
                    )

                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "+42.5%",
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 32.sp,
                                color = SophisticatedLavender
                            )
                        )
                        Text(
                            text = "Frontier Logic Available",
                            modifier = Modifier.padding(bottom = 4.dp),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SophisticatedSuccessGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }

                    // Progress indicator bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(100.dp))
                            .background(SophisticatedSurfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(0.72f)
                                .clip(RoundedCornerShape(100.dp))
                                .background(SophisticatedLavender)
                        )
                    }

                    Text(
                        text = "Real-time engine identifying structural flaws in SAP, Oracle, and legacy MES systems—converting untapped frontier algorithms into venture blueprints.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SophisticatedTextSecondary,
                            lineHeight = 18.sp,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }

        // Live Interactive Charting & Telemetry Section
        item {
            DashboardAnalyticsCard(
                bottlenecks = bottlenecks,
                selectedBottleneck = selectedBottleneck
            )
        }

        // Live Market Benchmarks & Comparative Efficiency Ratios
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

        // Sophisticated Deep Plum Highlight Card (Pitch Deck & Valuation CTA)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenAiDiagnosis() },
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SophisticatedCardHighlight
                ),
                border = BorderStroke(1.dp, SophisticatedLavender.copy(alpha = 0.25f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Pitch Deck & Valuation AI",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = SophisticatedLavenderLight
                            )
                        )
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI",
                            tint = SophisticatedLavender,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Text(
                        text = "Automated synthesis of cross-industry ERP flaws into investor-ready documentation and institutional DCF models.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SophisticatedSecondary,
                            lineHeight = 18.sp,
                            fontSize = 12.sp
                        )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onOpenAiDiagnosis,
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp),
                            shape = RoundedCornerShape(100.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SophisticatedLavender,
                                contentColor = SophisticatedLavenderDark
                            )
                        ) {
                            Text(
                                text = "Draft Custom Pitch",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        OutlinedButton(
                            onClick = onOpenAiDiagnosis,
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp),
                            shape = RoundedCornerShape(100.dp),
                            border = BorderStroke(1.dp, SophisticatedBorderSubtle),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = SophisticatedLavender
                            )
                        ) {
                            Text(
                                text = "Run AI Diagnosis",
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

        // Search and Domain Filters
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Search ERPs, industries, bottlenecks...",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = SophisticatedTextMuted,
                                fontSize = 13.sp
                            )
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = SophisticatedTextSecondary
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = SophisticatedTextSecondary
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(100.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SophisticatedSurface,
                        unfocusedContainerColor = SophisticatedSurface,
                        focusedBorderColor = SophisticatedLavender,
                        unfocusedBorderColor = SophisticatedBorder,
                        focusedTextColor = SophisticatedTextPrimary,
                        unfocusedTextColor = SophisticatedTextPrimary
                    )
                )

                // Domain Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedDomain == null,
                        onClick = { onDomainFilterChange(null) },
                        label = {
                            Text(
                                "All Domains (${bottlenecks.size})",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 11.sp
                                )
                            )
                        },
                        shape = RoundedCornerShape(100.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SophisticatedActivePill,
                            selectedLabelColor = SophisticatedLavender,
                            containerColor = SophisticatedSurface,
                            labelColor = SophisticatedTextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedDomain == null,
                            borderColor = SophisticatedBorder,
                            selectedBorderColor = SophisticatedLavender
                        )
                    )

                    BottleneckDomain.values().forEach { domain ->
                        val isDomSelected = selectedDomain == domain
                        FilterChip(
                            selected = isDomSelected,
                            onClick = { onDomainFilterChange(if (isDomSelected) null else domain) },
                            label = {
                                Text(
                                    domain.label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 11.sp
                                    )
                                )
                            },
                            shape = RoundedCornerShape(100.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SophisticatedActivePill,
                                selectedLabelColor = SophisticatedLavender,
                                containerColor = SophisticatedSurface,
                                labelColor = SophisticatedTextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isDomSelected,
                                borderColor = SophisticatedBorder,
                                selectedBorderColor = SophisticatedLavender
                            )
                        )
                    }
                }
            }
        }

        // Bottleneck Stream List
        items(filteredBottlenecks, key = { it.id }) { bottleneck ->
            val isSelected = selectedBottleneck?.id == bottleneck.id
            val venture = bottleneck.suggestedVentureIdea
            var isAuditDrawerExpanded by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onSelectBottleneck(bottleneck)
                    },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) SophisticatedSurfaceVariant else SophisticatedSurface
                ),
                border = BorderStroke(
                    1.dp,
                    if (isSelected) SophisticatedLavender else SophisticatedBorder
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Badges row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            DomainBadge(domain = bottleneck.domain)
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = if (bottleneck.problemScope == ProblemScope.MICRO_FRICTION) SophisticatedEmerald.copy(alpha = 0.15f) else SophisticatedSurfaceVariant,
                                border = BorderStroke(0.5.dp, if (bottleneck.problemScope == ProblemScope.MICRO_FRICTION) SophisticatedEmerald.copy(alpha = 0.5f) else SophisticatedBorder)
                            ) {
                                Text(
                                    text = bottleneck.problemScope.label.uppercase(),
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (bottleneck.problemScope == ProblemScope.MICRO_FRICTION) SophisticatedEmerald else SophisticatedTextSecondary
                                    )
                                )
                            }
                        }
                        SeverityBadge(severity = bottleneck.severity)
                    }

                    // Title and Target Industry
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = bottleneck.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = SophisticatedTextPrimary,
                                fontSize = 16.sp
                            )
                        )
                        Text(
                            text = "Industry: ${bottleneck.targetIndustry}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SophisticatedSecondary,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp
                            )
                        )
                    }

                    // 2-Level Verification Tag
                    bottleneck.verificationSource?.let { source ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SophisticatedDarkBg,
                            border = BorderStroke(0.5.dp, SophisticatedEmerald.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.VerifiedUser,
                                            contentDescription = "Verified",
                                            tint = SophisticatedEmerald,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Text(
                                            text = "2-LEVEL AUDIT VERIFIED (${source.auditConfidenceScore}%)",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = SophisticatedEmerald,
                                                letterSpacing = 0.5.sp
                                            )
                                        )
                                    }

                                    Text(
                                        text = if (isAuditDrawerExpanded) "Hide Proof ▲" else "View Proof ▼",
                                        modifier = Modifier.clickable { isAuditDrawerExpanded = !isAuditDrawerExpanded },
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = SophisticatedLavender
                                        )
                                    )
                                }

                                if (isAuditDrawerExpanded) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "• Primary Endpoint: ${source.primarySystemDoc}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 10.sp,
                                            color = SophisticatedTextSecondary
                                        )
                                    )
                                    Text(
                                        text = "• 2nd-Level Confirmation: ${source.secondaryValidationMethod}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 10.sp,
                                            color = SophisticatedTextMuted
                                        )
                                    )
                                    Text(
                                        text = "• Timestamp: ${source.verificationAuditTimestamp}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 9.sp,
                                            color = SophisticatedTextMuted
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Affected Systems Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Systems:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                color = SophisticatedTextMuted
                            )
                        )
                        bottleneck.affectedErpSystems.forEach { erp ->
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = SophisticatedDarkBg,
                                border = BorderStroke(0.5.dp, SophisticatedBorder)
                            ) {
                                Text(
                                    text = erp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        color = SophisticatedTextSecondary
                                    )
                                )
                            }
                        }
                    }

                    // Traditional Flaw vs Frontier Logic Preview
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SophisticatedDarkBg,
                        border = BorderStroke(1.dp, SophisticatedBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = "FLAW:",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SophisticatedCritical,
                                        fontSize = 10.sp
                                    )
                                )
                                Text(
                                    text = bottleneck.traditionalFlaw,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = SophisticatedTextSecondary,
                                        fontSize = 11.sp,
                                        lineHeight = 16.sp
                                    ),
                                    maxLines = 2
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = "LOGIC:",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SophisticatedLavender,
                                        fontSize = 10.sp
                                    )
                                )
                                Text(
                                    text = bottleneck.frontierLogic,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = SophisticatedTextPrimary,
                                        fontSize = 11.sp,
                                        lineHeight = 16.sp
                                    ),
                                    maxLines = 2
                                )
                            }
                        }
                    }

                    // Suggested Venture Row & CTA
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "VENTURE BLUEPRINT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    letterSpacing = 0.8.sp,
                                    color = SophisticatedTextMuted
                                )
                            )
                            Text(
                                text = venture.name,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = SophisticatedLavenderLight
                                )
                            )
                        }

                        Button(
                            onClick = {
                                onSelectBottleneck(bottleneck)
                                onNavigateToArchitect()
                            },
                            shape = RoundedCornerShape(100.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SophisticatedLavender,
                                contentColor = SophisticatedLavenderDark
                            ),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "View Architecture",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Go",
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }

        // Footer proprietary analysis indicator
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(2.dp)
                        .background(SophisticatedBorderSubtle, RoundedCornerShape(100.dp))
                )
                Text(
                    text = "PROPRIETARY ANALYSIS LAYER 4.0",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        letterSpacing = 2.sp,
                        color = SophisticatedTextMuted,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}
