package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*

@Composable
fun AiCustomAnalysisDialog(
    isOpen: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onSubmitPrompt: (String) -> Unit
) {
    if (!isOpen) return

    var promptInput by remember { mutableStateOf("") }

    val examplePrompts = listOf(
        "Semiconductor wafer fabrication defect classification in Infor MES",
        "Cold-chain food logistics perishability expiry in SAP S/4HANA",
        "Battery gigafactory cathode slurry mixing batch inconsistency",
        "Aerospace composite autoclave curing temperature drift",
        "Pharmaceutical sterile fill-finish regulatory batch sign-off delay"
    )

    Dialog(onDismissRequest = { if (!isLoading) onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(38.dp),
                            shape = CircleShape,
                            color = SophisticatedSurfaceVariant,
                            border = BorderStroke(1.dp, SophisticatedBorder)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "AI Scanner",
                                    tint = SophisticatedLavender,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Gemini Deep Diagnostic",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = SophisticatedTextPrimary,
                                    fontSize = 16.sp
                                )
                            )
                            Text(
                                text = "Frontier Logic & Pitch Deck Synthesizer",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SophisticatedTextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    if (!isLoading) {
                        Surface(
                            shape = CircleShape,
                            color = SophisticatedSurfaceVariant,
                            border = BorderStroke(1.dp, SophisticatedBorder)
                        ) {
                            IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = SophisticatedTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "Describe an industry workflow, ERP bottleneck, or quality control limit to architect a venture-scale startup solution, pitch deck, and valuation report:",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SophisticatedTextSecondary,
                        lineHeight = 18.sp,
                        fontSize = 12.sp
                    )
                )

                // Input field
                OutlinedTextField(
                    value = promptInput,
                    onValueChange = { promptInput = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "e.g. Pharmaceutical batch record reconciliation delays in SAP S/4HANA...",
                            style = MaterialTheme.typography.bodySmall.copy(color = SophisticatedTextMuted, fontSize = 12.sp)
                        )
                    },
                    minLines = 3,
                    maxLines = 5,
                    enabled = !isLoading,
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

                // Example prompt suggestions
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "SUGGESTED ENTERPRISE SCENARIOS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SophisticatedTextMuted,
                            letterSpacing = 0.5.sp
                        )
                    )
                    examplePrompts.take(3).forEach { example ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SophisticatedDarkBg,
                            border = BorderStroke(1.dp, SophisticatedBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = !isLoading) {
                                    promptInput = example
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = "Scenario",
                                    tint = SophisticatedSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = example,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.sp,
                                        color = SophisticatedTextSecondary
                                    )
                                )
                            }
                        }
                    }
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SophisticatedCritical,
                            fontSize = 12.sp
                        )
                    )
                }

                // Submit button
                Button(
                    onClick = { onSubmitPrompt(promptInput) },
                    enabled = promptInput.isNotBlank() && !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SophisticatedLavender,
                        contentColor = SophisticatedLavenderDark,
                        disabledContainerColor = SophisticatedSurfaceVariant,
                        disabledContentColor = SophisticatedTextMuted
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = SophisticatedLavenderDark
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Synthesizing Venture & Model...",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Run",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Architect Venture & Pitch Deck",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        )
                    }
                }
            }
        }
    }
}
