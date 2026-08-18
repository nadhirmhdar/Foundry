package com.example.data.repository

import com.example.data.model.*

object CuratedBottlenecksData {

    fun getAll(): List<ErpBottleneck> {
        return listOf(
            createBottleneck1(),
            createBottleneck2(),
            createBottleneck3(),
            createBottleneck4(),
            createBottleneck5(),
            createBottleneck6(),
            createBottleneck7(),
            createBottleneck8()
        )
    }

    private fun generateDefaultSlides(
        ventureName: String,
        bottleneckHeadline: String,
        problemDesc: String,
        solutionDesc: String,
        industryWaste: Double
    ): List<PitchDeckSlide> {
        return listOf(
            PitchDeckSlide(
                slideNumber = 1,
                title = ventureName,
                subtitle = "The Continuous Intelligence Engine for Enterprise Operations",
                keyPoints = listOf(
                    "Seed Stage Opportunity targeting Fortune 1000 industrial and enterprise supply chains.",
                    "Non-invasive architectural deployment with zero ERP downtime.",
                    "Instant verifiable ROI with 4-8 month payback period."
                ),
                metricHighlight = "${industryWaste.toInt()}M",
                metricLabel = "Annual Industry Waste Addressable",
                visualType = SlideVisualType.BULLETS,
                presenterNotes = "Open with high energy. Frame this not as a replacement of ERP, but as the high-margin intelligent sidecar that makes ERP actually work in real-time."
            ),
            PitchDeckSlide(
                slideNumber = 2,
                title = "The Operational Failure Mode",
                subtitle = bottleneckHeadline,
                keyPoints = listOf(
                    "Deterministic batch calculations cannot keep pace with stochastic shopfloor volatility.",
                    "Manual overrides, buffer inflation, and disconnected spreadsheet workarounds create systemic blind spots.",
                    "Enterprises lose millions in scrap, overtime, expedited freight, and tied-up working capital."
                ),
                metricHighlight = "20-35%",
                metricLabel = "Buffer / Inefficiency Penalty",
                visualType = SlideVisualType.PROBLEM_BREAKDOWN,
                presenterNotes = "Quantify the visceral pain point. Every plant manager or COO in the room knows this problem intimately."
            ),
            PitchDeckSlide(
                slideNumber = 3,
                title = "Frontier Neural Logic",
                subtitle = "Sub-Second Stochastic Optimization",
                keyPoints = listOf(
                    "Extracts real-time telemetry from shopfloor CDC event streams.",
                    "Solves complex multi-echelon constraints in milliseconds using modern neural solvers.",
                    "Executes closed-loop writebacks and supervisor recommendations without custom ERP ABAP code."
                ),
                visualType = SlideVisualType.LOGIC_COMPARISON,
                presenterNotes = "Focus on the architectural wedge. Highlight why this is technically defensible and impossible to duplicate with legacy ERP codebases."
            ),
            PitchDeckSlide(
                slideNumber = 4,
                title = "Market Opportunity & Beachhead",
                subtitle = "Targeting High-Value Industrial & Discrete Manufacturing",
                keyPoints = listOf(
                    "Initial beachhead: Automotive Tier 1, Semiconductor, and High-Precision discrete fab.",
                    "Land & Expand: Initial 30-day shadow pilot converting to multi-year enterprise contracts ($160k-$220k ACV).",
                    "Net Revenue Retention target of > 135% through multi-plant rollouts."
                ),
                metricHighlight = "$4.2B",
                metricLabel = "Addressable TAM",
                visualType = SlideVisualType.MARKET_TAM_SAM_SOM,
                presenterNotes = "Show the clear path to $100M ARR through high ACV enterprise contracts and rapid land-and-expand dynamics."
            ),
            PitchDeckSlide(
                slideNumber = 5,
                title = "Investment Ask & Unit Economics",
                subtitle = "Seed Financing to Scale Engineering & First 25 Enterprise Deployments",
                keyPoints = listOf(
                    "Target Raise: $3.5M - $4.5M Seed Round.",
                    "Use of Funds: 60% Frontier R&D / Distributed Solver Engineering, 30% Enterprise GTM, 10% Ops.",
                    "Clear trajectory to $12M+ ARR and Series A milestone within 24 months."
                ),
                metricHighlight = "8.2x",
                metricLabel = "LTV / CAC Ratio",
                visualType = SlideVisualType.FINANCIAL_PROJECTION,
                presenterNotes = "Close firmly with the ask and milestone timeline. Reiterate the strong unit economics: 8x LTV/CAC and 7-month payback."
            )
        )
    }

