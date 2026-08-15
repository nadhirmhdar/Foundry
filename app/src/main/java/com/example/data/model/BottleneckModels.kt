package com.example.data.model

enum class BottleneckDomain(val label: String, val badgeColorHex: Long) {
    ERP_LOGIC("Major ERP Logic Flaws", 0xFF00BBF9),
    BPA_FRICTION("Process Automation Gaps", 0xFF7209B7),
    HUMAN_QC_LIMIT("QC & Human Limits", 0xFFEF476F),
    CROSS_INDUSTRY("Cross-Industry Frontier", 0xFF00F5D4)
}

enum class SeverityLevel(val label: String, val colorHex: Long) {
    CRITICAL("Critical Bottleneck", 0xFFEF476F),
    HIGH("High Value Opportunity", 0xFFFB8500),
    MEDIUM("Optimized Frontier", 0xFFFFB703)
}

enum class ProblemScope(val label: String, val description: String) {
    MICRO_FRICTION("Micro-Friction", "Targeted low-friction unbundled workflow problem (<48hr wedge)"),
    MODULAR_BOTTLENECK("Modular Bottleneck", "Subsystem integration or batch scheduling failure"),
    SYSTEMIC_MACRO("Systemic Enterprise", "Multi-facility or end-to-end supply-chain synchronization")
}

data class EndpointVerificationSource(
    val primarySystemDoc: String, // e.g. "SAP S/4HANA OData API / A_ProductionOrder2"
    val verifiedEndpointUrl: String, // Legitimate documentation / standard endpoint
    val secondaryValidationMethod: String, // e.g. "Direct OData telemetry schema scrape & transaction trace analysis"
    val verificationAuditTimestamp: String,
    val auditConfidenceScore: Double // e.g. 98.4%
)

data class ErpBottleneck(
    val id: String,
    val title: String,
    val domain: BottleneckDomain,
    val severity: SeverityLevel,
    val problemScope: ProblemScope = ProblemScope.MODULAR_BOTTLENECK,
    val affectedErpSystems: List<String>, // e.g. ["SAP S/4HANA", "Oracle Cloud ERP", "Microsoft Dynamics 365"]
    val targetIndustry: String, // e.g. "Semiconductor & High-Precision Fab", "Automotive Tier 1", "Pharma Batching"
    val traditionalMethod: String, // How it is done today
    val traditionalFlaw: String, // Why current ERP / human methods break down
    val frontierLogic: String, // Modern breakthrough algorithm or architecture
    val adoptionFriction: String, // Why enterprises haven't transitioned yet
    val annualIndustryWasteMillions: Double, // Enterprise loss in $M
    val potentialEfficiencyGainPercent: Double, // Gain % (e.g. 42.5%)
    val suggestedVentureIdea: StartupVenture,
    val verificationSource: EndpointVerificationSource? = null
)
