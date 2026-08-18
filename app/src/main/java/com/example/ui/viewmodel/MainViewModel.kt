package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.AiStudioMediaRepository
import com.example.data.repository.IntelligenceRepository
import com.example.data.repository.MarketDataRepository
import com.example.data.sync.FirestoreSyncService
import com.example.data.sync.SyncStatus
import com.example.ui.screens.AiStudioTab
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val firestoreSyncService: FirestoreSyncService
    private val repository: IntelligenceRepository
    private val marketDataRepository: MarketDataRepository = MarketDataRepository()
    private val aiStudioMediaRepository: AiStudioMediaRepository = AiStudioMediaRepository()

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        firestoreSyncService = FirestoreSyncService(database.ventureDao(), viewModelScope)
        repository = IntelligenceRepository(database.ventureDao(), firestoreSyncService)

        val initialBottlenecks = repository.curatedBottlenecks
        val initialSelected = initialBottlenecks.firstOrNull()

        val initialMarketFeed = marketDataRepository.marketFeedState.value
        val initialComparativeRatios = initialSelected?.let {
            marketDataRepository.computeComparativeRatios(it)
        }

        _uiState.update { state ->
            state.copy(
                bottlenecks = initialBottlenecks,
                selectedBottleneck = initialSelected,
                marketFeed = initialMarketFeed,
                comparativeRatios = initialComparativeRatios,
                valuationCalculator = initialSelected?.let {
                    ValuationCalculatorState(
                        customAcvThousands = it.suggestedVentureIdea.valuationReport.unitEconomics.targetEnterpriseAcvThousands,
                        customCacThousands = it.suggestedVentureIdea.valuationReport.unitEconomics.customerAcquisitionCostThousands,
                        customYear3ArrMillions = it.suggestedVentureIdea.valuationReport.fiveYearFinancials.getOrNull(2)?.arrMillions ?: 12.8,
                        customExitMultiple = 12.0,
                        customTargetRaiseMillions = it.suggestedVentureIdea.pitchDeck.targetRaiseAmountMillions
                    )
                } ?: ValuationCalculatorState()
            )
        }

        observeSavedVentures()
        observeMarketData()
        observeSyncStatus()

        // Fetch live market data in background on launch
        viewModelScope.launch {
            marketDataRepository.refreshMarketData()
        }

        // Initialize Cloud Firestore synchronization and real-time observer
        firestoreSyncService.startRealtimeListener()
        viewModelScope.launch {
            firestoreSyncService.syncAll()
        }
    }

    private fun observeSyncStatus() {
        viewModelScope.launch {
            firestoreSyncService.syncInfo.collect { syncInfo ->
                _uiState.update { it.copy(syncInfo = syncInfo) }
            }
        }
    }

    private fun observeSavedVentures() {
        viewModelScope.launch {
            repository.savedVentures.collect { savedList ->
                _uiState.update { state ->
                    val isCurrentSaved = state.selectedBottleneck?.let { b ->
                        savedList.any { it.id == b.suggestedVentureIdea.id }
                    } ?: false
                    state.copy(savedVentures = savedList, isSaved = isCurrentSaved)
                }
            }
        }
    }

    private fun observeMarketData() {
        viewModelScope.launch {
            combine(
                marketDataRepository.marketFeedState,
                marketDataRepository.isRefreshing
            ) { feed, isRefreshing ->
                Pair(feed, isRefreshing)
            }.collect { (feed, isRefreshing) ->
                _uiState.update { state ->
                    val updatedRatios = state.selectedBottleneck?.let {
                        marketDataRepository.computeComparativeRatios(it)
                    }
                    state.copy(
                        marketFeed = feed,
                        isMarketFeedRefreshing = isRefreshing,
                        comparativeRatios = updatedRatios
                    )
                }
            }
        }
    }

    fun refreshMarketBenchmarks() {
        viewModelScope.launch {
            marketDataRepository.refreshMarketData()
            _uiState.update {
                it.copy(exportSnackbarMessage = "Refreshed Live Market Benchmarks & Ratios")
            }
        }
    }

    fun setPdfPreviewVisible(visible: Boolean) {
        _uiState.update { it.copy(isPdfPreviewVisible = visible) }
    }

    fun setCognitiveLoadBriefingVisible(visible: Boolean) {
        _uiState.update { it.copy(isCognitiveLoadBriefingVisible = visible) }
    }

    // --- AI Studio Hub Controls ---

    fun openAiStudioHub(tab: AiStudioTab = AiStudioTab.HIGH_THINKING) {
        val currentBottleneck = _uiState.value.selectedBottleneck
        val ventureName = currentBottleneck?.suggestedVentureIdea?.name ?: "ProcessFoundry"

        _uiState.update {
            it.copy(
                isAiStudioHubOpen = true,
                aiStudioActiveTab = tab,
                highThinkingQuery = if (it.highThinkingQuery.isBlank())
                    "Conduct invariant audit on $ventureName CDC ingestion pipeline during peak quarterly ERP posting"
                else it.highThinkingQuery,
                imagePrompt = if (it.imagePrompt.isBlank())
                    "3D dark cybernetic CAD diagram showing $ventureName zero-latency non-invasive ERP sidecar proxy"
                else it.imagePrompt,
                veoPrompt = if (it.veoPrompt.isBlank())
                    "Cinematic 3D animation of autonomous shopfloor robotic routing and closed-loop ERP reconciliation for $ventureName"
                else it.veoPrompt
            )
        }
    }

    fun closeAiStudioHub() {
        _uiState.update { it.copy(isAiStudioHubOpen = false) }
    }

    fun setAiStudioTab(tab: AiStudioTab) {
        _uiState.update { it.copy(aiStudioActiveTab = tab) }
    }

    // 1. High Thinking Mode (gemini-3.1-pro-preview with thinkingLevel = HIGH)
    fun setHighThinkingQuery(query: String) {
        _uiState.update { it.copy(highThinkingQuery = query) }
    }

    fun runHighThinkingAudit(query: String) {
        if (query.isBlank()) return
        _uiState.update { it.copy(isHighThinkingRunning = true, highThinkingError = null) }

        viewModelScope.launch {
            val bottleneck = _uiState.value.selectedBottleneck
            val result = aiStudioMediaRepository.runHighThinkingAudit(query, bottleneck)
            result.onSuccess { audit ->
                _uiState.update {
                    it.copy(
                        isHighThinkingRunning = false,
                        highThinkingResult = audit,
                        exportSnackbarMessage = "High Thinking Reasoning Audit Completed"
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isHighThinkingRunning = false,
                        highThinkingError = err.message ?: "Thinking process encountered an error"
                    )
                }
            }
        }
    }

    // 2. Image Studio (gemini-3.1-flash-image-preview - Create & Edit)
    fun setImagePrompt(prompt: String) {
        _uiState.update { it.copy(imagePrompt = prompt) }
    }

    fun setImageAspectRatio(ratio: ImageAspectRatio) {
        _uiState.update { it.copy(selectedImageAspectRatio = ratio) }
    }

    fun setSelectedImageForEdit(image: GeneratedAiImage?) {
        _uiState.update {
            it.copy(
                selectedImageForEdit = image,
                imagePrompt = if (image != null) "Add thermal imaging color map overlay and highlight micro-fractures in neon amber" else it.imagePrompt
            )
        }
    }

    fun generateOrEditImage(prompt: String, baseBitmap: Bitmap?, aspectRatio: ImageAspectRatio) {
        if (prompt.isBlank()) return
        _uiState.update { it.copy(isImageGenerating = true, imageGenerationError = null) }

        viewModelScope.launch {
            val result = aiStudioMediaRepository.generateOrEditImage(prompt, baseBitmap, aspectRatio)
            result.onSuccess { newImage ->
                _uiState.update {
                    it.copy(
                        isImageGenerating = false,
                        generatedImages = listOf(newImage) + it.generatedImages,
                        selectedImageForEdit = null,
                        exportSnackbarMessage = if (baseBitmap != null) "Image Edited with Gemini 3.1 Flash Image" else "Image Generated with Gemini 3.1 Flash Image"
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isImageGenerating = false,
                        imageGenerationError = err.message ?: "Image generation failed"
                    )
                }
            }
        }
    }

    // 3. Veo 3 Video Studio (veo-3.1-fast-generate-preview - 16:9 or 9:16)
    fun setVeoPrompt(prompt: String) {
        _uiState.update { it.copy(veoPrompt = prompt) }
    }

    fun setVeoAspectRatio(aspectRatio: VeoVideoAspectRatio) {
        _uiState.update { it.copy(selectedVeoAspectRatio = aspectRatio) }
    }

    fun setVeoResolution(resolution: VeoResolution) {
        _uiState.update { it.copy(selectedVeoResolution = resolution) }
    }

    fun setSelectedVeoVideo(video: GeneratedVeoVideo?) {
        _uiState.update { it.copy(selectedVeoVideo = video) }
    }

    fun generateVeoVideo(prompt: String, aspectRatio: VeoVideoAspectRatio, resolution: VeoResolution) {
        if (prompt.isBlank()) return
        _uiState.update { it.copy(isVeoGenerating = true, veoGenerationError = null) }

        viewModelScope.launch {
            val result = aiStudioMediaRepository.generateVeoVideo(prompt, aspectRatio, resolution)
            result.onSuccess { video ->
                _uiState.update {
                    it.copy(
                        isVeoGenerating = false,
                        generatedVideos = listOf(video) + it.generatedVideos,
                        selectedVeoVideo = video,
                        exportSnackbarMessage = "Veo 3 Video Generated (${aspectRatio.apiValue})"
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isVeoGenerating = false,
                        veoGenerationError = err.message ?: "Video generation failed"
                    )
                }
            }
        }
    }

    // --- Standard Navigation & Actions ---

    fun selectTab(tab: NavigationTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun selectBottleneck(bottleneck: ErpBottleneck) {
        val venture = bottleneck.suggestedVentureIdea
        val isSaved = _uiState.value.savedVentures.any { it.id == venture.id }
        val ratios = marketDataRepository.computeComparativeRatios(bottleneck)

        _uiState.update {
            it.copy(
                selectedBottleneck = bottleneck,
                isSaved = isSaved,
                currentSlideIndex = 0,
                comparativeRatios = ratios,
                valuationCalculator = ValuationCalculatorState(
                    customAcvThousands = venture.valuationReport.unitEconomics.targetEnterpriseAcvThousands,
                    customCacThousands = venture.valuationReport.unitEconomics.customerAcquisitionCostThousands,
                    customYear3ArrMillions = venture.valuationReport.fiveYearFinancials.getOrNull(2)?.arrMillions ?: 12.8,
                    customExitMultiple = 12.0,
                    customTargetRaiseMillions = venture.pitchDeck.targetRaiseAmountMillions
                )
            )
        }
    }

    fun setDomainFilter(domain: BottleneckDomain?) {
        _uiState.update { it.copy(selectedDomainFilter = domain) }
    }

    fun setDepartmentFilter(department: String?) {
        _uiState.update { it.copy(selectedDepartmentFilter = department) }
    }

    fun setPriorityFilter(priority: SeverityLevel?) {
        _uiState.update { it.copy(selectedPriorityFilter = priority) }
    }

    fun resetAllFilters() {
        _uiState.update {
            it.copy(
                searchQuery = "",
                selectedDomainFilter = null,
                selectedDepartmentFilter = null,
                selectedPriorityFilter = null
            )
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun toggleLiveScanner() {
        val current = _uiState.value.isLiveScanning
        _uiState.update { it.copy(isLiveScanning = !current) }
        if (!current) {
            viewModelScope.launch {
                repeat(4) {
                    delay(3500)
                    if (!_uiState.value.isLiveScanning) return@launch
                }
            }
        }
    }

    fun setSlideIndex(index: Int) {
        val maxIndex = (_uiState.value.selectedBottleneck?.suggestedVentureIdea?.pitchDeck?.slides?.size ?: 1) - 1
        _uiState.update { it.copy(currentSlideIndex = index.coerceIn(0, maxIndex.coerceAtLeast(0))) }
    }

    fun nextSlide() {
        val current = _uiState.value.currentSlideIndex
        setSlideIndex(current + 1)
    }

    fun previousSlide() {
        val current = _uiState.value.currentSlideIndex
        setSlideIndex(current - 1)
    }

    fun setPresenterMode(open: Boolean) {
        _uiState.update { it.copy(isPresenterModeOpen = open) }
    }

    fun updateValuationAcv(acv: Double) {
        _uiState.update {
            it.copy(valuationCalculator = it.valuationCalculator.copy(customAcvThousands = acv))
        }
    }

    fun updateValuationCac(cac: Double) {
        _uiState.update {
            it.copy(valuationCalculator = it.valuationCalculator.copy(customCacThousands = cac))
        }
    }

    fun updateValuationYear3Arr(arr: Double) {
        _uiState.update {
            it.copy(valuationCalculator = it.valuationCalculator.copy(customYear3ArrMillions = arr))
        }
    }

    fun updateValuationMultiple(multiple: Double) {
        _uiState.update {
            it.copy(valuationCalculator = it.valuationCalculator.copy(customExitMultiple = multiple))
        }
    }

    fun updateValuationRaise(raise: Double) {
        _uiState.update {
            it.copy(valuationCalculator = it.valuationCalculator.copy(customTargetRaiseMillions = raise))
        }
    }

    fun toggleSaveVenture() {
        val bottleneck = _uiState.value.selectedBottleneck ?: return
        val venture = bottleneck.suggestedVentureIdea
        viewModelScope.launch {
            if (_uiState.value.isSaved) {
                repository.deleteSavedVenture(venture.id)
                _uiState.update {
                    it.copy(
                        isSaved = false,
                        exportSnackbarMessage = "Removed ${venture.name} from Vault"
                    )
                }
            } else {
                repository.saveVenture(venture, bottleneck)
                _uiState.update {
                    it.copy(
                        isSaved = true,
                        exportSnackbarMessage = "Saved ${venture.name} to Founder Vault"
                    )
                }
            }
        }
    }

    fun deleteSavedVentureById(ventureId: String) {
        viewModelScope.launch {
            repository.deleteSavedVenture(ventureId)
            _uiState.update {
                val isCurrent = it.selectedBottleneck?.suggestedVentureIdea?.id == ventureId
                it.copy(
                    isSaved = if (isCurrent) false else it.isSaved,
                    exportSnackbarMessage = "Deleted from Vault"
                )
            }
        }
    }

    fun showAiPromptDialog(show: Boolean) {
        _uiState.update { it.copy(showAiPromptDialog = show, aiGenerationError = null) }
    }

    fun runAiDiagnosis(prompt: String) {
        if (prompt.isBlank()) return
        _uiState.update { it.copy(isAiGenerating = true, aiGenerationError = null) }

        viewModelScope.launch {
            val result = repository.runAiVentureDiagnosis(prompt)
            result.onSuccess { newBottleneck ->
                val updatedList = listOf(newBottleneck) + _uiState.value.bottlenecks
                val ratios = marketDataRepository.computeComparativeRatios(newBottleneck)
                _uiState.update {
                    it.copy(
                        bottlenecks = updatedList,
                        selectedBottleneck = newBottleneck,
                        comparativeRatios = ratios,
                        isAiGenerating = false,
                        showAiPromptDialog = false,
                        selectedTab = NavigationTab.ARCHITECT,
                        currentSlideIndex = 0,
                        exportSnackbarMessage = "Architected Venture: ${newBottleneck.suggestedVentureIdea.name}"
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isAiGenerating = false,
                        aiGenerationError = error.message ?: "Diagnosis failed. Please try again."
                    )
                }
            }
        }
    }

    fun clearSnackbar() {
        _uiState.update { it.copy(exportSnackbarMessage = null) }
    }

    fun generatePitchDeckMarkdown(): String {
        val bottleneck = _uiState.value.selectedBottleneck ?: return ""
        val venture = bottleneck.suggestedVentureIdea
        val deck = venture.pitchDeck
        val valuation = venture.valuationReport

        val sb = StringBuilder()
        sb.appendLine("# ${deck.title}")
        sb.appendLine("### ${deck.subtitle}")
        sb.appendLine("**Category:** ${venture.category} | **Target Raise:** $${deck.targetRaiseAmountMillions}M (${deck.fundingStage})")
        sb.appendLine("**Target ICP:** ${venture.targetIcp}")
        sb.appendLine("**Core Moat:** ${venture.coreMoat}")
        sb.appendLine("**Friction Bypass Strategy:** ${venture.frictionBypassStrategy}")
        sb.appendLine()
        sb.appendLine("---")
        sb.appendLine()
        sb.appendLine("## 1. Enterprise Bottleneck & Logic Breakdown")
        sb.appendLine("- **Affected ERPs:** ${bottleneck.affectedErpSystems.joinToString(", ")}")
        sb.appendLine("- **Traditional Approach:** ${bottleneck.traditionalMethod}")
        sb.appendLine("- **Core Flaw:** ${bottleneck.traditionalFlaw}")
        sb.appendLine("- **Frontier Logic:** ${bottleneck.frontierLogic}")
        sb.appendLine("- **Adoption Friction:** ${bottleneck.adoptionFriction}")
        sb.appendLine("- **Annual Industry Loss:** $${bottleneck.annualIndustryWasteMillions}M / yr")
        sb.appendLine("- **Efficiency Gain Potential:** +${bottleneck.potentialEfficiencyGainPercent}%")
        sb.appendLine()
        sb.appendLine("## 2. 3-Tier System Architecture")
        venture.architectureSteps.forEachIndexed { i, step ->
            sb.appendLine("### Layer ${i + 1}: ${step.layerName}")
            sb.appendLine("- **Tech Stack:** ${step.techStack}")
            sb.appendLine("- **Description:** ${step.description}")
        }
        sb.appendLine()
        sb.appendLine("## 3. Valuation & Pro-Forma Economics")
        sb.appendLine("- **Year 3 Projected Valuation:** $${valuation.year3ProjectedValuationMillions}M (at 12.0x Multiple)")
        sb.appendLine("- **LTV/CAC Ratio:** ${valuation.unitEconomics.ltvToCacRatio}x")
        sb.appendLine("- **CAC Payback:** ${valuation.unitEconomics.paybackPeriodMonths} Months")
        sb.appendLine("- **Net Revenue Retention:** ${valuation.unitEconomics.netRevenueRetentionPercent}%")
        sb.appendLine("- **Client Annual Savings:** $${valuation.customerRoi.annualClientCostSavingsMillions}M (${valuation.customerRoi.enterpriseRoiMultiple}x Enterprise ROI)")
        sb.appendLine()
        sb.appendLine("Generated by ProcessFoundry ERP Innovation & Pitch Engine")
        sb.appendLine()
        sb.appendLine("---")
        sb.appendLine()
        sb.appendLine("## 4. Synthesized Pitch Deck Architect Blueprint")
        val blueprint = com.example.data.repository.PitchDeckArchitectSynthesizer.synthesizeBlueprint(bottleneck)
        blueprint.allSections.forEach { section ->
            sb.appendLine("### ${section.pillar.title}: ${section.headline}")
            sb.appendLine("*${section.subheadline}*")
            sb.appendLine()
            sb.appendLine("**Executive Synthesis:**")
            sb.appendLine(section.executiveSummary)
            sb.appendLine()
            section.items.forEach { item ->
                val metric = if (item.metricValue != null) " [${item.metricValue} - ${item.metricLabel ?: ""}]" else ""
                sb.appendLine("- **${item.title}**$metric: ${item.narrative}")
            }
            sb.appendLine()
            sb.appendLine("> **Strategic Takeaway:** ${section.strategicTakeaway}")
            sb.appendLine()
        }
        return sb.toString()
    }

    fun syncWithFirestore() {
        viewModelScope.launch {
            firestoreSyncService.syncAll().onSuccess { count ->
                _uiState.update {
                    it.copy(exportSnackbarMessage = "Cloud Sync Complete: $count models synchronized")
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(exportSnackbarMessage = "Sync offline: Using local Room database")
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        firestoreSyncService.stopRealtimeListener()
    }
}
