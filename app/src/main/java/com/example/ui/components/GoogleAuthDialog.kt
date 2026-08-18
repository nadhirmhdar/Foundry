package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.auth.AuthUiState
import com.example.data.sync.SyncInfo
import com.example.ui.theme.*

@Composable
fun GoogleAuthDialog(
    isOpen: Boolean,
    authUiState: AuthUiState,
    syncInfo: SyncInfo,
    onSignInWithGoogle: () -> Unit,
    onSignOut: () -> Unit,
    onSyncNow: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    val context = LocalContext.current
    val user = authUiState.userProfile

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .testTag("google_auth_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SophisticatedDarkBg),
            border = BorderStroke(1.dp, SophisticatedBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top Header
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
                            border = BorderStroke(1.dp, SophisticatedLavender.copy(alpha = 0.5f))
                        ) {
                            Box(
                                modifier = Modifier.size(36.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudSync,
                                    contentDescription = "Cloud Identity",
                                    tint = SophisticatedLavender,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = "Firebase & Google Auth",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = SophisticatedTextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Cross-device multi-tenant portfolio sync",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SophisticatedTextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("auth_dialog_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = SophisticatedTextSecondary
                        )
                    }
                }

                Divider(color = SophisticatedBorderSubtle)

                if (authUiState.isAuthenticated && user != null) {
                    // Authenticated Profile State
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = SophisticatedSurface,
                        border = BorderStroke(1.dp, SophisticatedBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = SophisticatedLavender.copy(alpha = 0.2f),
                                    border = BorderStroke(1.5.dp, SophisticatedLavender)
                                ) {
                                    Box(
                                        modifier = Modifier.size(44.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = (user.displayName ?: user.email ?: "U").take(1).uppercase(),
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                color = SophisticatedLavender,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = user.displayName ?: "Verified Partner",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = SophisticatedTextPrimary,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFF1B382B),
                                            border = BorderStroke(1.dp, Color(0xFF34D399).copy(alpha = 0.4f))
                                        ) {
                                            Text(
                                                text = "GOOGLE SIGNED-IN",
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = Color(0xFF34D399),
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }
                                    }
                                    Text(
                                        text = user.email ?: "partner@processfoundry.ai",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = SophisticatedTextSecondary,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }

                            // Cloud Database status
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = SophisticatedSurfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudDone,
                                        contentDescription = null,
                                        tint = SophisticatedLavender,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Cloud Firestore Persistence Active • ${syncInfo.syncedCount} models in Vault",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = SophisticatedTextPrimary,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onSyncNow,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("auth_sync_now_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SophisticatedLavender,
                                contentColor = SophisticatedLavenderDark
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sync Cloud Vault", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = onSignOut,
                            modifier = Modifier
                                .height(44.dp)
                                .testTag("auth_sign_out_button"),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, SophisticatedBorder),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = SophisticatedTextSecondary
                            )
                        ) {
                            Text("Sign Out", fontSize = 12.sp)
                        }
                    }
                } else {
                    // Unauthenticated State
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = SophisticatedSurface,
                            border = BorderStroke(1.dp, SophisticatedBorder),
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = null,
                                    tint = SophisticatedLavender,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Sign in to Sync Your Portfolio",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = SophisticatedTextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Use Google Sign-in with Firebase Auth to securely sync your venture models across web, mobile, and emulator instances.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SophisticatedTextSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                ),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }

                        Button(
                            onClick = onSignInWithGoogle,
                            enabled = !authUiState.isAuthenticating,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("sign_in_with_google_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SophisticatedLavender,
                                contentColor = SophisticatedLavenderDark
                            )
                        ) {
                            if (authUiState.isAuthenticating) {
                                CircularProgressIndicator(
                                    color = SophisticatedLavenderDark,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Login,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Continue with Google Sign-In",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
