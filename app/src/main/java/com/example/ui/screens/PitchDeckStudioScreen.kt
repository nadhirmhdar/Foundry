package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ErpBottleneck
import com.example.ui.components.PitchDeckArchitectView
import com.example.ui.components.SlideCardView
import com.example.ui.theme.*

enum class PitchStudioTab(val label: String, val icon: ImageVector) {
    ARCHITECT("Deck Architect (4 Pillars)", Icons.Default.Architecture),
    SLIDES("Slide Deck (10 Cards)", Icons.Default.CoPresent)
}

@Composable
fun PitchDeckStudioScreen(
    bottleneck: ErpBottleneck?,
    currentSlideIndex: Int,
    isPresenterModeOpen: Boolean,
    onSelectSlideIndex: (Int) -> Unit,
    onNextSlide: () -> Unit,
    onPreviousSlide: () -> Unit,
    onTogglePresenterMode: (Boolean) -> Unit,
    onExportMarkdown: () -> Unit,
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
                text = "Select a bottleneck from the Radar Scanner to view its pitch deck.",
                style = MaterialTheme.typography.bodyMedium.copy(color = SophisticatedTextSecondary)
            )
        }
        return
    }

    val venture = bottleneck.suggestedVentureIdea
    val pitchDeck = venture.pitchDeck
    val slides = pitchDeck.slides
    val currentSlide = slides.getOrNull(currentSlideIndex) ?: slides.first()
    var isPresenterNotesVisible by remember { mutableStateOf(false) }
    var selectedStudioTab by remember { mutableStateOf(PitchStudioTab.ARCHITECT) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
    ) {
        // Pitch Deck Header Card
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
                                text = "FOUNDER PITCH STUDIO",
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

                            // Presenter Fullscreen Mode
                            Surface(
                                shape = CircleShape,
                                color = SophisticatedSurfaceVariant,
                                border = BorderStroke(1.dp, SophisticatedBorder)
                            ) {
                                IconButton(
                                    onClick = { onTogglePresenterMode(true) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Fullscreen,
                                        contentDescription = "Fullscreen Pitch",
                                        tint = SophisticatedLavender,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            // Export Report Button
                            Surface(
                                shape = CircleShape,
                                color = SophisticatedSurfaceVariant,
                                border = BorderStroke(1.dp, SophisticatedBorder)
                            ) {
                                IconButton(
                                    onClick = onExportMarkdown,
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
                        text = pitchDeck.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = SophisticatedTextPrimary,
                            fontSize = 20.sp
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
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "TARGET STAGE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        color = SophisticatedTextMuted,
                                        letterSpacing = 0.5.sp
                                    )
                                )
                                Text(
                                    text = pitchDeck.fundingStage,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SophisticatedLavender,
                                        fontSize = 12.sp
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
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "TARGET RAISE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        color = SophisticatedTextMuted,
                                        letterSpacing = 0.5.sp
                                    )
                                )
                                Text(
                                    text = "$${pitchDeck.targetRaiseAmountMillions}M",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SophisticatedLavenderLight,
                                        fontSize = 12.sp
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
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "TOTAL SLIDES",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        color = SophisticatedTextMuted,
                                        letterSpacing = 0.5.sp
                                    )
                                )
                                Text(
                                    text = "${slides.size} Slides",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SophisticatedSecondary,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }

                    // Mode Switcher: Deck Architect vs Slide Presentation
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SophisticatedDarkBg, RoundedCornerShape(100.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PitchStudioTab.values().forEach { tab ->
                            val isSelected = selectedStudioTab == tab
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = if (isSelected) SophisticatedActivePill else Color.Transparent,
                                border = if (isSelected) BorderStroke(1.dp, SophisticatedLavender.copy(alpha = 0.6f)) else null,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedStudioTab = tab }
                                    .testTag("pitch_mode_${tab.name.lowercase()}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = tab.icon,
                                        contentDescription = tab.label,
                                        tint = if (isSelected) SophisticatedLavender else SophisticatedTextSecondary,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = tab.label,
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
        }

        if (selectedStudioTab == PitchStudioTab.ARCHITECT) {
            // Pitch Deck Architect Mode (Problem, Solution, Market Opportunity, Financials)
            item {
                PitchDeckArchitectView(
                    bottleneck = bottleneck,
                    onExportMarkdown = onExportMarkdown
                )
            }
        } else {
            // Slide Deck Carousel Mode
            // Horizontal Slide Scroller
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    slides.forEachIndexed { index, slide ->
                        val isSelected = index == currentSlideIndex
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = if (isSelected) SophisticatedActivePill else SophisticatedSurface,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) SophisticatedLavender else SophisticatedBorder
                            ),
                            modifier = Modifier.clickable { onSelectSlideIndex(index) }
                        ) {
                            Text(
                                text = "Slide ${slide.slideNumber}",
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) SophisticatedLavender else SophisticatedTextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }

            // Main Slide Card
            item {
                SlideCardView(
                    slide = currentSlide,
                    totalSlides = slides.size,
                    isPresenterNotesVisible = isPresenterNotesVisible,
                    onTogglePresenterNotes = { isPresenterNotesVisible = !isPresenterNotesVisible }
                )
            }

            // Slide Navigation Control Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onPreviousSlide,
                        enabled = currentSlideIndex > 0,
                        shape = RoundedCornerShape(100.dp),
                        border = BorderStroke(1.dp, SophisticatedBorderSubtle),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = SophisticatedTextPrimary,
                            disabledContentColor = SophisticatedTextMuted
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Previous Slide",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Previous", fontSize = 11.sp)
                    }

                    // Page Progress Dots
                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        slides.forEachIndexed { index, _ ->
                            Box(
                                modifier = Modifier
                                    .size(if (index == currentSlideIndex) 8.dp else 6.dp)
                                    .background(
                                        if (index == currentSlideIndex) SophisticatedLavender else SophisticatedBorder,
                                        CircleShape
                                    )
                            )
                        }
                    }

                    Button(
                        onClick = onNextSlide,
                        enabled = currentSlideIndex < slides.size - 1,
                        shape = RoundedCornerShape(100.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SophisticatedLavender,
                            contentColor = SophisticatedLavenderDark,
                            disabledContainerColor = SophisticatedSurfaceVariant,
                            disabledContentColor = SophisticatedTextMuted
                        )
                    ) {
                        Text(
                            text = "Next",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Next Slide",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }

    // Fullscreen Investor Presenter Mode Dialog
    if (isPresenterModeOpen) {
        Dialog(
            onDismissRequest = { onTogglePresenterMode(false) },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = SophisticatedDarkBg
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Top presenter bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${pitchDeck.title} - PRESENTER MODE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SophisticatedLavender,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                        IconButton(onClick = { onTogglePresenterMode(false) }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Exit Presenter Mode",
                                tint = SophisticatedTextPrimary
                            )
                        }
                    }

                    // Main Slide Body
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        SlideCardView(
                            slide = currentSlide,
                            totalSlides = slides.size,
                            isPresenterNotesVisible = true,
                            onTogglePresenterNotes = {},
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Bottom navigation controls in Presenter Mode
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = SophisticatedSurface,
                            border = BorderStroke(1.dp, SophisticatedBorder)
                        ) {
                            IconButton(
                                onClick = onPreviousSlide,
                                enabled = currentSlideIndex > 0,
                                modifier = Modifier.size(46.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChevronLeft,
                                    contentDescription = "Prev",
                                    tint = if (currentSlideIndex > 0) SophisticatedLavender else SophisticatedTextMuted,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Text(
                            text = "Slide ${currentSlide.slideNumber} / ${slides.size}",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = SophisticatedTextPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        )

                        Surface(
                            shape = CircleShape,
                            color = if (currentSlideIndex < slides.size - 1) SophisticatedLavender else SophisticatedSurfaceVariant
                        ) {
                            IconButton(
                                onClick = onNextSlide,
                                enabled = currentSlideIndex < slides.size - 1,
                                modifier = Modifier.size(46.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "Next",
                                    tint = if (currentSlideIndex < slides.size - 1) SophisticatedLavenderDark else SophisticatedTextMuted,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

