package com.example.ui.screens

import android.graphics.Bitmap
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.*
import com.example.ui.theme.*

enum class AiStudioTab(val title: String, val subtitle: String, val modelBadge: String) {
    HIGH_THINKING("High Thinking Mode", "Complex Architectural Reasoning", "gemini-3.1-pro-preview"),
    IMAGE_STUDIO("Create & Edit Images", "Visuals & Blueprint Canvas", "gemini-3.1-flash-image-preview"),
    VEO_VIDEO("Veo 3 Video Studio", "16:9 / 9:16 Video Generation", "veo-3.1-fast-generate-preview")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiStudioHubDialog(
    isOpen: Boolean,
    currentBottleneck: ErpBottleneck?,
    activeTab: AiStudioTab,
    onTabSelected: (AiStudioTab) -> Unit,
    onDismiss: () -> Unit,
    // High Thinking Props
    highThinkingQuery: String,
    onHighThinkingQueryChange: (String) -> Unit,
    isHighThinkingRunning: Boolean,
    highThinkingResult: HighThinkingAuditResult?,
    highThinkingError: String?,
    onRunHighThinking: (String) -> Unit,
    // Image Studio Props
    imagePrompt: String,
    onImagePromptChange: (String) -> Unit,
    selectedImageAspectRatio: ImageAspectRatio,
    onImageAspectRatioChange: (ImageAspectRatio) -> Unit,
    isImageGenerating: Boolean,
    generatedImages: List<GeneratedAiImage>,
    selectedImageForEdit: GeneratedAiImage?,
    onSelectImageForEdit: (GeneratedAiImage?) -> Unit,
    imageGenerationError: String?,
    onGenerateOrEditImage: (String, Bitmap?, ImageAspectRatio) -> Unit,
    // Veo Video Props
    veoPrompt: String,
    onVeoPromptChange: (String) -> Unit,
    selectedVeoAspectRatio: VeoVideoAspectRatio,
    onVeoAspectRatioChange: (VeoVideoAspectRatio) -> Unit,
    selectedVeoResolution: VeoResolution,
    onVeoResolutionChange: (VeoResolution) -> Unit,
    isVeoGenerating: Boolean,
    generatedVideos: List<GeneratedVeoVideo>,
    selectedVeoVideo: GeneratedVeoVideo?,
    onSelectVeoVideo: (GeneratedVeoVideo?) -> Unit,
    veoGenerationError: String?,
    onGenerateVeoVideo: (String, VeoVideoAspectRatio, VeoResolution) -> Unit
) {
    if (!isOpen) return

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 16.dp),
            shape = RoundedCornerShape(28.dp),
            color = SophisticatedDarkBg,
            border = BorderStroke(1.dp, SophisticatedBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Top Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SophisticatedSurface,
                            border = BorderStroke(1.dp, SophisticatedLavender.copy(alpha = 0.4f)),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "AI Studio",
                                    tint = SophisticatedLavender,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Gemini & Veo AI Suite",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = SophisticatedTextPrimary,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 18.sp
                                    )
                                )
                                Surface(
                                    shape = RoundedCornerShape(100.dp),
                                    color = SophisticatedSurfaceVariant,
                                    border = BorderStroke(1.dp, SophisticatedBorderSubtle)
                                ) {
                                    Text(
                                        text = activeTab.modelBadge,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SophisticatedLavender,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    )
                                }
                            }
                            Text(
                                text = "Deep Thinking Reasoning • 16:9/9:16 Veo 3 Video • Image Create & Edit",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SophisticatedTextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .background(SophisticatedSurface, CircleShape)
                            .border(1.dp, SophisticatedBorderSubtle, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = SophisticatedTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tab Switcher Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SophisticatedSurface, RoundedCornerShape(16.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    AiStudioTab.values().forEach { tab ->
                        val isSelected = activeTab == tab
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onTabSelected(tab) }
                                .testTag("ai_tab_${tab.name.lowercase()}"),
                            color = if (isSelected) SophisticatedSurfaceVariant else Color.Transparent,
                            border = if (isSelected) BorderStroke(1.dp, SophisticatedLavender.copy(alpha = 0.6f)) else null,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = when (tab) {
                                            AiStudioTab.HIGH_THINKING -> Icons.Default.Psychology
                                            AiStudioTab.IMAGE_STUDIO -> Icons.Default.Image
                                            AiStudioTab.VEO_VIDEO -> Icons.Default.Videocam
                                        },
                                        contentDescription = tab.title,
                                        tint = if (isSelected) SophisticatedLavender else SophisticatedTextSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = when (tab) {
                                            AiStudioTab.HIGH_THINKING -> "Thinking"
                                            AiStudioTab.IMAGE_STUDIO -> "Images"
                                            AiStudioTab.VEO_VIDEO -> "Veo Video"
                                        },
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) SophisticatedTextPrimary else SophisticatedTextSecondary,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tab Content Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    when (activeTab) {
                        AiStudioTab.HIGH_THINKING -> {
                            HighThinkingStudioView(
                                bottleneck = currentBottleneck,
                                query = highThinkingQuery,
                                onQueryChange = onHighThinkingQueryChange,
                                isRunning = isHighThinkingRunning,
                                result = highThinkingResult,
                                errorMessage = highThinkingError,
                                onRunAudit = onRunHighThinking
                            )
                        }
                        AiStudioTab.IMAGE_STUDIO -> {
                            ImageCreateAndEditStudioView(
                                prompt = imagePrompt,
                                onPromptChange = onImagePromptChange,
                                selectedAspectRatio = selectedImageAspectRatio,
                                onAspectRatioChange = onImageAspectRatioChange,
                                isGenerating = isImageGenerating,
                                images = generatedImages,
                                selectedImageForEdit = selectedImageForEdit,
                                onSelectImageForEdit = onSelectImageForEdit,
                                errorMessage = imageGenerationError,
                                onGenerateOrEdit = onGenerateOrEditImage
                            )
                        }
                        AiStudioTab.VEO_VIDEO -> {
                            VeoVideoStudioView(
                                bottleneck = currentBottleneck,
                                prompt = veoPrompt,
                                onPromptChange = onVeoPromptChange,
                                selectedAspectRatio = selectedVeoAspectRatio,
                                onAspectRatioChange = onVeoAspectRatioChange,
                                selectedResolution = selectedVeoResolution,
                                onResolutionChange = onVeoResolutionChange,
                                isGenerating = isVeoGenerating,
                                videos = generatedVideos,
                                selectedVideo = selectedVeoVideo,
                                onSelectVideo = onSelectVeoVideo,
                                errorMessage = veoGenerationError,
                                onGenerateVideo = onGenerateVeoVideo
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// 1. HIGH THINKING VIEW (gemini-3.1-pro-preview with thinkingLevel = HIGH)
// -------------------------------------------------------------------------------------------------

@Composable
fun HighThinkingStudioView(
    bottleneck: ErpBottleneck?,
    query: String,
    onQueryChange: (String) -> Unit,
    isRunning: Boolean,
    result: HighThinkingAuditResult?,
    errorMessage: String?,
    onRunAudit: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    val thinkingPrompts = listOf(
        "Stress-test non-invasive CDC ingestion under 100k records/sec burst with zero ERP table locks",
        "Formulate deterministic invariant proof for real-time stochastic queue dispatching",
        "Audit catastrophic failure modes during unannounced quarterly ERP database schema migration",
        "Simulate Investment Committee adversarial audit challenging our 48-hour POC claim"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // High Thinking Banner
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SophisticatedSurface,
            border = BorderStroke(1.dp, SophisticatedLavender.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = SophisticatedSurfaceVariant,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "Thinking",
                            tint = SophisticatedLavender,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "gemini-3.1-pro-preview",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = SophisticatedTextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = SophisticatedLavender.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, SophisticatedLavender.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "THINKING: HIGH",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SophisticatedLavender,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                    Text(
                        text = "Exhaustive invariant constraint reasoning & deep adversarial risk audit.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SophisticatedTextSecondary,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        // Query Input Field
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("high_thinking_input"),
            placeholder = {
                Text(
                    text = "Pose an intricate architectural, mathematical, or investment committee query...",
                    style = MaterialTheme.typography.bodySmall.copy(color = SophisticatedTextMuted, fontSize = 12.sp)
                )
            },
            minLines = 3,
            maxLines = 5,
            enabled = !isRunning,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SophisticatedLavender,
                unfocusedBorderColor = SophisticatedBorder,
                focusedTextColor = SophisticatedTextPrimary,
                unfocusedTextColor = SophisticatedTextPrimary,
                cursorColor = SophisticatedLavender,
                focusedContainerColor = SophisticatedSurface,
                unfocusedContainerColor = SophisticatedSurface
            )
        )

        // Suggestion Chips
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "COMPLEX AUDIT BENCHMARKS",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = SophisticatedTextMuted,
                    letterSpacing = 0.5.sp
                )
            )
            thinkingPrompts.forEach { p ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SophisticatedSurface,
                    border = BorderStroke(1.dp, SophisticatedBorderSubtle),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !isRunning) { onQueryChange(p) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Prompt",
                            tint = SophisticatedLavender,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = p,
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
                style = MaterialTheme.typography.bodySmall.copy(color = SophisticatedCritical, fontSize = 12.sp)
            )
        }

        // Run Button
        Button(
            onClick = { onRunAudit(query) },
            enabled = query.isNotBlank() && !isRunning,
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .testTag("run_high_thinking_button"),
            shape = RoundedCornerShape(100.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SophisticatedLavender,
                contentColor = SophisticatedLavenderDark
            )
        ) {
            if (isRunning) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = SophisticatedLavenderDark
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "High Thinking Reasoning in Progress...",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = "Execute",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Execute High Thinking Invariant Audit",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp)
                )
            }
        }

        // Result Display Card
        result?.let { audit ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SophisticatedSurface, RoundedCornerShape(20.dp))
                    .border(1.dp, SophisticatedBorder, RoundedCornerShape(20.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Verdict Banner
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SophisticatedLavender.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, SophisticatedLavender.copy(alpha = 0.35f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "INVESTMENT COMMITTEE VERDICT",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SophisticatedLavender,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 0.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = audit.investmentCommitteeVerdict,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = SophisticatedTextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        )
                    }
                }

                // Scores Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        color = SophisticatedDarkBg,
                        border = BorderStroke(1.dp, SophisticatedBorderSubtle)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Defensibility Score",
                                style = MaterialTheme.typography.labelSmall.copy(color = SophisticatedTextMuted, fontSize = 10.sp)
                            )
                            Text(
                                text = "${audit.defensibilityScore} / 10.0",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = SophisticatedLavender,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        color = SophisticatedDarkBg,
                        border = BorderStroke(1.dp, SophisticatedBorderSubtle)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Execution Risk Index",
                                style = MaterialTheme.typography.labelSmall.copy(color = SophisticatedTextMuted, fontSize = 10.sp)
                            )
                            Text(
                                text = "${audit.executionRiskScore} / 10.0",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = if (audit.executionRiskScore < 4.5) SophisticatedLavender else SophisticatedSecondary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            )
                        }
                    }
                }

                // Reasoning Chain
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "THOUGHT CHAIN & REASONING PATH",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SophisticatedTextMuted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SophisticatedDarkBg,
                        border = BorderStroke(1.dp, SophisticatedBorderSubtle)
                    ) {
                        Text(
                            text = audit.thoughtChainSummary,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SophisticatedTextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        )
                    }
                }

                // Invariant Proof
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "MATHEMATICAL INVARIANT PROOF",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SophisticatedTextMuted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SophisticatedDarkBg,
                        border = BorderStroke(1.dp, SophisticatedBorderSubtle)
                    ) {
                        Text(
                            text = audit.invariantMathematicalProof,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SophisticatedLavender,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        )
                    }
                }

                // Critical Failure Modes
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "IDENTIFIED CRITICAL FAILURE MODES",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SophisticatedTextMuted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                    audit.criticalFailurePoints.forEach { point ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("•", color = SophisticatedSecondary, fontSize = 14.sp)
                            Text(
                                text = point,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SophisticatedTextPrimary,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// 2. IMAGE STUDIO VIEW (gemini-3.1-flash-image-preview - Create & Edit)
// -------------------------------------------------------------------------------------------------

@Composable
fun ImageCreateAndEditStudioView(
    prompt: String,
    onPromptChange: (String) -> Unit,
    selectedAspectRatio: ImageAspectRatio,
    onAspectRatioChange: (ImageAspectRatio) -> Unit,
    isGenerating: Boolean,
    images: List<GeneratedAiImage>,
    selectedImageForEdit: GeneratedAiImage?,
    onSelectImageForEdit: (GeneratedAiImage?) -> Unit,
    errorMessage: String?,
    onGenerateOrEdit: (String, Bitmap?, ImageAspectRatio) -> Unit
) {
    val scrollState = rememberScrollState()

    val imagePresets = listOf(
        "3D blueprint schematic of zero-latency ERP sidecar proxy in dark cybernetic command room",
        "Photorealistic high-speed industrial robotic arm with multi-spectral laser QC sensor",
        "Modern minimalist investor pitch slide graphic showing 100x stochastic optimization curve",
        "Volumetric shopfloor telemetry heat-map showing bottleneck flow dynamics"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Banner
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SophisticatedSurface,
            border = BorderStroke(1.dp, SophisticatedLavender.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = SophisticatedSurfaceVariant,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Image Model",
                            tint = SophisticatedLavender,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "gemini-3.1-flash-image-preview",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = SophisticatedTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Text-to-image creation and iterative multi-modal prompt editing.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SophisticatedTextSecondary,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        // Edit Mode Target Banner (if an image is selected for editing)
        if (selectedImageForEdit != null) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SophisticatedLavender.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, SophisticatedLavender.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editing Mode",
                            tint = SophisticatedLavender,
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text(
                                text = "EDITING SELECTED IMAGE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SophisticatedLavender,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                            Text(
                                text = selectedImageForEdit.prompt.take(45) + "...",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SophisticatedTextPrimary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    IconButton(
                        onClick = { onSelectImageForEdit(null) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear edit",
                            tint = SophisticatedTextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        // Aspect Ratio Selector
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "ASPECT RATIO",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = SophisticatedTextMuted
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ImageAspectRatio.values().forEach { ratio ->
                    val isSelected = selectedAspectRatio == ratio
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onAspectRatioChange(ratio) },
                        color = if (isSelected) SophisticatedSurfaceVariant else SophisticatedSurface,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) SophisticatedLavender else SophisticatedBorderSubtle
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = ratio.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) SophisticatedLavender else SophisticatedTextSecondary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // Prompt Input
        OutlinedTextField(
            value = prompt,
            onValueChange = onPromptChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("image_prompt_input"),
            placeholder = {
                Text(
                    text = if (selectedImageForEdit != null)
                        "Describe your edit (e.g. 'Add heat-map overlay on the robotic arm', 'Render in dark neon blueprint CAD style')..."
                    else
                        "Describe the visual asset to create with Gemini 3.1 Flash Image...",
                    style = MaterialTheme.typography.bodySmall.copy(color = SophisticatedTextMuted, fontSize = 12.sp)
                )
            },
            minLines = 3,
            maxLines = 4,
            enabled = !isGenerating,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SophisticatedLavender,
                unfocusedBorderColor = SophisticatedBorder,
                focusedTextColor = SophisticatedTextPrimary,
                unfocusedTextColor = SophisticatedTextPrimary,
                cursorColor = SophisticatedLavender,
                focusedContainerColor = SophisticatedSurface,
                unfocusedContainerColor = SophisticatedSurface
            )
        )

        // Presets
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "SUGGESTED VISUAL ASSETS",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = SophisticatedTextMuted
                )
            )
            imagePresets.take(2).forEach { p ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SophisticatedSurface,
                    border = BorderStroke(1.dp, SophisticatedBorderSubtle),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !isGenerating) { onPromptChange(p) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Preset",
                            tint = SophisticatedLavender,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = p,
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
                style = MaterialTheme.typography.bodySmall.copy(color = SophisticatedCritical, fontSize = 12.sp)
            )
        }

        // Action Button
        Button(
            onClick = {
                onGenerateOrEdit(
                    prompt,
                    selectedImageForEdit?.bitmap,
                    selectedAspectRatio
                )
            },
            enabled = prompt.isNotBlank() && !isGenerating,
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .testTag("generate_image_button"),
            shape = RoundedCornerShape(100.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SophisticatedLavender,
                contentColor = SophisticatedLavenderDark
            )
        ) {
            if (isGenerating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = SophisticatedLavenderDark
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (selectedImageForEdit != null) "Editing Image Asset..." else "Generating Image with Gemini 3.1...",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp)
                )
            } else {
                Icon(
                    imageVector = if (selectedImageForEdit != null) Icons.Default.AutoFixHigh else Icons.Default.Brush,
                    contentDescription = "Run",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (selectedImageForEdit != null) "Apply Edit to Image" else "Generate Image Asset",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp)
                )
            }
        }

        // Gallery of Created / Edited Images
        if (images.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "GENERATED IMAGE GALLERY (${images.size})",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = SophisticatedTextMuted
                    )
                )

                images.forEach { img ->
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = SophisticatedSurface,
                        border = BorderStroke(
                            1.dp,
                            if (selectedImageForEdit?.id == img.id) SophisticatedLavender else SophisticatedBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            // Image display or fallback canvas
                            if (img.bitmap != null) {
                                Image(
                                    bitmap = img.bitmap.asImageBitmap(),
                                    contentDescription = img.prompt,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                // Simulated high-fidelity preview canvas
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(160.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    color = SophisticatedDarkBg,
                                    border = BorderStroke(1.dp, SophisticatedBorderSubtle)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Image,
                                            contentDescription = "Visual",
                                            tint = SophisticatedLavender,
                                            modifier = Modifier.size(36.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = img.prompt,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = SophisticatedTextPrimary,
                                                textAlign = TextAlign.Center,
                                                fontSize = 12.sp
                                            ),
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Rendered in ${img.aspectRatio.label} • 1K Resolution",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = SophisticatedLavender,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = img.prompt,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = SophisticatedTextPrimary,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 11.sp
                                        ),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    if (img.isEdit) {
                                        Text(
                                            text = "Edited via Prompt Refinement",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = SophisticatedSecondary,
                                                fontSize = 9.sp
                                            )
                                        )
                                    }
                                }

                                Button(
                                    onClick = { onSelectImageForEdit(img) },
                                    shape = RoundedCornerShape(100.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = SophisticatedSurfaceVariant,
                                        contentColor = SophisticatedLavender
                                    ),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoFixHigh,
                                        contentDescription = "Edit",
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Edit with Prompt",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)
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

// -------------------------------------------------------------------------------------------------
// 3. VEO 3 VIDEO STUDIO VIEW (veo-3.1-fast-generate-preview with 16:9 or 9:16)
// -------------------------------------------------------------------------------------------------

@Composable
fun VeoVideoStudioView(
    bottleneck: ErpBottleneck?,
    prompt: String,
    onPromptChange: (String) -> Unit,
    selectedAspectRatio: VeoVideoAspectRatio,
    onAspectRatioChange: (VeoVideoAspectRatio) -> Unit,
    selectedResolution: VeoResolution,
    onResolutionChange: (VeoResolution) -> Unit,
    isGenerating: Boolean,
    videos: List<GeneratedVeoVideo>,
    selectedVideo: GeneratedVeoVideo?,
    onSelectVideo: (GeneratedVeoVideo?) -> Unit,
    errorMessage: String?,
    onGenerateVideo: (String, VeoVideoAspectRatio, VeoResolution) -> Unit
) {
    val scrollState = rememberScrollState()

    val veoPresets = listOf(
        "3D cinematic simulation of automated shopfloor AGV deadlock and dynamic rerouting",
        "Macro drone flythrough of autonomous semiconductor wafer robotic placement line",
        "3D dark-mode holographic projection of enterprise SaaS sidecar proxy telemetry stream",
        "High-speed pharmaceutical sterile fill-finish optical verification with pneumatic diverter"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Veo Model Banner
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SophisticatedSurface,
            border = BorderStroke(1.dp, SophisticatedLavender.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = SophisticatedSurfaceVariant,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = "Veo Model",
                            tint = SophisticatedLavender,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "veo-3.1-fast-generate-preview",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = SophisticatedTextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = SophisticatedLavender.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, SophisticatedLavender.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "VEO 3",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SophisticatedLavender,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                    Text(
                        text = "Generate high-fidelity video in 16:9 (Landscape) or 9:16 (Portrait) formats.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SophisticatedTextSecondary,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        // Aspect Ratio Selector (MANDATORY: 16:9 or 9:16)
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "ASPECT RATIO (16:9 / 9:16 MANDATE)",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = SophisticatedTextMuted,
                    letterSpacing = 0.5.sp
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                VeoVideoAspectRatio.values().forEach { ratio ->
                    val isSelected = selectedAspectRatio == ratio
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onAspectRatioChange(ratio) }
                            .testTag("veo_aspect_${ratio.apiValue.replace(":", "_")}"),
                        color = if (isSelected) SophisticatedSurfaceVariant else SophisticatedSurface,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) SophisticatedLavender else SophisticatedBorderSubtle
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (ratio == VeoVideoAspectRatio.LANDSCAPE_16_9) Icons.Default.Tv else Icons.Default.StayCurrentPortrait,
                                contentDescription = ratio.label,
                                tint = if (isSelected) SophisticatedLavender else SophisticatedTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = ratio.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) SophisticatedTextPrimary else SophisticatedTextSecondary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // Resolution Selector
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "RESOLUTION",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = SophisticatedTextMuted
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                VeoResolution.values().forEach { res ->
                    val isSelected = selectedResolution == res
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onResolutionChange(res) },
                        color = if (isSelected) SophisticatedSurfaceVariant else SophisticatedSurface,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) SophisticatedLavender else SophisticatedBorderSubtle
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = res.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) SophisticatedLavender else SophisticatedTextSecondary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // Prompt Input
        OutlinedTextField(
            value = prompt,
            onValueChange = onPromptChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("veo_prompt_input"),
            placeholder = {
                Text(
                    text = "Describe your 3D video simulation for Veo 3...",
                    style = MaterialTheme.typography.bodySmall.copy(color = SophisticatedTextMuted, fontSize = 12.sp)
                )
            },
            minLines = 3,
            maxLines = 4,
            enabled = !isGenerating,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SophisticatedLavender,
                unfocusedBorderColor = SophisticatedBorder,
                focusedTextColor = SophisticatedTextPrimary,
                unfocusedTextColor = SophisticatedTextPrimary,
                cursorColor = SophisticatedLavender,
                focusedContainerColor = SophisticatedSurface,
                unfocusedContainerColor = SophisticatedSurface
            )
        )

        // Presets
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "CURATED INDUSTRIAL SIMULATION PRESETS",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = SophisticatedTextMuted
                )
            )
            veoPresets.take(2).forEach { p ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SophisticatedSurface,
                    border = BorderStroke(1.dp, SophisticatedBorderSubtle),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !isGenerating) { onPromptChange(p) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Movie,
                            contentDescription = "Preset",
                            tint = SophisticatedLavender,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = p,
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
                style = MaterialTheme.typography.bodySmall.copy(color = SophisticatedCritical, fontSize = 12.sp)
            )
        }

        // Generate Video Button
        Button(
            onClick = { onGenerateVideo(prompt, selectedAspectRatio, selectedResolution) },
            enabled = prompt.isNotBlank() && !isGenerating,
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .testTag("generate_veo_video_button"),
            shape = RoundedCornerShape(100.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SophisticatedLavender,
                contentColor = SophisticatedLavenderDark
            )
        ) {
            if (isGenerating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = SophisticatedLavenderDark
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Generating Video with Veo 3 (${selectedAspectRatio.label})...",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = "Generate",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Generate Veo 3 Video (${selectedAspectRatio.apiValue})",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp)
                )
            }
        }

        // Generated Videos Showcase
        if (videos.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "GENERATED VEO 3 VIDEO ARCHIVE (${videos.size})",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = SophisticatedTextMuted
                    )
                )

                videos.forEach { video ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = SophisticatedSurface,
                        border = BorderStroke(1.dp, SophisticatedBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Video Canvas Simulation
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(if (video.aspectRatio == VeoVideoAspectRatio.PORTRAIT_9_16) 240.dp else 160.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                SophisticatedDarkBg,
                                                Color(0xFF0F172A)
                                            )
                                        )
                                    )
                                    .border(1.dp, SophisticatedBorderSubtle, RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = SophisticatedLavender.copy(alpha = 0.2f),
                                        modifier = Modifier.size(48.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = "Play",
                                                tint = SophisticatedLavender,
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = video.simulatedPreviewDescription,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = SophisticatedTextPrimary,
                                            fontSize = 11.sp,
                                            textAlign = TextAlign.Center
                                        ),
                                        maxLines = 3,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Surface(
                                            shape = RoundedCornerShape(100.dp),
                                            color = SophisticatedSurfaceVariant
                                        ) {
                                            Text(
                                                text = "Veo 3 • ${video.aspectRatio.apiValue}",
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = SophisticatedLavender,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(100.dp),
                                            color = SophisticatedSurfaceVariant
                                        ) {
                                            Text(
                                                text = video.resolution.label,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = SophisticatedTextSecondary,
                                                    fontSize = 9.sp
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = video.prompt,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SophisticatedTextSecondary,
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
