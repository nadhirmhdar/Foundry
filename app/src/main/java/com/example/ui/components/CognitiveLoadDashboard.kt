package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.*
import com.example.data.repository.CognitiveLoadSynthesizer
import com.example.ui.theme.*

/**
 * Executive Cognitive Load Dashboard Component
 * Formats analysis into a hyper-scannable 10-point briefing that respects human attention limits
 * while providing immediate depth-on-demand.
 */
@Composable
fun CognitiveLoadDashboardCard(
    bottleneck: ErpBottleneck,
    modifier: Modifier = Modifier,
    onOpenFullBriefing: (() -> Unit)? = null,
    onNavigateToArchitect: (() -> Unit)? = null
) {
    val briefing = remember(bottleneck) {
        CognitiveLoadSynthesizer.synthesizeBriefing(bottleneck)
    }
    var selectedFilter by remember { mutableStateOf(CognitiveFilterGroup.ALL) }
    var expandedPointIndex by remember { mutableStateOf<Int?>(null) }
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val filteredPoints = remember(briefing, selectedFilter) {
        when (selectedFilter) {
            CognitiveFilterGroup.ALL -> briefing.points
            CognitiveFilterGroup.TRIAGE_TOP3 -> briefing.points.filter { it.filterGroup == CognitiveFilterGroup.TRIAGE_TOP3 }
            CognitiveFilterGroup.ECONOMICS -> briefing.points.filter { it.filterGroup == CognitiveFilterGroup.ECONOMICS }
            CognitiveFilterGroup.TECH_AUDIT -> briefing.points.filter { it.filterGroup == CognitiveFilterGroup.TECH_AUDIT }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("cognitive_load_dashboard_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SophisticatedSurface),
        border = BorderStroke(1.dp, SophisticatedLavender.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Bar: Cognitive Gauge & Bandwidth Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                Brush.linearGradient(listOf(SophisticatedLavenderDark, SophisticatedSurfaceVariant)),
                                RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "Cognitive Ergonomics",
                            tint = SophisticatedLavender,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "COGNITIVE LOAD DASHBOARD",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = SophisticatedLavender
                                )
                            )
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = SophisticatedEmerald.copy(alpha = 0.15f),
                                border = BorderStroke(0.5.dp, SophisticatedEmerald.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = "LOW LOAD",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = SophisticatedEmerald
                                    )
                                )
                            }
                        }

                        Text(
                            text = "10-Point Concise Briefing • ${briefing.estimatedReadTimeSeconds}s High-Attention Scan",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = SophisticatedTextSecondary
                            )
                        )
                    }
                }

                // Action icons: Copy & Expand
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = {
                            val md = CognitiveLoadSynthesizer.generate10BulletMarkdown(briefing)
                            clipboardManager.setText(AnnotatedString(md))
                            Toast.makeText(context, "Copied 10-Point Briefing to Clipboard", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(32.dp).testTag("copy_10_bullet_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Memo",
                            tint = SophisticatedTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    if (onOpenFullBriefing != null) {
                        IconButton(
                            onClick = onOpenFullBriefing,
                            modifier = Modifier.size(32.dp).testTag("expand_cognitive_dialog_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.OpenInFull,
                                contentDescription = "Open Full Screen",
                                tint = SophisticatedLavender,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Cognitive Bandwidth & Signal Bar
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SophisticatedDarkBg,
                border = BorderStroke(0.5.dp, SophisticatedBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(SophisticatedEmerald)
                        )
                        Text(
                            text = "Signal: ${briefing.signalToNoiseRatioPercent}%",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = SophisticatedEmerald
                            )
                        )
                    }

                    Text(
                        text = "Friction: ${briefing.cognitiveFrictionPercent}% (Zero Jargon)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            color = SophisticatedTextSecondary
                        )
                    )

                    Text(
                        text = "Scan Pace: ~3.5s/pt",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = SophisticatedLavenderLight
                        )
                    )
                }
            }

            // Filter Tabs (Cognitive Chunking)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(CognitiveFilterGroup.values()) { group ->
                    val isSelected = selectedFilter == group
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = if (isSelected) SophisticatedActivePill else SophisticatedSurfaceVariant,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) SophisticatedLavender else SophisticatedBorder
                        ),
                        modifier = Modifier
                            .clickable { selectedFilter = group }
                            .testTag("cognitive_filter_${group.name.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                text = group.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) SophisticatedLavender else SophisticatedTextSecondary
                                )
                            )
                            Surface(
                                shape = CircleShape,
                                color = if (isSelected) SophisticatedLavender else SophisticatedBorderSubtle
                            ) {
                                Text(
                                    text = group.badge,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) SophisticatedLavenderDark else SophisticatedTextPrimary
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // 10 Bullet Point List
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                filteredPoints.forEach { point ->
                    val isExpanded = expandedPointIndex == point.index
                    CognitivePointItem(
                        point = point,
                        isExpanded = isExpanded,
                        onToggleExpand = {
                            expandedPointIndex = if (isExpanded) null else point.index
                        }
                    )
                }
            }

            // Bottom Takeaway
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "10 Invariants Verified • Tap any point for deep-dive telemetry",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 10.sp,
                        color = SophisticatedTextMuted
                    )
                )

                if (onNavigateToArchitect != null) {
                    TextButton(
                        onClick = onNavigateToArchitect,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.testTag("cognitive_to_architect_btn")
                    ) {
                        Text(
                            text = "Architect Venture →",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SophisticatedLavender
                            )
                        )
                    }
                }
            }
        }
    }
}

