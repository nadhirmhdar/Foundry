package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SensitivityScenario
import com.example.ui.theme.*

@Composable
fun SensitivityMatrixTable(
    scenarios: List<SensitivityScenario>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = SophisticatedSurface
        ),
        border = BorderStroke(1.dp, SophisticatedBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "VALUATION SENSITIVITY MATRIX",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold,
                    color = SophisticatedTextSecondary,
                    fontSize = 10.sp
                )
            )

            // Table Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        SophisticatedSurfaceVariant,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Multiple Scenario",
                    modifier = Modifier.weight(1.3f),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = SophisticatedTextSecondary,
                        fontSize = 10.sp
                    )
                )
                Text(
                    text = "Y3 ARR",
                    modifier = Modifier.weight(0.9f),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = SophisticatedTextSecondary,
                        fontSize = 10.sp
                    )
                )
                Text(
                    text = "Y3 Valuation",
                    modifier = Modifier.weight(1.1f),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = SophisticatedTextSecondary,
                        fontSize = 10.sp
                    )
                )
                Text(
                    text = "Y5 Valuation",
                    modifier = Modifier.weight(1.1f),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = SophisticatedTextSecondary,
                        fontSize = 10.sp
                    )
                )
            }

            // Table Rows
            scenarios.forEachIndexed { index, sc ->
                val highlightColor = when (index) {
                    0 -> SophisticatedTextPrimary
                    1 -> SophisticatedLavender
                    else -> SophisticatedSecondary
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = sc.label,
                        modifier = Modifier.weight(1.3f),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = if (index == 1) FontWeight.Bold else FontWeight.Normal,
                            color = highlightColor,
                            fontSize = 11.sp
                        )
                    )
                    Text(
                        text = "$${sc.year3ArrMillions}M",
                        modifier = Modifier.weight(0.9f),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SophisticatedTextSecondary,
                            fontSize = 11.sp
                        )
                    )
                    Text(
                        text = "$${sc.year3ValuationMillions.toInt()}M",
                        modifier = Modifier.weight(1.1f),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = highlightColor,
                            fontSize = 11.sp
                        )
                    )
                    Text(
                        text = "$${sc.year5ValuationMillions.toInt()}M",
                        modifier = Modifier.weight(1.1f),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = highlightColor,
                            fontSize = 11.sp
                        )
                    )
                }

                if (index < scenarios.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 6.dp),
                        thickness = 0.5.dp,
                        color = SophisticatedBorder
                    )
                }
            }
        }
    }
}
