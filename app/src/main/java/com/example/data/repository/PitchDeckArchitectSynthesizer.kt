package com.example.data.repository

import com.example.data.model.*

object PitchDeckArchitectSynthesizer {

    fun synthesizeBlueprint(
        bottleneck: ErpBottleneck,
        perspective: PitchPerspective = PitchPerspective.VENTURE_CAPITAL
    ): SynthesizedPitchBlueprint {
        val venture = bottleneck.suggestedVentureIdea
        val valuation = venture.valuationReport
        val deck = venture.pitchDeck

        val problemSection = buildProblemSection(bottleneck, venture, perspective)
        val solutionSection = buildSolutionSection(bottleneck, venture, perspective)
        val marketSection = buildMarketSection(bottleneck, venture, valuation, perspective)
        val financialsSection = buildFinancialsSection(bottleneck, venture, valuation, deck, perspective)

        return SynthesizedPitchBlueprint(
            ventureName = venture.name,
            tagline = venture.tagline,
            targetRaise = "$${deck.targetRaiseAmountMillions}M (${deck.fundingStage})",
            perspective = perspective,
            problemSection = problemSection,
            solutionSection = solutionSection,
            marketSection = marketSection,
            financialsSection = financialsSection
        )
    }

    private fun buildProblemSection(
        bottleneck: ErpBottleneck,
        venture: StartupVenture,
        perspective: PitchPerspective
    ): PitchDeckArchitectSection {
        val valuation = venture.valuationReport
        val headline = when (perspective) {
            PitchPerspective.VENTURE_CAPITAL -> "Mission-Critical Operational Paralysis in Legacy ERP Stacks"
            PitchPerspective.ENTERPRISE_BUYER -> "Escalating Process Inefficiencies & Millions in Annual Waste"
            PitchPerspective.EXECUTIVE_BOARD -> "Systemic ERP Execution Bottleneck & Operational Vulnerability"
        }

        val subheadline = "Target Systems: ${bottleneck.affectedErpSystems.joinToString(", ")}"

        val executiveSummary = when (perspective) {
            PitchPerspective.VENTURE_CAPITAL ->
                "Tier-1 enterprises in ${bottleneck.domain.label} remain constrained by archaic ${bottleneck.affectedErpSystems.firstOrNull() ?: "ERP"} architectures designed in the batch-processing era. Traditional heuristic algorithms cannot adapt to real-time production variance, burning \$${bottleneck.annualIndustryWasteMillions}M annually in idle capital, scrapped output, and compliance overhead. This structural flaw creates an urgent imperative for an autonomous intelligence replacement layer."
            PitchPerspective.ENTERPRISE_BUYER ->
                "Current enterprise workflows relying on ${bottleneck.traditionalMethod} cause chronic scheduling delays, human error in sign-offs, and unacceptable cycle times. By operating on disconnected batch cycles, operations bleed roughly \$${valuation.customerRoi.annualClientCostSavingsMillions}M per facility each year while overburdening plant managers and compliance officers with manual triage."
            PitchPerspective.EXECUTIVE_BOARD ->
                "The organization faces structural friction at the intersection of ${bottleneck.affectedErpSystems.joinToString(" and ")}. The underlying algorithmic flaw—${bottleneck.traditionalFlaw}—imposes severe velocity ceilings, elevating operational risk and preventing leadership from achieving SLA guarantees."
        }

        val items = listOf(
            PitchSectionItem(
                title = "The Underlying Algorithmic Flaw",
                narrative = bottleneck.traditionalFlaw,
                metricValue = "${bottleneck.affectedErpSystems.size} Systems",
                metricLabel = "Compromised ERP Platforms",
                tag = "ROOT CAUSE"
            ),
            PitchSectionItem(
                title = "Quantified Annual Industry Waste",
                narrative = "Direct economic destruction from scrap, idle machine capacity, inventory carrying distortion, and manual reconciliation.",
                metricValue = "\$${bottleneck.annualIndustryWasteMillions}M/yr",
                metricLabel = "Direct Industry Loss",
                tag = "FINANCIAL IMPACT"
            ),
            PitchSectionItem(
                title = "Operational Inefficiency & Human QC Limits",
                narrative = "Human operators are forced into manual triage, spreadsheet workarounds, and subjective approvals, introducing severe variance and latency into standard operating procedures.",
                metricValue = "${(100 - bottleneck.potentialEfficiencyGainPercent).toInt()}%",
                metricLabel = "Legacy Process Yield",
                tag = "BOTTLENECK"
            ),
            PitchSectionItem(
                title = "Enterprise Adoption Inertia",
                narrative = bottleneck.adoptionFriction,
                metricValue = "High Friction",
                metricLabel = "Standard Migration Barrier",
                tag = "BUYER HESITATION"
            )
        )

        val strategicTakeaway = "Legacy ERP platforms cannot solve this organically without risking multi-million dollar core disruptions. The enterprise requires an external autonomous engine."

        return PitchDeckArchitectSection(
            pillar = PitchArchitectPillar.PROBLEM,
            headline = headline,
            subheadline = subheadline,
            executiveSummary = executiveSummary,
            items = items,
            strategicTakeaway = strategicTakeaway
        )
    }

