package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.local.VentureEntity
import com.example.data.model.BottleneckDomain
import com.example.data.model.ErpBottleneck
import com.example.data.sync.SyncInfo
import com.example.data.sync.SyncStatus
import com.example.ui.components.DomainBadge
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun VaultScreen(
    savedVentures: List<VentureEntity>,
    bottlenecks: List<ErpBottleneck>,
    onSelectVenture: (ErpBottleneck) -> Unit,
    onDeleteVenture: (String) -> Unit,
    onNavigateToScanner: () -> Unit,
    syncInfo: SyncInfo = SyncInfo(),
    onSyncWithCloud: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
    ) {
        // Vault Header with Cloud Sync Bar
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
                                text = "FOUNDER VENTURE VAULT",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SophisticatedLavender,
                                    letterSpacing = 0.8.sp,
                                    fontSize = 10.sp
                                )
                            )
                        }

                        Text(
                            text = "${savedVentures.size} Saved Models",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium,
                                color = SophisticatedTextSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }

                    Text(
                        text = "Your persistent portfolio of bookmarked process bottlenecks, startup pitch decks, and valuation models backed by Room & Cloud Firestore.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SophisticatedTextSecondary,
                            lineHeight = 18.sp,
                            fontSize = 12.sp
                        )
                    )

                    // Cloud Firestore Sync Bar
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SophisticatedDarkBg,
                        border = BorderStroke(1.dp, SophisticatedBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                val (statusColor, statusIcon) = when (syncInfo.status) {
                                    SyncStatus.SYNCING -> Pair(SophisticatedLavender, Icons.Default.Sync)
                                    SyncStatus.SYNCED -> Pair(SophisticatedEmerald, Icons.Default.CloudDone)
                                    SyncStatus.OFFLINE -> Pair(SophisticatedSoftAmber, Icons.Default.CloudOff)
                                    SyncStatus.ERROR -> Pair(SophisticatedCritical, Icons.Default.CloudQueue)
                                    SyncStatus.IDLE -> Pair(SophisticatedLavender, Icons.Default.CloudSync)
                                }

                                Icon(
                                    imageVector = statusIcon,
                                    contentDescription = "Sync Status",
                                    tint = statusColor,
                                    modifier = Modifier.size(16.dp)
                                )

                                Column {
                                    Text(
                                        text = when (syncInfo.status) {
                                            SyncStatus.SYNCING -> "Syncing with Cloud Firestore..."
                                            SyncStatus.SYNCED -> "Cloud Synced (${syncInfo.syncedCount} models)"
                                            SyncStatus.OFFLINE -> "Local Offline (Room DB Active)"
                                            SyncStatus.ERROR -> "Sync Offline"
                                            SyncStatus.IDLE -> "Room-to-Firestore Ready"
                                        },
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 11.sp,
                                            color = SophisticatedTextPrimary
                                        )
                                    )
                                    if (syncInfo.lastSyncTimestamp != null) {
                                        Text(
                                            text = "Last synced at ${timeFormat.format(Date(syncInfo.lastSyncTimestamp))}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.sp,
                                                color = SophisticatedTextMuted
                                            )
                                        )
                                    }
                                }
                            }

                            TextButton(
                                onClick = onSyncWithCloud,
                                enabled = syncInfo.status != SyncStatus.SYNCING,
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Sync",
                                    tint = SophisticatedLavender,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (syncInfo.status == SyncStatus.SYNCING) "Syncing..." else "Sync Now",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = SophisticatedLavender
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        if (savedVentures.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = SophisticatedSurface),
                    border = BorderStroke(1.dp, SophisticatedBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(SophisticatedSurfaceVariant, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.BookmarkBorder,
                                contentDescription = "Empty Vault",
                                tint = SophisticatedLavender,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Text(
                            text = "No Ventures Saved Yet",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = SophisticatedTextPrimary
                            )
                        )

                        Text(
                            text = "Browse the Radar Scanner and bookmark venture ideas to save their full pitch decks and financial models to your offline Room database.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SophisticatedTextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                lineHeight = 18.sp,
                                fontSize = 12.sp
                            )
                        )

                        Button(
                            onClick = onNavigateToScanner,
                            shape = RoundedCornerShape(100.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SophisticatedLavender,
                                contentColor = SophisticatedLavenderDark
                            )
                        ) {
                            Text(
                                text = "Explore Radar Opportunities",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            )
                        }
                    }
                }
            }
        } else {
            items(savedVentures, key = { it.id }) { entity ->
                val matchingBottleneck = bottlenecks.find { it.suggestedVentureIdea.id == entity.id }
                val domain = try { BottleneckDomain.valueOf(entity.domain) } catch (e: Exception) { BottleneckDomain.ERP_LOGIC }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (matchingBottleneck != null) {
                                onSelectVenture(matchingBottleneck)
                            }
                        },
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
                            DomainBadge(domain = domain)
                            Surface(
                                shape = CircleShape,
                                color = SophisticatedDarkBg,
                                border = BorderStroke(1.dp, SophisticatedBorder)
                            ) {
                                IconButton(
                                    onClick = { onDeleteVenture(entity.id) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Delete",
                                        tint = SophisticatedTextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = entity.ventureName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = SophisticatedTextPrimary,
                                    fontSize = 16.sp
                                )
                            )
                            Text(
                                text = entity.tagline,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SophisticatedSecondary,
                                    fontSize = 12.sp
                                )
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = SophisticatedDarkBg,
                                border = BorderStroke(1.dp, SophisticatedBorder),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("TARGET RAISE", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = SophisticatedTextMuted))
                                    Text("$${entity.targetRaiseMillions}M", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = SophisticatedLavenderLight))
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = SophisticatedDarkBg,
                                border = BorderStroke(1.dp, SophisticatedBorder),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("SEED VALUATION", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = SophisticatedTextMuted))
                                    Text("$${entity.seedValuationMillions.toInt()}M", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = SophisticatedLavender))
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = SophisticatedDarkBg,
                                border = BorderStroke(1.dp, SophisticatedBorder),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("SAVED DATE", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = SophisticatedTextMuted))
                                    Text(dateFormat.format(Date(entity.createdAt)), style = MaterialTheme.typography.bodySmall.copy(color = SophisticatedTextSecondary, fontSize = 11.sp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
