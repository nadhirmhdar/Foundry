package com.example.ui.viewmodel

import com.example.data.local.VentureEntity
import com.example.data.model.*

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
    val isPdfPreviewVisible: Boolean = false
)
