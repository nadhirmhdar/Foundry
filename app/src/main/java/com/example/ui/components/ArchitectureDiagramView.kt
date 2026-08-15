package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ArchitectureStep
import com.example.ui.theme.*

@Composable
fun ArchitectureDiagramView(
    steps: List<ArchitectureStep>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        steps.forEachIndexed { index, step ->
            val stepColor = when (index % 3) {
                0 -> SophisticatedLavender
                1 -> SophisticatedSecondary
                else -> SophisticatedTertiary
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SophisticatedSurface
                ),
                border = BorderStroke(1.dp, SophisticatedBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    // Step Number Avatar
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(SophisticatedSurfaceVariant, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${step.stepNumber}",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                color = stepColor
                            )
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = step.layerName,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                                    color = SophisticatedTextPrimary,
                                    fontSize = 15.sp
                                )
                            )
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = SophisticatedSurfaceVariant,
                                border = BorderStroke(1.dp, SophisticatedBorder)
                            ) {
                                Text(
                                    text = step.techStack,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                                        color = stepColor
                                    )
                                )
                            }
                        }

                        Text(
                            text = step.description,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SophisticatedTextSecondary,
                                lineHeight = 18.sp,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }

            // Downward arrow connector if not last
            if (index < steps.size - 1) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = "Flow connection",
                        tint = SophisticatedBorderSubtle,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