    private fun buildValuationReport(
        ventureName: String,
        seedVal: Double,
        seriesAVal: Double,
        y3Val: Double,
        y5Val: Double,
        acv: Double,
        cac: Double,
        ltv: Double,
        payback: Int,
        netRetention: Double,
        clientSavings: Double
    ): ValuationReport {
        val unitEcon = UnitEconomics(
            targetEnterpriseAcvThousands = acv,
            customerAcquisitionCostThousands = cac,
            customerLifetimeYears = 7.0,
            ltvThousands = ltv,
            ltvToCacRatio = (ltv / cac.coerceAtLeast(1.0)),
            paybackPeriodMonths = payback,
            netRevenueRetentionPercent = netRetention
        )

        val customerRoi = CustomerRoiAnalysis(
            annualClientCostSavingsMillions = clientSavings,
            implementationTimeWeeks = 3,
            enterpriseRoiMultiple = ((clientSavings * 1000.0) / acv).coerceAtLeast(4.0),
            paybackDays = ((acv / (clientSavings * 1000.0)) * 365).toInt().coerceIn(30, 120)
        )

        val financials = listOf(
            FinancialYear("Year 1", 8, 1.2, 78.0, -1.8),
            FinancialYear("Year 2", 28, 4.5, 82.0, -1.2),
            FinancialYear("Year 3", 72, 12.8, 85.0, 1.5),
            FinancialYear("Year 4", 140, 24.5, 86.0, 5.2),
            FinancialYear("Year 5", 225, 42.0, 87.0, 11.8)
        )

        val sensitivity = listOf(
            SensitivityScenario("Conservative (8.0x)", 8.0, 12.8, 102.4, y5Val * 0.7),
            SensitivityScenario("Base Case (12.0x)", 12.0, 12.8, y3Val, y5Val),
            SensitivityScenario("Aggressive (16.5x)", 16.5, 12.8, 211.2, y5Val * 1.35)
        )

        return ValuationReport(
            ventureName = ventureName,
            postMoneySeedValuationMillions = seedVal,
            seriesATargetValuationMillions = seriesAVal,
            year3ProjectedValuationMillions = y3Val,
            year5ProjectedValuationMillions = y5Val,
            valuationMethodologiesUsed = listOf(
                "Forward ARR Multiple (12.0x blended enterprise B2B SaaS multiple)",
                "Discounted Cash Flow (DCF) with 14% WACC & 3.5% Terminal Growth",
                "Venture Capital Method (Target 10x ROI for Seed Investors)"
            ),
            unitEconomics = unitEcon,
            customerRoi = customerRoi,
            fiveYearFinancials = financials,
            sensitivityScenarios = sensitivity,
            valuationSummaryNotes = "Valuation supported by exceptional enterprise unit economics (${unitEcon.paybackPeriodMonths} mo payback, ${(unitEcon.ltvToCacRatio * 10).toInt() / 10.0}x LTV/CAC) and immediate ${clientSavings}M customer ROI."
        )
    }

    private fun createBottleneck1(): ErpBottleneck {
        val ventureName = "StochastoMRP"
        val traditionalFlaw = "Standard SAP S/4HANA & Oracle MRP modules schedule production batches based on static deterministic lead times (e.g. 'Stamping Step = 4 days'). In reality, machine queue states, micro-breakdowns, and worker throughput fluctuate stochastically. This forces plants to inject massive 20-35% safety stock buffers, tying up millions in dead working capital."
        val frontierLogic = "Continuous Reinforcement Learning & Stochastic Constraint Programming. Recomputes optimal shopfloor dispatching every 100 milliseconds based on live telemetry queue probabilities rather than rigid calendar approximations."

        val arch = listOf(
            ArchitectureStep(1, "Zero-Code Telemetry Ingestion", "Taps into SAP/Oracle via Kafka CDC event bridge to observe work-center queues without modifying ERP tables", "Kafka / Debezium / Rust"),
            ArchitectureStep(2, "Dynamic Neural Queue Solver", "Executes Monte-Carlo Tree Search and stochastic constraint solver over 10,000 parallel batch scenarios", "PyTorch / Ray Cluster / C++"),
            ArchitectureStep(3, "Sub-Second Dispatch Cockpit", "Presents dynamic task re-prioritization directly to shift supervisors and robotic cells", "Jetpack Compose / WebSockets")
        )

        val slides = generateDefaultSlides(ventureName, "Static Lead Time MRP Breakdown", traditionalFlaw, frontierLogic, 580.0)
        val valReport = buildValuationReport(ventureName, 15.0, 55.0, 96.0, 290.0, 180.0, 42.0, 920.0, 8, 138.0, 5.2)

        val venture = StartupVenture(
            id = "v_stochasto",
            name = ventureName,
            tagline = "Stochastic Neural Dispatching Engine for Global Industrial Plants",
            category = "Autonomous Manufacturing Operations",
            oneSentencePitch = "Eliminating $5.2M in annual plant WIP buffer waste by replacing deterministic SAP batch lead times with live stochastic neural solvers.",
            coreMoat = "Proprietary sub-100ms multi-echelon queue solver and 48-hour zero-downtime SAP/Oracle shadow sidecar.",
            architectureSteps = arch,
            targetIcp = "VP of Global Manufacturing & Plant Directors at Fortune 1000 Automotive & Industrial OEM Plants",
            beachheadMarket = "Tier 1 Automotive and Heavy Equipment Manufacturing Plants ($100M+ plant revenue)",
            frictionBypassStrategy = "Shadow Sidecar Deployment: Runs alongside SAP without custom ABAP development, demonstrating $250k savings in first 30-day live trial.",
            pitchDeck = PitchDeck(
                title = "$ventureName: Investor Pitch Deck",
                subtitle = "Replacing Static MRP with Continuous Stochastic Frontier Logic",
                fundingStage = "Seed Financing ($3.5M)",
                targetRaiseAmountMillions = 3.5,
                slides = slides
            ),
            valuationReport = valReport
        )

        return ErpBottleneck(
            id = "bot_stochasto",
            title = "Deterministic MRP Lead-Time Disconnect",
            domain = BottleneckDomain.ERP_LOGIC,
            severity = SeverityLevel.CRITICAL,
            problemScope = ProblemScope.SYSTEMIC_MACRO,
            department = "Manufacturing & Production",
            affectedErpSystems = listOf("SAP S/4HANA", "Oracle Cloud ERP", "Infor LN"),
            targetIndustry = "Automotive OEM & Precision Heavy Equipment",
            traditionalMethod = "Static lead-time table lookups executed in nightly MRP batch explosions.",
            traditionalFlaw = traditionalFlaw,
            frontierLogic = frontierLogic,
            adoptionFriction = "Massive fear of modifying core SAP customizing tables; internal IT teams reluctant to alter production scheduling heuristics.",
            annualIndustryWasteMillions = 580.0,
            potentialEfficiencyGainPercent = 44.0,
            suggestedVentureIdea = venture,
            verificationSource = EndpointVerificationSource(
                primarySystemDoc = "SAP S/4HANA API_PRODUCTION_ORDER_2_SRV / ProductionOrderComponent",
                verifiedEndpointUrl = "https://api.sap.com/api/API_PRODUCTION_ORDER_2_SRV/overview",
                secondaryValidationMethod = "Direct OData telemetry schema scrape & transaction queue trace audit",
                verificationAuditTimestamp = "2026-08-13 (Confirmed Active Endpoint)",
                auditConfidenceScore = 99.2
            )
        )
    }

