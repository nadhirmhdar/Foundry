package com.example.data.model

import android.graphics.Bitmap

// --- High Thinking Models ---
data class HighThinkingAuditResult(
    val query: String,
    val ventureTitle: String,
    val thoughtChainSummary: String,
    val deepArchitecturalAnalysis: String,
    val criticalFailurePoints: List<String>,
    val invariantMathematicalProof: String,
    val investmentCommitteeVerdict: String,
    val defensibilityScore: Double,
    val executionRiskScore: Double,
    val timestamp: Long = System.currentTimeMillis()
)

// --- Image Studio Models ---
enum class ImageAspectRatio(val label: String, val apiValue: String, val ratioFloat: Float) {
    SQUARE("1:1 Square", "1:1", 1.0f),
    LANDSCAPE("16:9 Cinema", "16:9", 16f / 9f),
    PORTRAIT("9:16 Story", "9:16", 9f / 16f),
    STANDARD("4:3 Standard", "4:3", 4f / 3f)
}

data class GeneratedAiImage(
    val id: String,
    val prompt: String,
    val base64Data: String? = null,
    val bitmap: Bitmap? = null,
    val aspectRatio: ImageAspectRatio = ImageAspectRatio.SQUARE,
    val isEdit: Boolean = false,
    val parentImageId: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

// --- Veo 3 Video Generation Models ---
enum class VeoVideoAspectRatio(val label: String, val apiValue: String, val ratioFloat: Float) {
    LANDSCAPE_16_9("16:9 (Landscape)", "16:9", 16f / 9f),
    PORTRAIT_9_16("9:16 (Portrait)", "9:16", 9f / 16f)
}

enum class VeoResolution(val label: String, val apiValue: String) {
    RES_1080P("1080p FHD", "1080p"),
    RES_720P("720p HD", "720p")
}

data class GeneratedVeoVideo(
    val id: String,
    val prompt: String,
    val aspectRatio: VeoVideoAspectRatio,
    val resolution: VeoResolution,
    val operationName: String? = null,
    val status: VeoVideoStatus = VeoVideoStatus.COMPLETED,
    val videoUri: String? = null,
    val simulatedPreviewDescription: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

enum class VeoVideoStatus {
    QUEUED,
    GENERATING,
    COMPLETED,
    FAILED
}
