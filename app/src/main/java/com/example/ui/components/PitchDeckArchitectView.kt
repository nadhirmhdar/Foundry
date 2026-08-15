package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.*
import com.example.data.repository.PitchDeckArchitectSynthesizer
import com.example.ui.theme.*

@Composable
fun PitchDeckArchitectView(
    bottleneck: ErpBottleneck,
    onExportMarkdown: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedPerspective by remember { mutableStateOf(PitchPerspective.VENTURE_CAPITAL) }
    var expandedPillar by remember { mutableStateOf<PitchArchitectPillar?>(null) } // null means all expanded or interactive
    var editingSection by remember { mutableStateOf<PitchDeckArchitectSection?>(null) }
    var customNotesMap by remember { mutableStateOf<Map<PitchArchitectPillar, String>>(emptyMap()) }

    val blueprint = remember(bottleneck, selectedPerspective) {
        PitchDeckArchitectSynthesizer.synthesizeBlueprint(bottleneck, selectedPerspective)
    }

    val copyToClipboard: (String, String) -> Unit = { label, text ->
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Copied $label to clipboard", Toast.LENGTH_SHORT).show()
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Perspective Mode Switcher Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SophisticatedSurface),
            border = BorderStroke(1.dp, SophisticatedBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
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
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = "Perspective",
                                    tint = SophisticatedLavender,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "SYNTHESIS PERSPECTIVE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SophisticatedLavender,
                                    letterSpacing = 0.8.sp,
                                    fontSize = 10.sp
                                )
                            )
                            Text(
                                text = selectedPerspective.label,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = SophisticatedTextPrimary,
                                    fontSize = 14.sp
                                )
                            )
                        }
                    }

                    // Copy Full Blueprint Button
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = SophisticatedSurfaceVariant,
                        border = BorderStroke(1.dp, SophisticatedBorder),
                        modifier = Modifier.clickable {
                            copyToClipboard("Full Pitch Deck Blueprint", blueprint.toFormattedMarkdown())
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Blueprint",
                                tint = SophisticatedLavender,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Copy Deck",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SophisticatedLavender,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                Text(
                    text = selectedPerspective.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SophisticatedTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                )

                // Perspective Selector Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SophisticatedDarkBg, RoundedCornerShape(100.dp))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    PitchPerspective.values().forEach { perspective ->
                        val isSelected = selectedPerspective == perspective
                        val shortLabel = when (perspective) {
                            PitchPerspective.VENTURE_CAPITAL -> "VC Pitch"
                            PitchPerspective.ENTERPRISE_BUYER -> "Buyer ROI"
                            PitchPerspective.EXECUTIVE_BOARD -> "Board Exec"
                        }
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = if (isSelected) SophisticatedActivePill else Color.Transparent,
                            border = if (isSelected) BorderStroke(1.dp, SophisticatedLavender.copy(alpha = 0.6f)) else null,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedPerspective = perspective }
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 7.dp, horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = shortLabel,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) SophisticatedLavender else SophisticatedTextSecondary,
                                        fontSize = 11.sp
                                    ),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        // The 4 Core Structured Sections
        blueprint.allSections.forEach { section ->
            val isCustomNotePresent = customNotesMap.containsKey(section.pillar)
            val customNote = customNotesMap[section.pillar]

            ArchitectPillarSectionCard(
                section = section,
                customNote = customNote,
                onCopySection = {
                    val sectionText = buildString {
                        appendLine("${section.pillar.title}: ${section.headline}")
                        appendLine(section.subheadline)
                        appendLine()
                        appendLine("EXECUTIVE SYNTHESIS:")
                        appendLine(section.executiveSummary)
                        appendLine()
                        appendLine("STRUCTURED EVIDENCE:")
                        section.items.forEach {
                            appendLine("• ${it.title} (${it.metricValue ?: ""}): ${it.narrative}")
                        }
                        appendLine()
                        appendLine("TAKEAWAY: ${section.strategicTakeaway}")
                        if (!customNote.isNullOrBlank()) {
                            appendLine()
                            appendLine("FOUNDER NOTE: $customNote")
                        }
                    }
                    copyToClipboard(section.pillar.title, sectionText)
                },
                onEditSection = {
                    editingSection = section
                }
            )
        }
    }

    // Founder Note Customization Dialog
    if (editingSection != null) {
        val targetSection = editingSection!!
        var noteInput by remember { mutableStateOf(customNotesMap[targetSection.pillar] ?: "") }

        Dialog(onDismissRequest = { editingSection = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SophisticatedSurface),
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
                        Text(
                            text = "Add Founder Annotation",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SophisticatedTextPrimary,
                                fontSize = 16.sp
                            )
                        )
                        IconButton(
                            onClick = { editingSection = null },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = SophisticatedTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Text(
                        text = "Customize or append specific client quotes, pilot data, or tactical proof points to ${targetSection.pillar.title}:",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SophisticatedTextSecondary,
                            fontSize = 12.sp
                        )
                    )

                    OutlinedTextField(
                        value = noteInput,
                        onValueChange = { noteInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = "e.g. In initial discovery calls with Fortune 500 VP of Supply Chain, validation time was confirmed to be 4 days instead of standard 48 hours...",
                                style = MaterialTheme.typography.bodySmall.copy(color = SophisticatedTextMuted, fontSize = 12.sp)
                            )
                        },
                        minLines = 3,
                        maxLines = 6,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SophisticatedLavender,
                            unfocusedBorderColor = SophisticatedBorder,
                            focusedTextColor = SophisticatedTextPrimary,
                            unfocusedTextColor = SophisticatedTextPrimary,
                            cursorColor = SophisticatedLavender,
                            focusedContainerColor = SophisticatedDarkBg,
                            unfocusedContainerColor = SophisticatedDarkBg
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                customNotesMap = customNotesMap - targetSection.pillar
                                editingSection = null
                            },
                            shape = RoundedCornerShape(100.dp),
                            border = BorderStroke(1.dp, SophisticatedBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Clear Note", color = SophisticatedTextSecondary, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                if (noteInput.isNotBlank()) {
                                    customNotesMap = customNotesMap + (targetSection.pillar to noteInput.trim())
                                } else {
                                    customNotesMap = customNotesMap - targetSection.pillar
                                }
                                editingSection = null
                            },
                            shape = RoundedCornerShape(100.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SophisticatedLavender,
                                contentColor = SophisticatedLavenderDark
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Save Note", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ArchitectPillarSectionCard(
    section: PitchDeckArchitectSection,
    customNote: String?,
    onCopySection: () -> Unit,
    onEditSection: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (pillarColor, pillarIcon) = when (section.pillar) {
        PitchArchitectPillar.PROBLEM -> SophisticatedCritical to Icons.Default.WarningAmber
        PitchArchitectPillar.SOLUTION -> SophisticatedLavender to Icons.Default.AutoAwesome
        PitchArchitectPillar.MARKET_OPPORTUNITY -> SophisticatedSecondary to Icons.Default.TrendingUp
        PitchArchitectPillar.FINANCIALS -> SophisticatedSuccessGreen to Icons.Default.MonetizationOn
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
            // Header with Pillar Tag and Copy/Edit actions
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
                        color = pillarColor.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, pillarColor.copy(alpha = 0.35f))
                    ) {
                        Box(modifier = Modifier.size(32.dp), contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = pillarIcon,
                                contentDescription = section.pillar.title,
                                tint = pillarColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Text(
                        text = section.pillar.title.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = pillarColor,
                            letterSpacing = 0.8.sp,
                            fontSize = 11.sp
                        )
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Edit/Annotate Button
                    Surface(
                        shape = CircleShape,
                        color = SophisticatedSurfaceVariant,
                        border = BorderStroke(1.dp, SophisticatedBorder)
                    ) {
                        IconButton(onClick = onEditSection, modifier = Modifier.size(30.dp)) {
                            Icon(
                                imageVector = Icons.Default.EditNote,
                                contentDescription = "Annotate",
                                tint = SophisticatedTextSecondary,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    // Copy Section Button
                    Surface(
                        shape = CircleShape,
                        color = SophisticatedSurfaceVariant,
                        border = BorderStroke(1.dp, SophisticatedBorder)
                    ) {
                        IconButton(onClick = onCopySection, modifier = Modifier.size(30.dp)) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Section",
                                tint = SophisticatedLavender,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            // Headline & Subheadline
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = section.headline,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = SophisticatedTextPrimary,
                        fontSize = 16.sp
                    )
                )
                Text(
                    text = section.subheadline,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = SophisticatedTextSecondary,
                        fontSize = 11.sp
                    )
                )
            }

            // Executive Summary Callout Box
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SophisticatedDarkBg,
                border = BorderStroke(1.dp, SophisticatedBorder)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height(56.dp)
                            .background(pillarColor, RoundedCornerShape(2.dp))
                    )
                    Text(
                        text = section.executiveSummary,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SophisticatedTextPrimary,
                            fontSize = 12.sp,
                            lineHeight = 19.sp
                        )
                    )
                }
            }

            // Structured Evidence Grid Items
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                section.items.forEach { item ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SophisticatedSurfaceVariant,
                        border = BorderStroke(1.dp, SophisticatedBorder)
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
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    if (item.tag != null) {
                                        Surface(
                                            shape = RoundedCornerShape(100.dp),
                                            color = SophisticatedDarkBg,
                                            border = BorderStroke(0.5.dp, SophisticatedBorder)
                                        ) {
                                            Text(
                                                text = item.tag,
                                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = SophisticatedLavender,
                                                    letterSpacing = 0.5.sp
                                                )
                                            )
                                        }
                                    }
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = SophisticatedTextPrimary,
                                            fontSize = 12.sp
                                        )
                                    )
                                }

                                if (item.metricValue != null) {
                                    Surface(
                                        shape = RoundedCornerShape(100.dp),
                                        color = pillarColor.copy(alpha = 0.14f),
                                        border = BorderStroke(1.dp, pillarColor.copy(alpha = 0.3f))
                                    ) {
                                        Text(
                                            text = item.metricValue,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = pillarColor,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                }
                            }

                            Text(
                                text = item.narrative,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SophisticatedTextSecondary,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                )
                            )
                        }
                    }
                }
            }

            // Custom Founder Note (if present)
            if (!customNote.isNullOrBlank()) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SophisticatedSoftAmber.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, SophisticatedSoftAmber.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.StickyNote2,
                            contentDescription = "Founder Note",
                            tint = SophisticatedSoftAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "FOUNDER ANNOTATION",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SophisticatedSoftAmber,
                                    fontSize = 9.sp,
                                    letterSpacing = 0.5.sp
                                )
                            )
                            Text(
                                text = customNote,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SophisticatedTextPrimary,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                )
                            )
                        }
                    }
                }
            }

            // Strategic Takeaway Footer
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SophisticatedDarkBg,
                border = BorderStroke(1.dp, SophisticatedBorderSubtle)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircleOutline,
                        contentDescription = "Takeaway",
                        tint = SophisticatedSuccessGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = section.strategicTakeaway,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SophisticatedTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}
