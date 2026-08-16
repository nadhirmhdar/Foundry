package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

enum class CognitiveFilterGroup(val label: String, val badge: String) {
    ALL("All 10 Invariants", "10"),
    TRIAGE_TOP3("⚡ Speed Triage", "3"),
    ECONOMICS("💰 Unit Economics", "3"),
    TECH_AUDIT("🔬 Tech & Endpoints", "4")
}

data class CognitiveLoadPoint(
    val index: Int, // 1 to 10
    val categoryTag: String, // e.g. "WEDGE", "LOGIC", "ICP"
    val oneLinerLead: String, // Bold headline < 8 words
    val executiveSummary: String, // 1 crisp sentence
    val quantifiedMetricBadge: String?, // e.g. "+44% OEE", "$580M"
    val filterGroup: CognitiveFilterGroup,
    val depthKeyValues: List<Pair<String, String>> = emptyList()
)

data class CognitiveLoadBriefing(
    val ventureId: String,
    val ventureName: String,
    val industryDomain: String,
    val estimatedReadTimeSeconds: Int = 35,
    val cognitiveFrictionPercent: Double = 12.5, // Lower is better (12.5% load)
    val signalToNoiseRatioPercent: Double = 98.8,
    val points: List<CognitiveLoadPoint>
)
