package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.GroundedMarketIntelligenceResult
import com.example.data.model.SearchGroundingSource
import com.example.ui.theme.*

@Composable
fun SearchGroundingDialog(
    isOpen: Boolean,
    query: String,
    isLoading: Boolean,
    result: GroundedMarketIntelligenceResult?,
    errorMessage: String?,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f)
                .testTag("search_grounding_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SophisticatedDarkBg),
            border = BorderStroke(1.dp, SophisticatedBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = SophisticatedSurfaceVariant,
                            border = BorderStroke(1.dp, SophisticatedLavender.copy(alpha = 0.6f))
                        ) {
                            Box(
                                modifier = Modifier.size(38.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TravelExplore,
                                    contentDescription = "Search Grounding",
                                    tint = SophisticatedLavender,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Google Search Grounding",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = SophisticatedTextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = SophisticatedSurfaceVariant,
                                    border = BorderStroke(1.dp, SophisticatedBorder)
                                ) {
                                    Text(
                                        text = "GEMINI 3.5 FLASH",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SophisticatedLavender
                                        )
                                    )
                                }
                            }
                            Text(
                                text = "Real-time web research & verified market multiples",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SophisticatedTextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("search_grounding_dismiss_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = SophisticatedTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Search Input Field
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_grounding_input"),
                    placeholder = {
                        Text(
                            "e.g. Latest ARR multiples for SAP S/4HANA sidecar tools",
                            color = SophisticatedTextSecondary.copy(alpha = 0.6f),
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = SophisticatedLavender
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = { onSearch(query) },
                            enabled = query.isNotBlank() && !isLoading,
                            modifier = Modifier.testTag("search_grounding_submit_button")
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = SophisticatedLavender,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = "Search",
                                    tint = if (query.isNotBlank()) SophisticatedLavender else SophisticatedTextSecondary.copy(alpha = 0.4f)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SophisticatedSurface,
                        unfocusedContainerColor = SophisticatedSurface,
                        focusedBorderColor = SophisticatedLavender,
                        unfocusedBorderColor = SophisticatedBorder,
                        focusedTextColor = SophisticatedTextPrimary,
                        unfocusedTextColor = SophisticatedTextPrimary
                    ),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator(
                                color = SophisticatedLavender,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = "Grounding query with Google Search index...",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SophisticatedTextSecondary,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                } else if (errorMessage != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else if (result != null) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Multiples Badge Highlights
                        if (result.verifiedMultiples.isNotEmpty()) {
                            item {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = "VERIFIED VALUATION MULTIPLES & BENCHMARKS",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = SophisticatedLavender,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.6.sp,
                                            fontSize = 10.sp
                                        )
                                    )
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        items(result.verifiedMultiples) { multiple ->
                                            Surface(
                                                shape = RoundedCornerShape(100.dp),
                                                color = SophisticatedSurfaceVariant,
                                                border = BorderStroke(1.dp, SophisticatedLavender.copy(alpha = 0.5f))
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.TrendingUp,
                                                        contentDescription = null,
                                                        tint = SophisticatedLavender,
                                                        modifier = Modifier.size(13.dp)
                                                    )
                                                    Text(
                                                        text = multiple,
                                                        style = MaterialTheme.typography.labelSmall.copy(
                                                            color = SophisticatedTextPrimary,
                                                            fontWeight = FontWeight.SemiBold,
                                                            fontSize = 11.sp
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Key Market Signals
                        if (result.keySignals.isNotEmpty()) {
                            item {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    color = SophisticatedSurface,
                                    border = BorderStroke(1.dp, SophisticatedBorder)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "Grounded Market Signals",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                color = SophisticatedTextPrimary,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        result.keySignals.forEach { signal ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.Top
                                            ) {
                                                Text(
                                                    text = "◈",
                                                    color = SophisticatedLavender,
                                                    fontSize = 10.sp,
                                                    modifier = Modifier.padding(top = 2.dp)
                                                )
                                                Text(
                                                    text = signal,
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        color = SophisticatedTextSecondary,
                                                        fontSize = 12.sp,
                                                        lineHeight = 16.sp
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Synthesis Text Body
                        item {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                color = SophisticatedSurface,
                                border = BorderStroke(1.dp, SophisticatedBorder)
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Executive Market Synthesis",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = SophisticatedTextPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = result.synthesisText,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = SophisticatedTextSecondary,
                                            fontSize = 12.sp,
                                            lineHeight = 18.sp
                                        )
                                    )
                                }
                            }
                        }

                        // Grounding Sources & Cited Links
                        if (result.sources.isNotEmpty()) {
                            item {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = "GROUNDED WEB SOURCES",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = SophisticatedLavender,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.6.sp,
                                            fontSize = 10.sp
                                        )
                                    )
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        result.sources.forEach { source ->
                                            Surface(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        try {
                                                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(source.url))
                                                            context.startActivity(browserIntent)
                                                        } catch (e: Exception) {
                                                            // Ignore if no browser
                                                        }
                                                    },
                                                shape = RoundedCornerShape(10.dp),
                                                color = SophisticatedSurface,
                                                border = BorderStroke(1.dp, SophisticatedBorderSubtle)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                        modifier = Modifier.weight(1f)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Public,
                                                            contentDescription = null,
                                                            tint = SophisticatedLavender,
                                                            modifier = Modifier.size(15.dp)
                                                        )
                                                        Column {
                                                            Text(
                                                                text = source.title,
                                                                style = MaterialTheme.typography.labelSmall.copy(
                                                                    color = SophisticatedTextPrimary,
                                                                    fontWeight = FontWeight.Medium,
                                                                    fontSize = 11.sp
                                                                ),
                                                                maxLines = 1
                                                            )
                                                            Text(
                                                                text = source.domain,
                                                                style = MaterialTheme.typography.labelSmall.copy(
                                                                    color = SophisticatedTextSecondary,
                                                                    fontSize = 9.sp
                                                                ),
                                                                maxLines = 1
                                                            )
                                                        }
                                                    }
                                                    Icon(
                                                        imageVector = Icons.Default.OpenInNew,
                                                        contentDescription = "Open link",
                                                        tint = SophisticatedTextSecondary,
                                                        modifier = Modifier.size(14.dp)
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
            }
        }
    }
}
