package com.example.ui.components.charts

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.math.atan2
import kotlin.math.roundToInt

@Composable
fun DomainWasteDonutChart(
    slices: List<DonutSliceData>,
    modifier: Modifier = Modifier,
    centerLabel: String = "TOTAL ANNUAL WASTE",
    centerValue: String = "$3.8B"
) {
    if (slices.isEmpty()) return

    val total = remember(slices) { slices.sumOf { it.value.toDouble() }.toFloat().coerceAtLeast(1f) }
    val progress = remember { Animatable(0f) }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(slices) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing)
        )
    }

    val selectedSlice = selectedIndex?.let { slices.getOrNull(it) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Donut Canvas with Central Highlight
        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(slices) {
                        detectTapGestures { offset ->
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val dx = offset.x - center.x
                            val dy = offset.y - center.y
                            val dist = kotlin.math.sqrt(dx * dx + dy * dy)
                            val innerR = (size.width / 2f) * 0.55f
                            val outerR = (size.width / 2f) * 0.95f

                            if (dist in innerR..outerR) {
                                var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                                if (angle < 0) angle += 360f
                                // Align with start angle of -90 deg
                                angle = (angle + 90f) % 360f

                                var currentSweep = 0f
                                var hitIndex: Int? = null
                                for (i in slices.indices) {
                                    val sweep = (slices[i].value / total) * 360f
                                    if (angle in currentSweep..(currentSweep + sweep)) {
                                        hitIndex = i
                                        break
                                    }
                                    currentSweep += sweep
                                }
                                selectedIndex = if (selectedIndex == hitIndex) null else hitIndex
                            } else {
                                selectedIndex = null
                            }
                        }
                    }
            ) {
                val strokeWidth = 24.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2f
                val topLeft = Offset(
                    (size.width - radius * 2) / 2f,
                    (size.height - radius * 2) / 2f
                )
                val arcSize = Size(radius * 2, radius * 2)

                var startAngle = -90f
                val animProg = progress.value

                slices.forEachIndexed { index, slice ->
                    val isSelected = selectedIndex == index
                    val sweepAngle = (slice.value / total) * 360f * animProg
                    val currentStroke = if (isSelected) strokeWidth * 1.3f else strokeWidth

                    drawArc(
                        color = slice.color,
                        startAngle = startAngle + 1.5f,
                        sweepAngle = (sweepAngle - 3f).coerceAtLeast(0.1f),
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = currentStroke, cap = StrokeCap.Round)
                    )
                    startAngle += sweepAngle
                }
            }

            // Center Text inside Donut
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (selectedSlice != null) {
                    Text(
                        text = selectedSlice.subtext,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = selectedSlice.color,
                            fontSize = 17.sp
                        )
                    )
                    Text(
                        text = "${((selectedSlice.value / total) * 100).roundToInt()}% of Total",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SophisticatedTextSecondary,
                            fontSize = 10.sp
                        )
                    )
                } else {
                    Text(
                        text = centerValue,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = SophisticatedLavender,
                            fontSize = 20.sp
                        )
                    )
                    Text(
                        text = "Global Inefficiency",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SophisticatedTextMuted,
                            fontSize = 9.sp,
                            letterSpacing = 0.5.sp
                        )
                    )
                }
            }
        }

        // Legend Grid
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            slices.chunked(2).forEach { rowSlices ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowSlices.forEach { slice ->
                        val index = slices.indexOf(slice)
                        val isSelected = selectedIndex == index
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) SophisticatedSurfaceVariant else SophisticatedDarkBg,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) slice.color else SophisticatedBorder
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    selectedIndex = if (isSelected) null else index
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(slice.color, CircleShape)
                                )
                                Column {
                                    Text(
                                        text = slice.label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = SophisticatedTextPrimary,
                                            fontSize = 11.sp
                                        ),
                                        maxLines = 1
                                    )
                                    Text(
                                        text = slice.subtext,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = SophisticatedTextSecondary,
                                            fontSize = 10.sp
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
}