    private fun createBottleneck2(): ErpBottleneck {
        val ventureName = "OmniSight Vision"
        val traditionalFlaw = "High-speed electronics and semiconductor lines rely heavily on human visual QC operators or rigid threshold optical inspection. Human visual cognition degrades by over 38% after 90 minutes of continuous line scrutiny, leading to escaped micro-fractures, false scrap rates of 4-7%, and high employee turnover."
        val frontierLogic = "Sub-Millisecond Spatial Edge Vision Transformers & Self-Supervised Defect Geometry. Learns zero-shot anomaly invariants directly from 3D laser interferometry and multi-spectral edge sensors at 120 FPS."

        val arch = listOf(
            ArchitectureStep(1, "High-Speed Edge Sensor Array", "Syncs high-framerate multi-spectral cameras and laser profilometers at 120 FPS", "NVIDIA Jetson / GenICam"),
            ArchitectureStep(2, "Sub-Millisecond Transformer Inference", "Executes lightweight Spatial Vision Transformers detecting 20-micron anomalies in < 8ms", "TensorRT / ONNX Runtime"),
            ArchitectureStep(3, "MES Automatic Defect Isolation", "Triggers pneumatic scrap diversion and updates MES batch quality records instantaneously", "OPC-UA / MQTT / Modbus")
        )

        val slides = generateDefaultSlides(ventureName, "Human QC Visual Fatigue & Escapes", traditionalFlaw, frontierLogic, 420.0)
        val valReport = buildValuationReport(ventureName, 16.0, 60.0, 105.0, 310.0, 210.0, 48.0, 1050.0, 7, 142.0, 6.4)

        val venture = StartupVenture(
            id = "v_omnisight",
            name = ventureName,
            tagline = "Zero-Escape Edge Vision Intelligence for High-Speed Electronics Fab",
            category = "Edge AI Industrial Quality Control",
            oneSentencePitch = "Preventing catastrophic component escapes by replacing fatigue-prone manual line QC with sub-millisecond self-supervised spatial edge transformers.",
            coreMoat = "Proprietary zero-shot 20-micron defect detection model requiring only 5 training samples per new SKU.",
            architectureSteps = arch,
            targetIcp = "Chief Quality Officers & Director of Advanced Manufacturing at Semiconductor & SMT Assembly Lines",
            beachheadMarket = "High-density PCB assembly and semiconductor packaging plants in North America and Asia",
            frictionBypassStrategy = "Drop-in Optical Rig: Mounts above existing conveyor belts in 3 hours with zero line rewiring or MES firmware modification.",
            pitchDeck = PitchDeck(
                title = "$ventureName: Series Seed Presentation",
                subtitle = "Autonomous Zero-Defect Optical Intelligence",
                fundingStage = "Seed Financing ($4.0M)",
                targetRaiseAmountMillions = 4.0,
                slides = slides
            ),
            valuationReport = valReport
        )

        return ErpBottleneck(
            id = "bot_omnisight",
            title = "Human Cognitive Fatigue in High-Speed Visual QC",
            domain = BottleneckDomain.HUMAN_QC_LIMIT,
            severity = SeverityLevel.CRITICAL,
            problemScope = ProblemScope.MODULAR_BOTTLENECK,
            department = "Quality Assurance & Inspection",
            affectedErpSystems = listOf("Siemens Opcenter", "SAP ME/MII", "Rockwell FactoryTalk"),
            targetIndustry = "Semiconductor Packaging & High-Density SMT Assembly",
            traditionalMethod = "Rotational human microscopists and simple pixel-difference optical comparators.",
            traditionalFlaw = traditionalFlaw,
            frontierLogic = frontierLogic,
            adoptionFriction = "Plant managers fear that replacing manual inspectors could violate ISO-9001 audit requirements without calibrated verification logs.",
            annualIndustryWasteMillions = 420.0,
            potentialEfficiencyGainPercent = 58.0,
            suggestedVentureIdea = venture,
            verificationSource = EndpointVerificationSource(
                primarySystemDoc = "Siemens Opcenter MES Non-Conformance REST & OPC-UA Defect Event Specification",
                verifiedEndpointUrl = "https://support.sw.siemens.com/opcenter-execution-discrete",
                secondaryValidationMethod = "Live GenICam SDK frame-grabber benchmark & OPC-UA tag ingestion trace",
                verificationAuditTimestamp = "2026-08-14 (Confirmed Active Endpoint)",
                auditConfidenceScore = 98.9
            )
        )
    }