    private fun buildSolutionSection(
        bottleneck: ErpBottleneck,
        venture: StartupVenture,
        perspective: PitchPerspective
    ): PitchDeckArchitectSection {
        val headline = when (perspective) {
            PitchPerspective.VENTURE_CAPITAL -> "Frontier Autonomous Intelligence with Zero-Migration Wedge"
            PitchPerspective.ENTERPRISE_BUYER -> "Non-Invasive AI Co-Pilot & Autonomous Orchestration Engine"
            PitchPerspective.EXECUTIVE_BOARD -> "Modernized Process Layer Delivering Immediate Measurable ROI"
        }

        val subheadline = "Frontier Paradigm: ${bottleneck.frontierLogic.take(65)}..."

        val executiveSummary = when (perspective) {
            PitchPerspective.VENTURE_CAPITAL ->
                "${venture.name} deploys an autonomous reasoning layer directly alongside existing ${bottleneck.affectedErpSystems.firstOrNull() ?: "ERP"} installations. Utilizing ${venture.frictionBypassStrategy}, the platform captures telemetry in real time, executes multi-agent dynamic optimization, and injects validated deterministic decisions back into the host system with zero migration downtime."
            PitchPerspective.ENTERPRISE_BUYER ->
                "${venture.name} introduces a turnkey solution that runs in shadow mode during week one, delivering audit-grade suggestions without altering existing ERP databases. Once validated, automated write-backs eliminate manual bottleneck tasks, delivering ${bottleneck.potentialEfficiencyGainPercent}% immediate efficiency uplift within ${venture.valuationReport.customerRoi.implementationTimeWeeks} weeks."
            PitchPerspective.EXECUTIVE_BOARD ->
                "A unified intelligent operational fabric bridging core ERP data with modern machine intelligence. Eliminates ${bottleneck.traditionalFlaw} through deterministic reinforcement learning and closed-loop validation."
        }

        val items = listOf(
            PitchSectionItem(
                title = "Frontier Reasoning Breakthrough",
                narrative = bottleneck.frontierLogic,
                metricValue = "+${bottleneck.potentialEfficiencyGainPercent}%",
                metricLabel = "Efficiency Delta",
                tag = "BREAKTHROUGH"
            ),
            PitchSectionItem(
                title = "Friction-Bypassing Adoption Wedge",
                narrative = venture.frictionBypassStrategy,
                metricValue = "${venture.valuationReport.customerRoi.implementationTimeWeeks} Weeks",
                metricLabel = "Deployment Time",
                tag = "TIME TO VALUE"
            ),
            PitchSectionItem(
                title = "Multi-Tier Architecture & Automated Feedback",
                narrative = "3-tiered pipeline spanning ${venture.architectureSteps.joinToString(" → ") { it.layerName }}, ensuring end-to-end data integrity and verifiable audit trails.",
                metricValue = "${venture.architectureSteps.size} Layers",
                metricLabel = "Engine Architecture",
                tag = "TECH STACK"
            ),
            PitchSectionItem(
                title = "Enterprise Security & Compliance Guardrails",
                narrative = "SOC2 Type II, ISO 27001 compliant with on-premise connector options and strict deterministic policy guardrails to eliminate hallucination risks.",
                metricValue = "100%",
                metricLabel = "Audit Verification",
                tag = "GOVERNANCE"
            )
        )

        val strategicTakeaway = "${venture.name} transforms a multi-million dollar annual bottleneck into a continuous competitive advantage without ripping and replacing the underlying ERP."

        return PitchDeckArchitectSection(
            pillar = PitchArchitectPillar.SOLUTION,
            headline = headline,
            subheadline = subheadline,
            executiveSummary = executiveSummary,
            items = items,
            strategicTakeaway = strategicTakeaway
        )
    }