/**
 * Individual scannable item for each of the 10 points
 */
@Composable
fun CognitivePointItem(
    point: CognitiveLoadPoint,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    val categoryColor = when (point.filterGroup) {
        CognitiveFilterGroup.TRIAGE_TOP3 -> SophisticatedRose
        CognitiveFilterGroup.ECONOMICS -> SophisticatedGold
        CognitiveFilterGroup.TECH_AUDIT -> SophisticatedEmerald
        CognitiveFilterGroup.ALL -> SophisticatedLavender
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (isExpanded) SophisticatedCardHighlight else SophisticatedDarkBg,
        border = BorderStroke(
            0.5.dp,
            if (isExpanded) categoryColor.copy(alpha = 0.5f) else SophisticatedBorderSubtle
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleExpand() }
            .testTag("cognitive_point_${point.index}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // Index Number Pill
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = SophisticatedSurfaceVariant,
                        border = BorderStroke(0.5.dp, SophisticatedBorder)
                    ) {
                        Text(
                            text = String.format("%02d", point.index),
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = SophisticatedLavender
                            )
                        )
                    }

                    // Category Tag
                    Text(
                        text = point.categoryTag,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            color = categoryColor
                        )
                    )
                }

                // Quantified Metric Badge
                point.quantifiedMetricBadge?.let { badge ->
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = categoryColor.copy(alpha = 0.12f),
                        border = BorderStroke(0.5.dp, categoryColor.copy(alpha = 0.35f))
                    ) {
                        Text(
                            text = badge,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = categoryColor
                            )
                        )
                    }
                }
            }

            // Bold One-Liner Lead (Under 8 words)
            Text(
                text = point.oneLinerLead,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.5.sp,
                    color = SophisticatedTextPrimary,
                    lineHeight = 16.sp
                )
            )

            // 1-Sentence Executive Synthesis
            Text(
                text = point.executiveSummary,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    color = SophisticatedTextSecondary,
                    lineHeight = 15.sp
                )
            )

            // Progressive Disclosure: Depth Drawer on Demand
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) + fadeIn(),
                exit = shrinkVertically(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                        .background(SophisticatedSurface, RoundedCornerShape(10.dp))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "DEPTH-ON-DEMAND TELEMETRY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            color = SophisticatedLavender
                        )
                    )

                    point.depthKeyValues.forEach { (key, value) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = key,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = SophisticatedTextMuted
                                ),
                                modifier = Modifier.weight(0.42f)
                            )
                            Text(
                                text = value,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = SophisticatedTextPrimary
                                ),
                                modifier = Modifier.weight(0.58f)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Full-Screen / Modal Dialog for the 10-Point Cognitive Load Briefing
 */
