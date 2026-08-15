package com.example.ui.components.charts

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.math.roundToInt

@Composable
fun ErpComparativeBarChart(
    items: List<ComparativeBarData>,
    modifier: Modifier = Modifier,
    onItemClick: ((ComparativeBarData) -> Unit)? = null
) {
    if (items.isEmpty()) return

    val progress = remember { Animatable(0f) }
    var selectedItem by remember { mutableStateOf<ComparativeBarData?>(null) }

    LaunchedEffect(items) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Comparative items list
        items.forEach { data ->
            val isSelected = selectedItem == data
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isSelected) SophisticatedDarkBg else Color.Transparent)
                    .clickable {
                        selectedItem = if (isSelected) null else data
                        onItemClick?.invoke(data)
                    }
                    .padding(horizontal = 6.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Label & delta badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = data.label,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = SophisticatedTextPrimary,
                                fontSize = 13.sp
                            )
                        )
                        Text(
                            text = data.category,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SophisticatedTextSecondary,
                                fontSize = 10.sp
                            )
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = SophisticatedSuccessGreen.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, SophisticatedSuccessGreen.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "+${data.deltaPercent.roundToInt()}% Delta",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SophisticatedSuccessGreen,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                // Dual comparison bars: Legacy vs Frontier
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    // Frontier logic bar (top, glowing)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Frontier",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                color = SophisticatedLavender,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.width(44.dp)
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(12.dp)
                                .clip(RoundedCornerShape(100.dp))
                                .background(SophisticatedSurfaceVariant)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth((data.frontierValue / 100f) * progress.value)
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(SophisticatedSecondary, data.accentColor, SophisticatedLavenderLight)
                                        )
                                    )
                            )
                        }

                        Text(
                            text = "${data.frontierValue.roundToInt()}${data.unit}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SophisticatedLavender
                            ),
                            modifier = Modifier.width(36.dp)
                        )
                    }

                    // Legacy baseline bar (bottom, muted)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Legacy",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                color = SophisticatedTextMuted
                            ),
                            modifier = Modifier.width(44.dp)
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(8.dp)
                                .clip(RoundedCornerShape(100.dp))
                                .background(SophisticatedSurfaceVariant)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth((data.legacyValue / 100f) * progress.value)
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(SophisticatedTextMuted.copy(alpha = 0.6f))
                            )
                        }

                        Text(
                            text = "${data.legacyValue.roundToInt()}${data.unit}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                color = SophisticatedTextSecondary
                            ),
                            modifier = Modifier.width(36.dp)
                        )
                    }
                }
            }
        }
    }
}
