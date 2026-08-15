package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.PdfReportViewerDialog
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.NavigationTab

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ProcessFoundryTheme {
                ProcessFoundryApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProcessFoundryApp(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.exportSnackbarMessage) {
        uiState.exportSnackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    val onSharePitchDeckReport: () -> Unit = {
        val reportText = viewModel.generatePitchDeckMarkdown()
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, reportText)
            putExtra(Intent.EXTRA_TITLE, "ProcessFoundry Venture Pitch & Valuation Report")
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Export Venture Pitch Report")
        context.startActivity(shareIntent)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = SophisticatedDarkBg,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(SophisticatedSurface, RoundedCornerShape(12.dp))
                                .padding(1.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                shape = RoundedCornerShape(12.dp),
                                color = SophisticatedSurface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, SophisticatedBorder)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "◈",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = SophisticatedLavender,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "ProcessFoundry",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        letterSpacing = (-0.2).sp,
                                        color = SophisticatedTextPrimary
                                    )
                                )
                                Surface(
                                    shape = RoundedCornerShape(100.dp),
                                    color = SophisticatedSurfaceVariant,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, SophisticatedBorder)
                                ) {
                                    Text(
                                        text = "LOGIC AI",
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.5.sp,
                                            color = SophisticatedLavender
                                        )
                                    )
                                }
                            }

                            Text(
                                text = "Enterprise Logic & Valuation Studio",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SophisticatedTextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                },
                actions = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        // PDF Investment Memo Preview
                        Surface(
                            shape = CircleShape,
                            color = SophisticatedSurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, SophisticatedBorderSubtle)
                        ) {
                            IconButton(
                                onClick = { viewModel.setPdfPreviewVisible(true) },
                                modifier = Modifier
                                    .size(38.dp)
                                    .testTag("top_pdf_preview_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PictureAsPdf,
                                    contentDescription = "PDF Memo Preview",
                                    tint = SophisticatedLavender,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // AI Diagnostic Trigger
                        Surface(
                            shape = CircleShape,
                            color = SophisticatedSurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, SophisticatedBorderSubtle)
                        ) {
                            IconButton(
                                onClick = { viewModel.showAiPromptDialog(true) },
                                modifier = Modifier
                                    .size(38.dp)
                                    .testTag("ai_diagnostic_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "AI Diagnostic",
                                    tint = SophisticatedLavender,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SophisticatedDarkBg,
                    titleContentColor = SophisticatedTextPrimary
                )
            )
        },
        bottomBar = {
            Surface(
                color = SophisticatedSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, SophisticatedBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                NavigationBar(
                    containerColor = SophisticatedSurface,
                    tonalElevation = 0.dp
                ) {
                    NavigationTab.values().forEach { tab ->
                        val isSelected = uiState.selectedTab == tab
                        val iconVector = when (tab) {
                            NavigationTab.SCANNER -> if (isSelected) Icons.Filled.Sensors else Icons.Outlined.Sensors
                            NavigationTab.ARCHITECT -> if (isSelected) Icons.Filled.AccountTree else Icons.Outlined.AccountTree
                            NavigationTab.PITCH_DECK -> if (isSelected) Icons.Filled.CoPresent else Icons.Outlined.CoPresent
                            NavigationTab.VALUATION -> if (isSelected) Icons.Filled.MonetizationOn else Icons.Outlined.MonetizationOn
                            NavigationTab.VAULT -> if (isSelected) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder
                        }

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.selectTab(tab) },
                            modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}"),
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (tab == NavigationTab.VAULT && uiState.savedVentures.isNotEmpty()) {
                                            Badge(
                                                containerColor = SophisticatedLavender,
                                                contentColor = SophisticatedLavenderDark
                                            ) {
                                                Text("${uiState.savedVentures.size}")
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = iconVector,
                                        contentDescription = tab.label
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = tab.label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = SophisticatedLavender,
                                selectedTextColor = SophisticatedLavender,
                                unselectedIconColor = SophisticatedTextSecondary,
                                unselectedTextColor = SophisticatedTextSecondary,
                                indicatorColor = SophisticatedActivePill
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.selectedTab) {
                NavigationTab.SCANNER -> {
                    ScannerScreen(
                        bottlenecks = uiState.bottlenecks,
                        selectedBottleneck = uiState.selectedBottleneck,
                        selectedDomain = uiState.selectedDomainFilter,
                        searchQuery = uiState.searchQuery,
                        isLiveScanning = uiState.isLiveScanning,
                        onSelectBottleneck = { viewModel.selectBottleneck(it) },
                        onDomainFilterChange = { viewModel.setDomainFilter(it) },
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onToggleLiveScanner = { viewModel.toggleLiveScanner() },
                        onOpenAiDiagnosis = { viewModel.showAiPromptDialog(true) },
                        onNavigateToArchitect = { viewModel.selectTab(NavigationTab.ARCHITECT) },
                        marketFeed = uiState.marketFeed,
                        comparativeRatios = uiState.comparativeRatios,
                        isMarketFeedRefreshing = uiState.isMarketFeedRefreshing,
                        onRefreshMarketBenchmarks = { viewModel.refreshMarketBenchmarks() },
                        onOpenPdfPreview = { viewModel.setPdfPreviewVisible(true) }
                    )
                }
                NavigationTab.ARCHITECT -> {
                    VentureArchitectScreen(
                        bottleneck = uiState.selectedBottleneck,
                        isSaved = uiState.isSaved,
                        onToggleSave = { viewModel.toggleSaveVenture() },
                        onNavigateToPitchDeck = { viewModel.selectTab(NavigationTab.PITCH_DECK) },
                        onNavigateToValuation = { viewModel.selectTab(NavigationTab.VALUATION) },
                        onCopyMarkdown = onSharePitchDeckReport,
                        onOpenAiDiagnosis = { viewModel.showAiPromptDialog(true) },
                        onOpenPdfPreview = { viewModel.setPdfPreviewVisible(true) }
                    )
                }
                NavigationTab.PITCH_DECK -> {
                    PitchDeckStudioScreen(
                        bottleneck = uiState.selectedBottleneck,
                        currentSlideIndex = uiState.currentSlideIndex,
                        isPresenterModeOpen = uiState.isPresenterModeOpen,
                        onSelectSlideIndex = { viewModel.setSlideIndex(it) },
                        onNextSlide = { viewModel.nextSlide() },
                        onPreviousSlide = { viewModel.previousSlide() },
                        onTogglePresenterMode = { viewModel.setPresenterMode(it) },
                        onExportMarkdown = onSharePitchDeckReport,
                        onOpenPdfPreview = { viewModel.setPdfPreviewVisible(true) }
                    )
                }
                NavigationTab.VALUATION -> {
                    ValuationReportScreen(
                        bottleneck = uiState.selectedBottleneck,
                        calculatorState = uiState.valuationCalculator,
                        onUpdateAcv = { viewModel.updateValuationAcv(it) },
                        onUpdateCac = { viewModel.updateValuationCac(it) },
                        onUpdateYear3Arr = { viewModel.updateValuationYear3Arr(it) },
                        onUpdateMultiple = { viewModel.updateValuationMultiple(it) },
                        onUpdateRaise = { viewModel.updateValuationRaise(it) },
                        onExportReport = onSharePitchDeckReport,
                        marketFeed = uiState.marketFeed,
                        comparativeRatios = uiState.comparativeRatios,
                        isMarketFeedRefreshing = uiState.isMarketFeedRefreshing,
                        onRefreshMarketBenchmarks = { viewModel.refreshMarketBenchmarks() },
                        onOpenPdfPreview = { viewModel.setPdfPreviewVisible(true) }
                    )
                }
                NavigationTab.VAULT -> {
                    VaultScreen(
                        savedVentures = uiState.savedVentures,
                        bottlenecks = uiState.bottlenecks,
                        onSelectVenture = {
                            viewModel.selectBottleneck(it)
                            viewModel.selectTab(NavigationTab.ARCHITECT)
                        },
                        onDeleteVenture = { viewModel.deleteSavedVentureById(it) },
                        onNavigateToScanner = { viewModel.selectTab(NavigationTab.SCANNER) }
                    )
                }
            }
        }

        // PDF Institutional Report Viewer Dialog
        if (uiState.isPdfPreviewVisible && uiState.selectedBottleneck != null) {
            PdfReportViewerDialog(
                bottleneck = uiState.selectedBottleneck!!,
                marketFeed = uiState.marketFeed,
                onDismiss = { viewModel.setPdfPreviewVisible(false) }
            )
        }

        // Gemini AI Custom Diagnosis Dialog
        AiCustomAnalysisDialog(
            isOpen = uiState.showAiPromptDialog,
            isLoading = uiState.isAiGenerating,
            errorMessage = uiState.aiGenerationError,
            onDismiss = { viewModel.showAiPromptDialog(false) },
            onSubmitPrompt = { prompt -> viewModel.runAiDiagnosis(prompt) }
        )
    }
}