@Composable
fun CognitiveLoadBriefingDialog(
    bottleneck: ErpBottleneck,
    onDismiss: () -> Unit,
    onNavigateToArchitect: (() -> Unit)? = null
) {
    val briefing = remember(bottleneck) {
        CognitiveLoadSynthesizer.synthesizeBriefing(bottleneck)
    }
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var selectedFilter by remember { mutableStateOf(CognitiveFilterGroup.ALL) }
    var expandedPointIndex by remember { mutableStateOf<Int?>(null) }

    val filteredPoints = remember(briefing, selectedFilter) {
        when (selectedFilter) {
            CognitiveFilterGroup.ALL -> briefing.points
            CognitiveFilterGroup.TRIAGE_TOP3 -> briefing.points.filter { it.filterGroup == CognitiveFilterGroup.TRIAGE_TOP3 }
            CognitiveFilterGroup.ECONOMICS -> briefing.points.filter { it.filterGroup == CognitiveFilterGroup.ECONOMICS }
            CognitiveFilterGroup.TECH_AUDIT -> briefing.points.filter { it.filterGroup == CognitiveFilterGroup.TECH_AUDIT }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            shape = RoundedCornerShape(28.dp),
            color = SophisticatedDarkBg,
            border = BorderStroke(1.dp, SophisticatedLavender.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Modal Top Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(36.dp)
                                .background(SophisticatedSurface, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = SophisticatedTextPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "10-Point Cognitive Briefing",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SophisticatedTextPrimary,
                                    fontSize = 16.sp
                                )
                            )
                            Text(
                                text = "${briefing.ventureName} • ${briefing.industryDomain}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SophisticatedTextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    // Share & Copy
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        IconButton(
                            onClick = {
                                val md = CognitiveLoadSynthesizer.generate10BulletMarkdown(briefing)
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, md)
                                    putExtra(Intent.EXTRA_TITLE, "10-Point Executive Briefing: ${briefing.ventureName}")
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share 10-Point Briefing"))
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .background(SophisticatedSurface, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = SophisticatedLavender,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                val md = CognitiveLoadSynthesizer.generate10BulletMarkdown(briefing)
                                clipboardManager.setText(AnnotatedString(md))
                                Toast.makeText(context, "Copied Briefing to Clipboard", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .background(SophisticatedSurface, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                                tint = SophisticatedTextPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bandwidth summary badge
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SophisticatedSurface,
                    border = BorderStroke(0.5.dp, SophisticatedBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("SCAN TIME", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = SophisticatedTextMuted))
                            Text("${briefing.estimatedReadTimeSeconds}s", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = SophisticatedEmerald))
                        }
                        Divider(modifier = Modifier.height(24.dp).width(1.dp), color = SophisticatedBorder)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("SIGNAL RATIO", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = SophisticatedTextMuted))
                            Text("${briefing.signalToNoiseRatioPercent}%", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = SophisticatedLavender))
                        }
                        Divider(modifier = Modifier.height(24.dp).width(1.dp), color = SophisticatedBorder)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("COGNITIVE LOAD", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = SophisticatedTextMuted))
                            Text("MINIMAL", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = SophisticatedGold))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Filter tabs
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(CognitiveFilterGroup.values()) { group ->
                        val isSelected = selectedFilter == group
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedFilter = group },
                            label = { Text("${group.label} (${group.badge})", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SophisticatedActivePill,
                                selectedLabelColor = SophisticatedLavender
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable 10-Point List
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(filteredPoints, key = { it.index }) { point ->
                        val isExpanded = expandedPointIndex == point.index
                        CognitivePointItem(
                            point = point,
                            isExpanded = isExpanded,
                            onToggleExpand = {
                                expandedPointIndex = if (isExpanded) null else point.index
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // CTA Button
                Button(
                    onClick = {
                        onDismiss()
                        onNavigateToArchitect?.invoke()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SophisticatedLavender)
                ) {
                    Text(
                        text = "Open Full Architecture & Financials",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SophisticatedLavenderDark
                        )
                    )
                }
            }
        }
    }
}