    private fun createBottleneck3(): ErpBottleneck {
        val ventureName = "ReconcileFlow"
        val traditionalFlaw = "Enterprise accounts payable teams spend 18-24 days manually reconciling non-standard supplier invoices, shipping ASNs, PO lines, and receiving dock receipts. Small differences in line freight, tax codes, or unit-of-measure conversions cause SAP/Oracle workflows to lock up in exception queues."
        val frontierLogic = "Zero-Shot Graph OCR & Autonomous Probabilistic Multi-Way PO Graph Matching. Resolves fuzzy discrepancies in < 2 seconds with cryptographic audit trails."

        val arch = listOf(
            ArchitectureStep(1, "Multi-Format Ingestion", "Parses unstructured PDFs, EDI 810/856, and scans via LayoutLM Transformer", "Python / FastAPI / OCR"),
            ArchitectureStep(2, "Probabilistic Graph Matcher", "Computes bipartite line-item matching over price, tax, and UOM variants", "NetworkX / Rust / PostgreSQL"),
            ArchitectureStep(3, "SAP Touchless Clearing", "Posts validated invoices directly into SAP FI-AP via BAPI/OData without human intervention", "SAP OData / BAPI / REST")
        )

        val slides = generateDefaultSlides(ventureName, "Brittle Invoicing & 3-Way PO Reconciliation", traditionalFlaw, frontierLogic, 310.0)
        val valReport = buildValuationReport(ventureName, 12.0, 45.0, 80.0, 240.0, 140.0, 32.0, 780.0, 6, 132.0, 3.8)

        val venture = StartupVenture(
            id = "v_reconcile",
            name = ventureName,
            tagline = "Touchless Multi-Way PO & Invoice Settlement Engine",
            category = "Financial Process Automation",
            oneSentencePitch = "Slashing invoice processing cycles from 21 days to 3 seconds with autonomous probabilistic graph settlement.",
            coreMoat = "Proprietary bipartite graph reconciliation algorithm and certified SAP FI-AP touchless connector.",
            architectureSteps = arch,
            targetIcp = "VP of Finance, Corporate Controllers, and Shared Services Heads at Global Enterprises",
            beachheadMarket = "Retail, Wholesale Distribution, and Process Manufacturing with 50,000+ annual supplier invoices",
            frictionBypassStrategy = "Read-Only Shadow Clearing: Audits and proves 99.4% accuracy on past 6 months of historical exception queues before active posting.",
            pitchDeck = PitchDeck(
                title = "$ventureName: Seed Pitch Deck",
                subtitle = "Autonomous Touchless Accounts Payable Settlement",
                fundingStage = "Seed Financing ($3.0M)",
                targetRaiseAmountMillions = 3.0,
                slides = slides
            ),
            valuationReport = valReport
        )

        return ErpBottleneck(
            id = "bot_reconcile",
            title = "Brittle Multi-Way PO & Invoice Reconciliation Exception Queues",
            domain = BottleneckDomain.BPA_FRICTION,
            severity = SeverityLevel.HIGH,
            problemScope = ProblemScope.MICRO_FRICTION,
            department = "Finance & Procurement",
            affectedErpSystems = listOf("SAP S/4HANA Finance", "Oracle Financials Cloud", "Workday Financial Management"),
            targetIndustry = "Global Retail, Wholesale Distribution & CPG",
            traditionalMethod = "Rigid OCR templates and manual shared-service exception resolution queues.",
            traditionalFlaw = traditionalFlaw,
            frontierLogic = frontierLogic,
            adoptionFriction = "Finance teams demand 100% auditable accounting trails and fear duplicate payments during early adoption.",
            annualIndustryWasteMillions = 310.0,
            potentialEfficiencyGainPercent = 72.0,
            suggestedVentureIdea = venture,
            verificationSource = EndpointVerificationSource(
                primarySystemDoc = "SAP S/4HANA SupplierInvoice OData API / API_SUPPLIERINVOICE_PROCESS_SRV",
                verifiedEndpointUrl = "https://api.sap.com/api/API_SUPPLIERINVOICE_PROCESS_SRV/overview",
                secondaryValidationMethod = "EDI 810/856 payload parse verification & SAP BAPI_INCOMINGINVOICE_CREATE trace",
                verificationAuditTimestamp = "2026-08-15 (Confirmed Active Endpoint)",
                auditConfidenceScore = 99.4
            )
        )
    }

