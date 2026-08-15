package com.example.data.model

data class ArchitectureStep(
    val stepNumber: Int,
    val layerName: String,
    val description: String,
    val techStack: String
)

data class StartupVenture(
    val id: String,
    val name: String,
    val tagline: String,
    val category: String, // e.g. "Enterprise Autonomous MES", "Edge Vision AI for Zero-Defect Stamping"
    val oneSentencePitch: String,
    val coreMoat: String,
    val architectureSteps: List<ArchitectureStep>,
    val targetIcp: String, // Ideal Customer Profile
    val beachheadMarket: String,
    val frictionBypassStrategy: String, // How startup bypasses ERP migration resistance
    val pitchDeck: PitchDeck,
    val valuationReport: ValuationReport
)
