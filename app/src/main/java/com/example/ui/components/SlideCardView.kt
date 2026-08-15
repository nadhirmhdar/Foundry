package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.example.data.model.PitchDeckSlide
import com.example.ui.theme.*

@Composable
fun SlideCardView(
    slide: PitchDeckSlide,
    totalSlides: Int,
    isPresenterNotesVisible: Boolean,
    onTogglePresenterNotes: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Slide Top Bar
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
                        text = "SLIDE ${slide.slideNumber} OF $totalSlides",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SophisticatedLavender,
                            letterSpacing = 1.sp,
                            fontSize = 10.sp
                        )
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = SophisticatedSurfaceVariant,
                    border = BorderStroke(1.dp, SophisticatedBorder)
                ) {
                    IconButton(
                        onClick = onTogglePresenterNotes,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = if (isPresenterNotesVisible) Icons.Default.VisibilityOff else Icons.Default.SpeakerNotes,
                            contentDescription = "Toggle Presenter Notes",
                            tint = if (isPresenterNotesVisible) SophisticatedLavender else SophisticatedTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Slide Title & Subtitle
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = slide.title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = SophisticatedTextPrimary,
                        fontSize = 20.sp
                    )
                )
                if (slide.subtitle.isNotBlank()) {
                    Text(
                        text = slide.subtitle,
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = SophisticatedSecondary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    )
                }
            }

            // Metric Highlight Banner if available
            if (slide.metricHighlight != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SophisticatedCardHighlight
                    ),
                    border = BorderStroke(1.dp, SophisticatedLavender.copy(alpha = 0.25f))
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
                                text = slide.metricLabel ?: "KEY METRIC",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    letterSpacing = 1.sp,
                                    color = SophisticatedSecondary,
                                    fontSize = 10.sp
                                )
                            )
                            Text(
                                text = "INVESTOR HIGHLIGHT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    color = SophisticatedTextMuted
                                )
                            )
                        }
                        Text(
                            text = slide.metricHighlight,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SophisticatedLavenderLight,
                                fontSize = 22.sp
                            )
                        )
                    }
                }
            }

            // Bullet Key Points
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                slide.keyPoints.forEach { point ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 7.dp)
                                .size(6.dp)
                                .background(SophisticatedLavender, CircleShape)
                        )
                        Text(
                            text = point,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = SophisticatedTextPrimary,
                                lineHeight = 20.sp,
                                fontSize = 13.sp
                            )
                        )
                    }
                }
            }

            // Presenter Notes Section
            if (isPresenterNotesVisible) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = SophisticatedSurfaceVariant,
                    border = BorderStroke(1.dp, SophisticatedBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = "Presenter Voice",
                            tint = SophisticatedLavender,
                            modifier = Modifier.size(18.dp)
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "PRESENTER NOTE FOR INVESTOR CALL",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SophisticatedLavender
                                )
                            )
                            Text(
                                text = slide.presenterNotes,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SophisticatedTextPrimary,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