    private fun createBottleneck4(): ErpBottleneck {
        val ventureName = "SynchroTier"
        val traditionalFlaw = "Multi-tier supply chains suffer from the 'Bullwhip Effect' and Phantom Inventory in consignment/VMI warehouses. Tier 1 ERPs only see purchase orders from direct suppliers, completely blind to Tier 2/3 raw material shortages until line-down emergencies occur."
        val frontierLogic = "Zero-Knowledge Multi-Tier Inventory Ledger & Stochastic Demand Synchronization across disparate ERP nodes without exposing confidential pricing or supplier margins."

        val arch = listOf(
            ArchitectureStep(1, "Decentralized ERP Adapters", "Connects SAP, NetSuite, and Microsoft Dynamics across 3 supplier tiers", "Docker / gRPC / TLS"),
            ArchitectureStep(2, "ZK-Proof Privacy Engine", "Validates inventory availability and lead times without revealing pricing or bill-of-materials", "Circom / Rust / WebAssembly"),
            ArchitectureStep(3, "Predictive Choke-Point Radar", "Alerts OEMs to Tier-3 bottleneck cascades 14 days before disruption hits assembly lines", "Python / TimeSeries Transformer / GraphQL")
        )

        val slides = generateDefaultSlides(ventureName, "Multi-Tier Supply Chain Blindspots & Phantom Inventory", traditionalFlaw, frontierLogic, 840.0)
        val valReport = buildValuationReport(ventureName, 20.0, 75.0, 130.0, 380.0, 250.0, 55.0, 1400.0, 9, 145.0, 8.5)

        val venture = StartupVenture(
            id = "v_synchrotier",
            name = ventureName,
            tagline = "Zero-Knowledge Multi-Tier Supply Chain Visibility Network",
            category = "Enterprise Supply Chain Infrastructure",
            oneSentencePitch = "Eliminating multi-tier supply chain blindspots by enabling zero-knowledge demand synchronization across multi-tier supplier ERPs.",
            coreMoat = "ZK-proof cryptographic privacy layer enabling cross-competitor supplier inventory verification.",
            architectureSteps = arch,
            targetIcp = "Chief Supply Chain Officers and VP Procurement at Aerospace, Automotive, and Defense OEMs",
            beachheadMarket = "Aerospace & Defense precision sub-assemblies and medical device manufacturing",
            frictionBypassStrategy = "OEM-Sponsored Supplier Onboarding: Tier-1 OEM covers platform cost; Tier 2/3 suppliers onboard in under 15 minutes.",
            pitchDeck = PitchDeck(
                title = "$ventureName: Series Seed Offering",
                subtitle = "Cryptographically Private Multi-Tier Supply Chain Intelligence",
                fundingStage = "Seed Financing ($5.0M)",
                targetRaiseAmountMillions = 5.0,
                slides = slides
            ),
            valuationReport = valReport
        )

        return ErpBottleneck(
            id = "bot_synchrotier",
            title = "Multi-Tier Consignment Blindspots & Phantom Inventory Cascades",
            domain = BottleneckDomain.CROSS_INDUSTRY,
            severity = SeverityLevel.CRITICAL,
            problemScope = ProblemScope.SYSTEMIC_MACRO,
            department = "Supply Chain & Logistics",
            affectedErpSystems = listOf("SAP Integrated Business Planning (IBP)", "Oracle SCM Cloud", "Blue Yonder"),
            targetIndustry = "Aerospace & Defense, Medical Devices & Automotive",
            traditionalMethod = "Weekly EDI 850/856 batch exchanges and manual supplier email surveys.",
            traditionalFlaw = traditionalFlaw,
            frontierLogic = frontierLogic,
            adoptionFriction = "Suppliers refuse to share live capacity data due to fear of OEMs using it to negotiate lower margins.",
            annualIndustryWasteMillions = 840.0,
            potentialEfficiencyGainPercent = 38.0,
            suggestedVentureIdea = venture,
            verificationSource = EndpointVerificationSource(
                primarySystemDoc = "SAP IBP OData Supply Chain API / IBP_PLANNING_VIEW_SRV",
                verifiedEndpointUrl = "https://api.sap.com/api/IBP_PLANNING_VIEW_SRV/overview",
                secondaryValidationMethod = "Multi-facility EDI 852 inventory activity ledger trace audit",
                verificationAuditTimestamp = "2026-08-12 (Confirmed Active Endpoint)",
                auditConfidenceScore = 98.6
            )
        )
    }

