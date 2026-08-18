package com.example.ui.viewmodel

import android.graphics.Bitmap
import com.example.data.local.VentureEntity
import com.example.data.model.*
import com.example.data.sync.SyncInfo
import com.example.ui.screens.AiStudioTab

enum class NavigationTab(val label: String, val iconName: String) {
    SCANNER("Radar Scanner", "radar"),
    ARCHITECT("Venture Architect", "architecture"),
    PITCH_DECK("Pitch Studio", "presentation"),
    VALUATION("Valuation Model", "monetization"),
    VAULT("Saved Vault", "inventory")
}

data class ValuationCalculatorState(
    val customAcvThousands: Double = 160.0,
    val customCacThousands: Double = 35.0,
    val customYear3ArrMillions: Double = 12.8,
    val customExitMultiple: Double = 12.0,
    val customTargetRaiseMillions: Double = 3.5
) {
    val computedLtvThousands: Double get() = (customAcvThousands * 0.85 * 6.5)
    val computedLtvToCacRatio: Double get() = if (customCacThousands > 0) computedLtvThousands / customCacThousands else 0.0
    val computedPaybackMonths: Int get() = if (customAcvThousands > 0) ((customCacThousands / customAcvThousands) * 12).toInt().coerceIn(4, 24) else 12
    val computedYear3ValuationMillions: Double get() = customYear3ArrMillions * customExitMultiple
    val computedPostMoneySeedValuationMillions: Double get() = customTargetRaiseMillions * 4.0
}

data class MainUiState(
    val selectedTab: NavigationTab = NavigationTab.SCANNER,
    val bottlenecks: List<ErpBottleneck> = emptyList(),
    val selectedBottleneck: ErpBottleneck? = null,
    val selectedDomainFilter: BottleneckDomain? = null,
    val selectedDepartmentFilter: String? = null,
    val selectedPriorityFilter: SeverityLevel? = null,
    val searchQuery: String = "",
    val isAiGenerating: Boolean = false,
    val aiGenerationError: String? = null,
    val isSaved: Boolean = false,
    val isLiveScanning: Boolean = false,
    val currentSlideIndex: Int = 0,
    val isPresenterModeOpen: Boolean = false,
    val valuationCalculator: ValuationCalculatorState = ValuationCalculatorState(),
    val savedVentures: List<VentureEntity> = emptyList(),
    val showAiPromptDialog: Boolean = false,
    val exportSnackbarMessage: String? = null,
    val marketFeed: RealtimeMarketDataFeed? = null,
    val comparativeRatios: ComparativeEfficiencyRatios? = null,
    val isMarketFeedRefreshing: Boolean = false,
    val isPdfPreviewVisible: Boolean = false,
    val isCognitiveLoadBriefingVisible: Boolean = false,
    val syncInfo: SyncInfo = SyncInfo(),

    // --- AI Studio Hub State ---
    val isAiStudioHubOpen: Boolean = false,
    val aiStudioActiveTab: AiStudioTab = AiStudioTab.HIGH_THINKING,

    // High Thinking State (gemini-3.1-pro-preview, thinkingLevel = HIGH)
    val highThinkingQuery: String = "",
    val isHighThinkingRunning: Boolean = false,
    val highThinkingResult: HighThinkingAuditResult? = null,
    val highThinkingError: String? = null,

    // Image Studio State (gemini-3.1-flash-image-preview)
    val imagePrompt: String = "",
    val selectedImageAspectRatio: ImageAspectRatio = ImageAspectRatio.SQUARE,
    val isImageGenerating: Boolean = false,
    val generatedImages: List<GeneratedAiImage> = emptyList(),
    val selectedImageForEdit: GeneratedAiImage? = null,
    val imageGenerationError: String? = null,

    // Veo 3 Video State (veo-3.1-fast-generate-preview, 16:9 / 9:16)
    val veoPrompt: String = "",
    val selectedVeoAspectRatio: VeoVideoAspectRatio = VeoVideoAspectRatio.LANDSCAPE_16_9,
    val selectedVeoResolution: VeoResolution = VeoResolution.RES_1080P,
    val isVeoGenerating: Boolean = false,
    val generatedVideos: List<GeneratedVeoVideo> = emptyList(),
    val selectedVeoVideo: GeneratedVeoVideo? = null,
    val veoGenerationError: String? = null
)