    private fun buildMarketSection(
        bottleneck: ErpBottleneck,
        venture: StartupVenture,
        valuation: ValuationReport,
        perspective: PitchPerspective
    ): PitchDeckArchitectSection {
        val tam = (bottleneck.annualIndustryWasteMillions * 4.2).toInt()
        val sam = (tam * 0.32).toInt()
        val som = (sam * 0.15).toInt()

        val headline = when (perspective) {
            PitchPerspective.VENTURE_CAPITAL -> "A \$${tam / 1000.0}B Market Ripe for Disruption & Defensible Moats"
            PitchPerspective.ENTERPRISE_BUYER -> "Industry-Wide Validation Across Global Enterprise Leaders"
            PitchPerspective.EXECUTIVE_BOARD -> "Target Market Expansion & Sustainable Competitive Advantage"
        }

        val subheadline = "Ideal Customer Profile: ${venture.targetIcp}"

        val executiveSummary = when (perspective) {
            PitchPerspective.VENTURE_CAPITAL ->
                "The global market for ERP optimization and intelligent automation exceeds \$${tam}M. Starting with the high-urgency beachhead in ${venture.beachheadMarket}, ${venture.name} scales into adjacent industrial verticals via compounding data network effects. As more enterprise plants connect, proprietary workflow graphs create an insurmountable competitive moat against legacy vendors."
            PitchPerspective.ENTERPRISE_BUYER ->
                "Designed specifically for enterprise leaders matching the profile of ${venture.targetIcp}. Proven across top-tier manufacturers and supply chain operators requiring stringent compliance and measurable bottom-line gains."
            PitchPerspective.EXECUTIVE_BOARD ->
                "Initial focus on ${venture.beachheadMarket} provides rapid reference customer acquisition, paving the way for multi-facility enterprise account expansion and high retention."
        }

        val items = listOf(
            PitchSectionItem(
                title = "Total Addressable Market (TAM)",
                narrative = "Global enterprise expenditure on ERP add-ons, workflow modernization, and manual quality assurance remediation across target industry sectors.",
                metricValue = "\$${tam}M",
                metricLabel = "Total TAM",
                tag = "MARKET SIZE"
            ),
            PitchSectionItem(
                title = "Beachhead Market & Initial Target Segment",
                narrative = venture.beachheadMarket,
                metricValue = "\$${som}M",
                metricLabel = "Initial SOM",
                tag = "GO-TO-MARKET"
            ),
            PitchSectionItem(
                title = "Core Defensibility & Proprietary Moat",
                narrative = venture.coreMoat,
                metricValue = "High Moat",
                metricLabel = "Data Switching Cost",
                tag = "DEFENSIBILITY"
            ),
            PitchSectionItem(
                title = "Target ICP & Buying Center",
                narrative = "${venture.targetIcp}. Direct alignment with operational KPIs (OEE, OTIF, Scrap Reduction, Inventory Turns).",
                metricValue = "\$${valuation.unitEconomics.targetEnterpriseAcvThousands.toInt()}k",
                metricLabel = "Target ACV",
                tag = "ICP PROFILE"
            )
        )

        val strategicTakeaway = "Strong data gravity and workflow entanglement create >130% net revenue retention once deployed within the initial enterprise facility."

        return PitchDeckArchitectSection(
            pillar = PitchArchitectPillar.MARKET_OPPORTUNITY,
            headline = headline,
            subheadline = subheadline,
            executiveSummary = executiveSummary,
            items = items,
            strategicTakeaway = strategicTakeaway
        )
    }