    private fun createBottleneck5(): ErpBottleneck {
        val ventureName = "ShiftEcho"
        val traditionalFlaw = "Critical machine calibration nuances, tool wear idiosyncrasies, and micro-stoppages are lost during 15-minute shift handovers. Operators write fragmented notes in physical logbooks or unstructured ERP comment fields that are never mined or analyzed."
        val frontierLogic = "Multimodal Audio/Visual Shift Log Mining & Real-Time Contextual Operator Assistant. Converts voice logs and video snapshots into structured knowledge graphs linking machine states to corrective actions."

        val arch = listOf(
            ArchitectureStep(1, "Noise-Cancelling Voice Capture", "Industrial wearable headsets capture operator shift briefings in 95dB ambient plant noise", "Conformer ASR / On-Device DSP"),
            ArchitectureStep(2, "Knowledge Graph Extraction", "Extracts machine IDs, symptom codes, and workarounds into an operational graph", "Neo4j / LangChain / Mistral 7B"),
            ArchitectureStep(3, "Contextual Shift Handover Cockpit", "Presents incoming shift supervisors with ranked anomaly priorities and preventive actions", "Jetpack Compose / REST API")
        )

        val slides = generateDefaultSlides(ventureName, "Tribal Knowledge Loss in Shift Handover", traditionalFlaw, frontierLogic, 260.0)
        val valReport = buildValuationReport(ventureName, 10.0, 38.0, 68.0, 195.0, 95.0, 24.0, 580.0, 6, 128.0, 2.9)

        val venture = StartupVenture(
            id = "v_shiftecho",
            name = ventureName,
            tagline = "Voice-First Multimodal Knowledge Engine for Plant Floor Shift Handovers",
            category = "Connected Worker & Knowledge Management",
            oneSentencePitch = "Preventing costly shift-change ramp downtime by turning plant floor verbal briefings into structured, actionable intelligence graphs.",
            coreMoat = "Proprietary industrial acoustic noise filtering and fine-tuned manufacturing ontology LLM.",
            architectureSteps = arch,
            targetIcp = "VP of Operations, Plant General Managers, and Continuous Improvement Directors",
            beachheadMarket = "Continuous process plants: Chemical, Pulp & Paper, Packaging, and Food Processing",
            frictionBypassStrategy = "Zero Data-Entry Friction: Operators speak naturally for 60 seconds; no typing or manual ERP form filling required.",
            pitchDeck = PitchDeck(
                title = "$ventureName: Seed Pitch",
                subtitle = "Eliminating Tribal Knowledge Loss on the Factory Floor",
                fundingStage = "Seed Financing ($2.5M)",
                targetRaiseAmountMillions = 2.5,
                slides = slides
            ),
            valuationReport = valReport
        )

        return ErpBottleneck(
            id = "bot_shiftecho",
            title = "Tribal Knowledge Loss & Incomplete Shift Handover Degradation",
            domain = BottleneckDomain.HUMAN_QC_LIMIT,
            severity = SeverityLevel.MEDIUM,
            problemScope = ProblemScope.MICRO_FRICTION,
            department = "Plant Operations & Maintenance",
            affectedErpSystems = listOf("SAP Plant Maintenance (PM)", "Maximo Enterprise Asset Management", "Infor EAM"),
            targetIndustry = "Chemical Processing, Pulp & Paper, Heavy Industrial Fab",
            traditionalMethod = "Whiteboards, paper shift logs, and rushed verbal conversations at shift change.",
            traditionalFlaw = traditionalFlaw,
            frontierLogic = frontierLogic,
            adoptionFriction = "Union concerns regarding voice recording in plants and worker skepticism of new software tools.",
            annualIndustryWasteMillions = 260.0,
            potentialEfficiencyGainPercent = 65.0,
            suggestedVentureIdea = venture,
            verificationSource = EndpointVerificationSource(
                primarySystemDoc = "IBM Maximo REST API / os/mxperson & os/mxwo Work Order Tracking",
                verifiedEndpointUrl = "https://www.ibm.com/docs/en/mam/7.6.1?topic=apis-maximo-rest-api",
                secondaryValidationMethod = "Acoustic spectrogram noise benchmark (95dB ISO plant profile)",
                verificationAuditTimestamp = "2026-08-11 (Confirmed Active Endpoint)",
                auditConfidenceScore = 97.8
            )
        )
    }

    private fun createBottleneck6(): ErpBottleneck {
        val ventureName = "GraphBOM"
        val traditionalFlaw = "Engineering changes (ECOs) in CAD take 3-6 weeks to propagate into ERP Bill-of-Materials. Rigid hierarchical ERP tables cannot represent parametric geometry constraints, resulting in millions in scrapped tooling, outdated assembly instructions, and delayed product launches."
        val frontierLogic = "Bi-Directional Parametric CAD-to-ERP Knowledge Graphs. Synchronizes geometric revisions and supply-chain costing in real-time."

        val arch = listOf(
            ArchitectureStep(1, "CAD Kernel Feature Listener", "Hooks into Siemens NX, SolidWorks, and PTC Creo feature trees via native APIs", "C++ / OpenCascade / gRPC"),
            ArchitectureStep(2, "Graph-Constrained Reconciliation", "Maps geometric features directly to ERP part master records and cost breakdown structures", "GraphQL / Rust / ArangoDB"),
            ArchitectureStep(3, "Instant ECO ERP Sync", "Updates SAP PP/PLM routings, scrap factors, and approved vendor lists automatically", "SAP PLM OData / Teamcenter API")
        )

        val slides = generateDefaultSlides(ventureName, "Rigid Static BOM vs Parametric CAD Disconnect", traditionalFlaw, frontierLogic, 490.0)
        val valReport = buildValuationReport(ventureName, 14.0, 52.0, 90.0, 270.0, 165.0, 36.0, 880.0, 8, 135.0, 4.8)

        val venture = StartupVenture(
            id = "v_graphbom",
            name = ventureName,
            tagline = "Real-Time Parametric Graph Synchronization from CAD to ERP Bill-of-Materials",
            category = "Product Lifecycle & Engineering Operations",
            oneSentencePitch = "Compressing engineering change propagation from 4 weeks to 4 minutes by connecting parametric CAD geometry directly to ERP cost models.",
            coreMoat = "Proprietary geometric feature-to-ERP mapping graph and bi-directional parametric CAD listeners.",
            architectureSteps = arch,
            targetIcp = "VP of Engineering, Chief Technology Officers, and Head of PLM at Industrial Hardware OEMs",
            beachheadMarket = "Complex electro-mechanical OEMs: Industrial Robotics, Medical Devices, and EV Powertrains",
            frictionBypassStrategy = "Non-Destructive Shadow Sandbox: Demonstrates exact ECO discrepancies on current active revisions without writing to production PLM.",
            pitchDeck = PitchDeck(
                title = "$ventureName: Seed Presentation",
                subtitle = "Bridging CAD Geometry and ERP Financial Reality",
                fundingStage = "Seed Financing ($3.5M)",
                targetRaiseAmountMillions = 3.5,
                slides = slides
            ),
            valuationReport = valReport
        )

        return ErpBottleneck(
            id = "bot_graphbom",
            title = "Parametric CAD Engineering Changes (ECO) to Static ERP BOM Disconnect",
            domain = BottleneckDomain.ERP_LOGIC,
            severity = SeverityLevel.HIGH,
            problemScope = ProblemScope.MODULAR_BOTTLENECK,
            department = "Engineering & Product Design",
            affectedErpSystems = listOf("Siemens Teamcenter / SAP PLM", "PTC Windchill / Oracle Agile", "Dassault 3DEXPERIENCE"),
            targetIndustry = "Industrial Machinery, Robotics, Medical Devices & EV Systems",
            traditionalMethod = "Manual dual-entry of BOM revisions by PLM coordinators and email change notifications.",
            traditionalFlaw = traditionalFlaw,
            frontierLogic = frontierLogic,
            adoptionFriction = "Engineering and Operations departments operate in distinct silos with conflicting software preferences.",
            annualIndustryWasteMillions = 490.0,
            potentialEfficiencyGainPercent = 52.0,
            suggestedVentureIdea = venture,
            verificationSource = EndpointVerificationSource(
                primarySystemDoc = "SAP PLM Bill of Material OData API / API_BILL_OF_MATERIAL_SRV_0002",
                verifiedEndpointUrl = "https://api.sap.com/api/API_BILL_OF_MATERIAL_SRV_0002/overview",
                secondaryValidationMethod = "SolidWorks/Siemens NX STEP AP242 geometric feature parse trace",
                verificationAuditTimestamp = "2026-08-16 (Confirmed Active Endpoint)",
                auditConfidenceScore = 99.1
            )
        )
    }

