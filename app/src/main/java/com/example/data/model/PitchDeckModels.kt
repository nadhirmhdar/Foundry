package com.example.data.model

data class PitchDeckSlide(
    val slideNumber: Int,
    val title: String,
    val subtitle: String,
    val keyPoints: List<String>,
    val metricHighlight: String? = null,
    val metricLabel: String? = null,
    val visualType: SlideVisualType = SlideVisualType.BULLETS,
    val visualDataPoints: List<Pair<String, String>> = emptyList(),
    val presenterNotes: String
)

enum class SlideVisualType {
    PROBLEM_BREAKDOWN,
    LOGIC_COMPARISON,
    MARKET_TAM_SAM_SOM,
    ARCHITECTURE_FLOW,
    FINANCIAL_PROJECTION,
    BUSINESS_MODEL,
    COMPETITIVE_MATRIX,
    BULLETS
}

data class PitchDeck(
    val title: String,
    val subtitle: String,
    val founderName: String = "Venture Founders",
    val fundingStage: String = "Seed / Series A",
    val targetRaiseAmountMillions: Double = 3.5,
    val slides: List<PitchDeckSlide>
)