    private fun buildFinancialsSection(
        bottleneck: ErpBottleneck,
        venture: StartupVenture,
        valuation: ValuationReport,
        deck: PitchDeck,
        perspective: PitchPerspective
    ): PitchDeckArchitectSection {
        val y3 = valuation.fiveYearFinancials.getOrNull(2)
        val y5 = valuation.fiveYearFinancials.getOrNull(4)

        val headline = when (perspective) {
            PitchPerspective.VENTURE_CAPITAL -> "Venture-Scale Economics & \$${y5?.arrMillions ?: 68.0}M Year 5 ARR Path"
            PitchPerspective.ENTERPRISE_BUYER -> "Compelling Enterprise ROI with ${valuation.customerRoi.paybackDays}-Day Payback"
            PitchPerspective.EXECUTIVE_BOARD -> "Financial Projections, Capital Efficiency & Valuation Milestones"
        }

        val subheadline = "Raising \$${deck.targetRaiseAmountMillions}M for 18-Month Runway & Enterprise Expansion"

        val executiveSummary = when (perspective) {
            PitchPerspective.VENTURE_CAPITAL ->
                "${venture.name} boasts top-decile SaaS unit economics: \$${valuation.unitEconomics.targetEnterpriseAcvThousands.toInt()}k ACV, ${valuation.unitEconomics.ltvToCacRatio}x LTV/CAC ratio, and a ${valuation.unitEconomics.paybackPeriodMonths}-month CAC payback period. Seed funding of \$${deck.targetRaiseAmountMillions}M finances engineering scale and 12 enterprise pilot conversions, driving ARR to \$${y3?.arrMillions ?: 14.8}M by Year 3 and position for a premium Series A/B valuation."
            PitchPerspective.ENTERPRISE_BUYER ->
                "For an enterprise client, deployment generates \$${valuation.customerRoi.annualClientCostSavingsMillions}M in direct annual operational savings against an annual license of \$${valuation.unitEconomics.targetEnterpriseAcvThousands.toInt()}k—representing a ${valuation.customerRoi.enterpriseRoiMultiple}x immediate enterprise ROI with capital payback in under ${valuation.customerRoi.paybackDays} days."
            PitchPerspective.EXECUTIVE_BOARD ->
                "Capital-efficient growth profile scaling from ${valuation.fiveYearFinancials.firstOrNull()?.customersCount ?: 6} enterprise accounts in Year 1 to ${y5?.customersCount ?: 280} in Year 5 with gross margins expanding above 80%."
        }

        val items = listOf(
            PitchSectionItem(
                title = "Target Unit Economics & Margin",
                narrative = "Average Contract Value: \$${valuation.unitEconomics.targetEnterpriseAcvThousands.toInt()}k/yr. CAC: \$${valuation.unitEconomics.customerAcquisitionCostThousands.toInt()}k. LTV: \$${valuation.unitEconomics.ltvThousands.toInt()}k.",
                metricValue = "${valuation.unitEconomics.ltvToCacRatio}x",
                metricLabel = "LTV / CAC Ratio",
                tag = "UNIT ECONOMICS"
            ),
            PitchSectionItem(
                title = "Customer Payback & Net Retention",
                narrative = "CAC is fully recouped within ${valuation.unitEconomics.paybackPeriodMonths} months, backed by ${valuation.unitEconomics.netRevenueRetentionPercent}% net revenue retention via multi-plant expansion.",
                metricValue = "${valuation.unitEconomics.netRevenueRetentionPercent}%",
                metricLabel = "Net Revenue Retention",
                tag = "RETENTION"
            ),
            PitchSectionItem(
                title = "5-Year Revenue Trajectory",
                narrative = "Scaling from \$${valuation.fiveYearFinancials.firstOrNull()?.arrMillions ?: 1.8}M ARR (Yr 1) → \$${y3?.arrMillions ?: 14.8}M ARR (Yr 3) → \$${y5?.arrMillions ?: 68.0}M ARR (Yr 5).",
                metricValue = "\$${y3?.arrMillions ?: 14.8}M",
                metricLabel = "Year 3 Projected ARR",
                tag = "GROWTH"
            ),
            PitchSectionItem(
                title = "Seed Capital Allocation (\$${deck.targetRaiseAmountMillions}M)",
                narrative = "55% Applied AI/ERP Engineering, 30% Enterprise Solutions Architecture & GTM, 15% SOC2 / Security Infrastructure & Working Capital.",
                metricValue = "\$${deck.targetRaiseAmountMillions}M",
                metricLabel = "Target Seed Round",
                tag = "USE OF FUNDS"
            )
        )

        val strategicTakeaway = "Unlocks a \$${valuation.year3ProjectedValuationMillions.toInt()}M+ enterprise valuation by Year 3 with institutional forward ARR multiples of 12.0x+."

        return PitchDeckArchitectSection(
            pillar = PitchArchitectPillar.FINANCIALS,
            headline = headline,
            subheadline = subheadline,
            executiveSummary = executiveSummary,
            items = items,
            strategicTakeaway = strategicTakeaway
        )
    }
}