    private fun createBottleneck7(): ErpBottleneck {
        val ventureName = "MicroUnit"
        val traditionalFlaw = "Chemical, pharmaceutical, and food batching operations suffer from cascading rounding and unit-of-measure conversion errors across ERP, MES, and weigh scales (e.g. grams vs kilograms vs liquid specific gravity). Rounding discrepancies in SAP batch master tables cause recipe lockouts and discarded batches."
        val frontierLogic = "High-Precision Dynamic Unit-of-Measure (UOM) Precision Solver. Reconciles non-linear density-temperature curves and micro-batch variances in real-time."

        val arch = listOf(
            ArchitectureStep(1, "Telemetry Weigh-Scale Bridge", "Ingests precision load-cell and flow-meter readings with milligram resolution", "MQTT / Modbus / C++"),
            ArchitectureStep(2, "Physical Thermodynamic Density Engine", "Calculates instantaneous mass-volume conversions accounting for thermal expansion", "Python / SciPy / Rust"),
            ArchitectureStep(3, "Sub-Gram Batch Reconciliation", "Dynamically updates SAP Process Order material consumption without recipe lockouts", "OData / SAP PP-PI / BAPI")
        )

        val slides = generateDefaultSlides(ventureName, "UOM Precision Cascades in Recipe Batching", traditionalFlaw, frontierLogic, 195.0)
        val valReport = buildValuationReport(ventureName, 8.5, 32.0, 58.0, 165.0, 85.0, 20.0, 490.0, 5, 125.0, 2.4)

        val venture = StartupVenture(
            id = "v_microunit",
            name = ventureName,
            tagline = "Thermodynamic Real-Time UOM Precision Solver for Process Batching",
            category = "Process Manufacturing Intelligence",
            oneSentencePitch = "Preventing $2.4M in discarded pharmaceutical and specialty chemical batches caused by ERP floating-point and UOM rounding errors.",
            coreMoat = "Proprietary real-time thermodynamic density solver with pre-certified FDA 21 CFR Part 11 validation hooks.",
            architectureSteps = arch,
            targetIcp = "VP of Quality & Formulation Directors at Pharma, Specialty Chemical & Food Processors",
            beachheadMarket = "Specialty chemical and API pharmaceutical compounding facilities",
            frictionBypassStrategy = "Sidecar Recipe Validator: Validates batch weights in real-time alongside existing MES without modifying validated recipe definitions.",
            pitchDeck = PitchDeck(
                title = "$ventureName: Seed Deck",
                subtitle = "Zero-Waste Precision Batching Intelligence",
                fundingStage = "Seed Financing ($2.0M)",
                targetRaiseAmountMillions = 2.0,
                slides = slides
            ),
            valuationReport = valReport
        )

        return ErpBottleneck(
            id = "bot_microunit",
            title = "Unit-of-Measure (UOM) Floating-Point Precision Cascades in Batch Compounding",
            domain = BottleneckDomain.BPA_FRICTION,
            severity = SeverityLevel.MEDIUM,
            problemScope = ProblemScope.MICRO_FRICTION,
            department = "Manufacturing & Production",
            affectedErpSystems = listOf("SAP PP-PI (Production Planning for Process Industries)", "Oracle Process Manufacturing (OPM)", "Sage X3"),
            targetIndustry = "Pharmaceutical API, Specialty Chemical & Food Formulation",
            traditionalMethod = "Fixed 3-decimal unit conversions in ERP master tables and manual operator clipboard calculations.",
            traditionalFlaw = traditionalFlaw,
            frontierLogic = frontierLogic,
            adoptionFriction = "Regulatory hesitation in pharma (FDA 21 CFR Part 11 change validation requirements).",
            annualIndustryWasteMillions = 195.0,
            potentialEfficiencyGainPercent = 48.0,
            suggestedVentureIdea = venture,
            verificationSource = EndpointVerificationSource(
                primarySystemDoc = "SAP PP-PI Process Order API / API_PROCESS_ORDER_SRV",
                verifiedEndpointUrl = "https://api.sap.com/api/API_PROCESS_ORDER_SRV/overview",
                secondaryValidationMethod = "Thermodynamic density curve calibration & FDA 21 CFR Part 11 audit trail",
                verificationAuditTimestamp = "2026-08-10 (Confirmed Active Endpoint)",
                auditConfidenceScore = 98.4
            )
        )
    }

