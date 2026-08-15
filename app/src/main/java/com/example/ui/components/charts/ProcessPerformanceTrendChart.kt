package com.example.ui.components.charts

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.math.roundToInt

@Composable
fun ProcessPerformanceTrendChart(
    points: List<ChartPoint>,
    modifier: Modifier = Modifier,
    unit: String = "%",
    title: String = "Process Throughput & Efficiency Gain",
    showLegend: Boolean = true
) {
    if (points.isEmpty()) return

    val animationProgress = remember { Animatable(0f) }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(points) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing)
        )
    }

    val maxVal = remember(points) {
        points.maxOfOrNull { maxOf(it.frontierValue, it.baselineValue) }?.let { it * 1.15f } ?: 100f
    }
    val minVal = remember(points) {
        points.minOfOrNull { minOf(it.frontierValue, it.baselineValue) }?.let { (it * 0.85f).coerceAtLeast(0f) } ?: 0f
    }

    val selectedPoint = selectedIndex?.let { points.getOrNull(it) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Scrubber Tooltip Header
        if (selectedPoint != null) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SophisticatedDarkBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, SophisticatedLavender.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${selectedPoint.xLabel} (${selectedPoint.subtext})",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SophisticatedLavender,
                                fontSize = 11.sp
                            )
                        )
                        Text(
                            text = "Legacy: ${selectedPoint.baselineValue.roundToInt()}$unit  →  Frontier: ${selectedPoint.frontierValue.roundToInt()}$unit",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SophisticatedTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }

                    val delta = selectedPoint.frontierValue - selectedPoint.baselineValue
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = SophisticatedSuccessGreen.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "+${delta.roundToInt()}$unit Gain",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SophisticatedSuccessGreen,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        // Canvas Line & Gradient Area Chart
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            val chartWidth = constraints.maxWidth.toFloat()
            val chartHeight = constraints.maxHeight.toFloat()
            val paddingLeft = 40.dp.value * 2.5f
            val paddingRight = 16.dp.value * 2.5f
            val paddingTop = 16.dp.value * 2.5f
            val paddingBottom = 24.dp.value * 2.5f

            val drawWidth = chartWidth - paddingLeft - paddingRight
            val drawHeight = chartHeight - paddingTop - paddingBottom

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(points) {
                        detectTapGestures { offset ->
                            val relativeX = offset.x - paddingLeft
                            if (relativeX >= 0 && relativeX <= drawWidth) {
                                val stepX = drawWidth / (points.size - 1).coerceAtLeast(1)
                                val index = (relativeX / stepX).roundToInt().coerceIn(0, points.size - 1)
                                selectedIndex = if (selectedIndex == index) null else index
                            }
                        }
                    }
                    .pointerInput(points) {
                        detectDragGestures(
                            onDrag = { change, _ ->
                                val relativeX = change.position.x - paddingLeft
                                if (relativeX >= 0 && relativeX <= drawWidth) {
                                    val stepX = drawWidth / (points.size - 1).coerceAtLeast(1)
                                    val index = (relativeX / stepX).roundToInt().coerceIn(0, points.size - 1)
                                    selectedIndex = index
                                }
                            },
                            onDragEnd = {
                                // Keep selected or retain state
                            }
                        )
                    }
            ) {
                // Background Horizontal Gridlines
                val gridSteps = 4
                for (i in 0..gridSteps) {
                    val yPos = paddingTop + (drawHeight / gridSteps) * i
                    drawLine(
                        color = SophisticatedBorder.copy(alpha = 0.4f),
                        start = Offset(paddingLeft, yPos),
                        end = Offset(chartWidth - paddingRight, yPos),
                        strokeWidth = 1f
                    )
                }

                val stepX = drawWidth / (points.size - 1).coerceAtLeast(1)
                val progress = animationProgress.value

                // Compute point coordinates
                val baselinePoints = points.mapIndexed { index, p ->
                    val x = paddingLeft + (index * stepX)
                    val normalizedY = (p.baselineValue - minVal) / (maxVal - minVal).coerceAtLeast(1f)
                    val y = paddingTop + drawHeight - (normalizedY * drawHeight * progress)
                    Offset(x, y)
                }

                val frontierPoints = points.mapIndexed { index, p ->
                    val x = paddingLeft + (index * stepX)
                    val normalizedY = (p.frontierValue - minVal) / (maxVal - minVal).coerceAtLeast(1f)
                    val y = paddingTop + drawHeight - (normalizedY * drawHeight * progress)
                    Offset(x, y)
                }

                // 1. Draw Frontier Gradient Fill
                if (frontierPoints.isNotEmpty()) {
                    val fillPath = Path().apply {
                        moveTo(frontierPoints.first().x, paddingTop + drawHeight)
                        frontierPoints.forEachIndexed { i, pt ->
                            if (i == 0) {
                                lineTo(pt.x, pt.y)
                            } else {
                                val prev = frontierPoints[i - 1]
                                val cx = (prev.x + pt.x) / 2f
                                cubicTo(cx, prev.y, cx, pt.y, pt.x, pt.y)
                            }
                        }
                        lineTo(frontierPoints.last().x, paddingTop + drawHeight)
                        close()
                    }

                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                SophisticatedLavender.copy(alpha = 0.35f),
                                SophisticatedLavender.copy(alpha = 0.05f),
                                Color.Transparent
                            ),
                            startY = paddingTop,
                            endY = paddingTop + drawHeight
                        )
                    )
                }

                // 2. Draw Legacy Baseline Line (Dashed Style)
                if (baselinePoints.isNotEmpty()) {
                    val baselinePath = Path().apply {
                        baselinePoints.forEachIndexed { i, pt ->
                            if (i == 0) moveTo(pt.x, pt.y) else lineTo(pt.x, pt.y)
                        }
                    }
                    drawPath(
                        path = baselinePath,
                        color = SophisticatedTextMuted.copy(alpha = 0.7f),
                        style = Stroke(
                            width = 2.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        )
                    )
                }

                // 3. Draw Frontier Smooth Curve Line
                if (frontierPoints.isNotEmpty()) {
                    val frontierPath = Path().apply {
                        frontierPoints.forEachIndexed { i, pt ->
                            if (i == 0) {
                                moveTo(pt.x, pt.y)
                            } else {
                                val prev = frontierPoints[i - 1]
                                val cx = (prev.x + pt.x) / 2f
                                cubicTo(cx, prev.y, cx, pt.y, pt.x, pt.y)
                            }
                        }
                    }

                    drawPath(
                        path = frontierPath,
                        brush = Brush.horizontalGradient(
                            colors = listOf(SophisticatedSecondary, SophisticatedLavender, SophisticatedLavenderLight)
                        ),
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                }

                // 4. Draw Data Node Dots
                frontierPoints.forEachIndexed { index, pt ->
                    val isSelected = selectedIndex == index
                    drawCircle(
                        color = if (isSelected) SophisticatedLavenderLight else SophisticatedLavender,
                        radius = if (isSelected) 6.dp.toPx() else 4.dp.toPx(),
                        center = pt
                    )
                    drawCircle(
                        color = SophisticatedDarkBg,
                        radius = if (isSelected) 3.dp.toPx() else 2.dp.toPx(),
                        center = pt
                    )
                }

                // 5. Draw Selected Scrubber Vertical Indicator Line
                selectedIndex?.let { index ->
                    val pt = frontierPoints.getOrNull(index)
                    if (pt != null) {
                        drawLine(
                            color = SophisticatedLavender.copy(alpha = 0.7f),
                            start = Offset(pt.x, paddingTop),
                            end = Offset(pt.x, paddingTop + drawHeight),
                            strokeWidth = 1.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                        )
                    }
                }
            }
        }

        // X-Axis Labels Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 28.dp, end = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            points.forEachIndexed { index, p ->
                val isSelected = selectedIndex == index
                Text(
                    text = p.xLabel,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) SophisticatedLavender else SophisticatedTextSecondary,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }
        }

        // Bottom Legend
        if (showLegend) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp, 3.dp)
                            .background(SophisticatedLavender, RoundedCornerShape(2.dp))
                    )
                    Text(
                        text = "Frontier Engine",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SophisticatedLavender
                        )
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp, 2.dp)
                            .background(SophisticatedTextMuted)
                    )
                    Text(
                        text = "Legacy ERP Baseline",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            color = SophisticatedTextSecondary
                        )
                    )
                }
            }
        }
    }
}