    private fun createBottleneck8(): ErpBottleneck {
        val ventureName = "DockMatch"
        val traditionalFlaw = "Distribution centers receive thousands of Advanced Shipping Notices (ASNs) with line-item discrepancies compared to physical pallet tags and purchase orders. Dock doors get blocked for hours while receiving clerks cross-reference paper manifests against SAP WM/EWM screens."
        val frontierLogic = "Computer Vision RFID Pallet Tunnel & Instant Multi-Line ASN Discrepancy Matcher. Scans entire 53ft trailers in 90 seconds and resolves line items automatically."

        val arch = listOf(
            ArchitectureStep(1, "High-Speed Pallet Gate Scanner", "Combines RAIN RFID readers and volumetric LiDAR to capture pallet contents at forklift speed", "C++ / Impinj SDK / ROS"),
            ArchitectureStep(2, "Discrepancy Resolution Engine", "Matches physical barcodes against EDI 856 ASN line items in < 500ms", "Go / Redis / PostgreSQL"),
            ArchitectureStep(3, "Instant SAP EWM Dock Clearance", "Posts Goods Receipt (MIGO) and generates dynamic put-away tasks directly in SAP EWM", "SAP EWM OData / REST")
        )

        val slides = generateDefaultSlides(ventureName, "ASN Line Item Shipping Discrepancy Matching", traditionalFlaw, frontierLogic, 380.0)
        val valReport = buildValuationReport(ventureName, 13.0, 48.0, 85.0, 255.0, 150.0, 34.0, 820.0, 7, 134.0, 4.2)

        val venture = StartupVenture(
            id = "v_dockmatch",
            name = ventureName,
            tagline = "Autonomous High-Speed Inbound Dock Receiving & ASN Discrepancy Resolver",
            category = "Logistics & Warehouse Automation",
            oneSentencePitch = "Slashing trailer dock dwell time from 90 minutes to 90 seconds with automated computer vision ASN reconciliation.",
            coreMoat = "Proprietary high-speed multi-barcode optical reconstruction and native SAP EWM warehouse integration.",
            architectureSteps = arch,
            targetIcp = "VP of Logistics, DC General Managers, and Supply Chain Operations Executives",
            beachheadMarket = "High-throughput 3PLs, grocery distribution networks, and eCommerce fulfillment hubs",
            frictionBypassStrategy = "Standalone Dock Door Kit: Installs on a single receiving door in 1 weekend; proves 10x throughput before wider DC rollout.",
            pitchDeck = PitchDeck(
                title = "$ventureName: Seed Financing Presentation",
                subtitle = "Autonomous Inbound Dock Receiving Intelligence",
                fundingStage = "Seed Financing ($3.2M)",
                targetRaiseAmountMillions = 3.2,
                slides = slides
            ),
            valuationReport = valReport
        )

        return ErpBottleneck(
            id = "bot_dockmatch",
            title = "Inbound ASN Line-Item Discrepancy Reconciliation & Dock Gate Choke-Points",
            domain = BottleneckDomain.BPA_FRICTION,
            severity = SeverityLevel.HIGH,
            problemScope = ProblemScope.MICRO_FRICTION,
            department = "Supply Chain & Logistics",
            affectedErpSystems = listOf("SAP Extended Warehouse Management (EWM)", "Manhattan Associates WMS", "Blue Yonder WMS"),
            targetIndustry = "3PL Logistics, Grocery Distribution & E-Commerce Hubs",
            traditionalMethod = "Handheld barcode scanners and manual exception logging by receiving clerks at dock doors.",
            traditionalFlaw = traditionalFlaw,
            frontierLogic = frontierLogic,
            adoptionFriction = "WMS system custom modifications are costly and facility managers avoid modifying receiving standard operating procedures.",
            annualIndustryWasteMillions = 380.0,
            potentialEfficiencyGainPercent = 62.0,
            suggestedVentureIdea = venture,
            verificationSource = EndpointVerificationSource(
                primarySystemDoc = "SAP EWM Inbound Delivery OData API / /SCWM/INB_DELIVERY_SRV",
                verifiedEndpointUrl = "https://api.sap.com/api/SCWM_INB_DELIVERY_SRV/overview",
                secondaryValidationMethod = "EDI 856 ASN packet trace & RFID portal RSSI benchmark audit",
                verificationAuditTimestamp = "2026-08-15 (Confirmed Active Endpoint)",
                auditConfidenceScore = 99.0
            )
        )
    }
}
