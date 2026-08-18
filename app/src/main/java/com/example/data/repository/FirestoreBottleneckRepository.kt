package com.example.data.repository

import android.util.Log
import com.example.data.model.*
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Interface defining CRUD and querying operations for Process Bottlenecks.
 */
interface BottleneckRepository {
    suspend fun createBottleneck(bottleneck: ErpBottleneck): Result<String>
    suspend fun getBottleneckById(id: String): Result<ErpBottleneck?>
    fun observeBottleneckById(id: String): Flow<ErpBottleneck?>
    fun getAllBottlenecks(): Flow<List<ErpBottleneck>>
    suspend fun getAllBottlenecksOnce(): Result<List<ErpBottleneck>>
    fun getBottlenecksByDomain(domain: BottleneckDomain): Flow<List<ErpBottleneck>>
    fun getBottlenecksBySeverity(severity: SeverityLevel): Flow<List<ErpBottleneck>>
    fun getBottlenecksByDepartment(department: String): Flow<List<ErpBottleneck>>
    fun searchBottlenecks(query: String): Flow<List<ErpBottleneck>>
    suspend fun updateBottleneck(bottleneck: ErpBottleneck): Result<Unit>
    suspend fun updateBottleneckField(id: String, fieldName: String, value: Any?): Result<Unit>
    suspend fun deleteBottleneck(id: String): Result<Unit>
    suspend fun batchInsertBottlenecks(bottlenecks: List<ErpBottleneck>): Result<Int>
}

/**
 * Production-ready Firestore Data Repository implementing CRUD operations for Process Bottlenecks.
 */
class FirestoreBottleneckRepository(
    private val customCollectionName: String = COLLECTION_NAME
) : BottleneckRepository {

    companion object {
        private const val TAG = "FirestoreBottleneckRepo"
        const val COLLECTION_NAME = "process_bottlenecks"
    }

    private fun getFirestore(): FirebaseFirestore? {
        return try {
            FirebaseFirestore.getInstance()
        } catch (e: Throwable) {
            Log.d(TAG, "Firestore instance not available: ${e.message}")
            null
        }
    }

    // =========================================================================
    // CREATE OPERATIONS
    // =========================================================================

    /**
     * Creates a new process bottleneck document in Firestore.
     * Uses the bottleneck's ID or auto-generates a new document ID if blank.
     */
    override suspend fun createBottleneck(bottleneck: ErpBottleneck): Result<String> = withContext(Dispatchers.IO) {
        try {
            val firestore = getFirestore() ?: return@withContext Result.failure(
                IllegalStateException("Firebase Firestore is not initialized")
            )

            val docId = if (bottleneck.id.isNotBlank()) bottleneck.id else "btn_${System.currentTimeMillis()}"
            val preparedBottleneck = bottleneck.copy(id = docId)
            val dataMap = bottleneckToMap(preparedBottleneck)

            firestore.collection(customCollectionName)
                .document(docId)
                .set(dataMap, SetOptions.merge())
                .await()

            Log.d(TAG, "Successfully created bottleneck: $docId")
            Result.success(docId)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating bottleneck: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Batch inserts a list of bottlenecks into Firestore using a Firestore WriteBatch.
     */
    override suspend fun batchInsertBottlenecks(bottlenecks: List<ErpBottleneck>): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val firestore = getFirestore() ?: return@withContext Result.failure(
                IllegalStateException("Firebase Firestore is not initialized")
            )

            if (bottlenecks.isEmpty()) return@withContext Result.success(0)

            // Firestore batches support up to 500 writes
            var insertedCount = 0
            val chunks = bottlenecks.chunked(450)

            for (chunk in chunks) {
                val batch = firestore.batch()
                for (item in chunk) {
                    val docId = if (item.id.isNotBlank()) item.id else "btn_${System.currentTimeMillis()}_${insertedCount++}"
                    val docRef = firestore.collection(customCollectionName).document(docId)
                    batch.set(docRef, bottleneckToMap(item.copy(id = docId)), SetOptions.merge())
                }
                batch.commit().await()
                insertedCount += chunk.size
            }

            Log.d(TAG, "Successfully batch inserted $insertedCount bottlenecks")
            Result.success(insertedCount)
        } catch (e: Exception) {
            Log.e(TAG, "Error in batchInsertBottlenecks: ${e.message}", e)
            Result.failure(e)
        }
    }

    // =========================================================================
    // READ OPERATIONS
    // =========================================================================

    /**
     * Fetches a single process bottleneck by its unique ID.
     */
    override suspend fun getBottleneckById(id: String): Result<ErpBottleneck?> = withContext(Dispatchers.IO) {
        try {
            val firestore = getFirestore() ?: return@withContext Result.failure(
                IllegalStateException("Firebase Firestore is not initialized")
            )

            val snapshot = firestore.collection(customCollectionName)
                .document(id)
                .get()
                .await()

            if (snapshot.exists()) {
                val bottleneck = documentToBottleneck(snapshot)
                Result.success(bottleneck)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting bottleneck by ID $id: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Observes a single bottleneck document in real-time.
     */
    override fun observeBottleneckById(id: String): Flow<ErpBottleneck?> = callbackFlow {
        val firestore = getFirestore()
        if (firestore == null) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val listener: ListenerRegistration = firestore.collection(customCollectionName)
            .document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Snapshot error for bottleneck $id: ${error.message}")
                    trySend(null)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    trySend(documentToBottleneck(snapshot))
                } else {
                    trySend(null)
                }
            }

        awaitClose {
            listener.remove()
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Streams all process bottlenecks in real-time from Firestore.
     */
    override fun getAllBottlenecks(): Flow<List<ErpBottleneck>> = callbackFlow {
        val firestore = getFirestore()
        if (firestore == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener: ListenerRegistration = firestore.collection(customCollectionName)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Error observing all bottlenecks: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val bottlenecks = snapshot.documents.mapNotNull { doc ->
                        documentToBottleneck(doc)
                    }
                    trySend(bottlenecks)
                }
            }

        awaitClose {
            listener.remove()
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Performs a one-shot query for all process bottlenecks.
     */
    override suspend fun getAllBottlenecksOnce(): Result<List<ErpBottleneck>> = withContext(Dispatchers.IO) {
        try {
            val firestore = getFirestore() ?: return@withContext Result.failure(
                IllegalStateException("Firebase Firestore is not initialized")
            )

            val snapshot = firestore.collection(customCollectionName)
                .orderBy("updatedAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val bottlenecks = snapshot.documents.mapNotNull { doc ->
                documentToBottleneck(doc)
            }
            Result.success(bottlenecks)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching bottlenecks once: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Streams bottlenecks filtered by domain in real-time.
     */
    override fun getBottlenecksByDomain(domain: BottleneckDomain): Flow<List<ErpBottleneck>> = callbackFlow {
        val firestore = getFirestore()
        if (firestore == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener: ListenerRegistration = firestore.collection(customCollectionName)
            .whereEqualTo("domain", domain.name)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Error filtering by domain ${domain.name}: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { documentToBottleneck(it) }
                    trySend(list)
                }
            }

        awaitClose {
            listener.remove()
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Streams bottlenecks filtered by severity level in real-time.
     */
    override fun getBottlenecksBySeverity(severity: SeverityLevel): Flow<List<ErpBottleneck>> = callbackFlow {
        val firestore = getFirestore()
        if (firestore == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener: ListenerRegistration = firestore.collection(customCollectionName)
            .whereEqualTo("severity", severity.name)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Error filtering by severity ${severity.name}: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { documentToBottleneck(it) }
                    trySend(list)
                }
            }

        awaitClose {
            listener.remove()
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Streams bottlenecks filtered by department in real-time.
     */
    override fun getBottlenecksByDepartment(department: String): Flow<List<ErpBottleneck>> = callbackFlow {
        val firestore = getFirestore()
        if (firestore == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener: ListenerRegistration = firestore.collection(customCollectionName)
            .whereEqualTo("department", department)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Error filtering by department $department: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { documentToBottleneck(it) }
                    trySend(list)
                }
            }

        awaitClose {
            listener.remove()
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Search bottlenecks matching text query across title, targetIndustry, and affected ERP systems.
     */
    override fun searchBottlenecks(query: String): Flow<List<ErpBottleneck>> {
        return getAllBottlenecks().map { list ->
            if (query.isBlank()) {
                list
            } else {
                val clean = query.trim().lowercase()
                list.filter { item ->
                    item.title.lowercase().contains(clean) ||
                            item.targetIndustry.lowercase().contains(clean) ||
                            item.department.lowercase().contains(clean) ||
                            item.affectedErpSystems.any { it.lowercase().contains(clean) } ||
                            item.suggestedVentureIdea.name.lowercase().contains(clean) ||
                            item.traditionalFlaw.lowercase().contains(clean) ||
                            item.frontierLogic.lowercase().contains(clean)
                }
            }
        }.flowOn(Dispatchers.Default)
    }

    // =========================================================================
    // UPDATE OPERATIONS
    // =========================================================================

    /**
     * Updates an entire process bottleneck document in Firestore.
     */
    override suspend fun updateBottleneck(bottleneck: ErpBottleneck): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val firestore = getFirestore() ?: return@withContext Result.failure(
                IllegalStateException("Firebase Firestore is not initialized")
            )

            val dataMap = bottleneckToMap(bottleneck)
            firestore.collection(customCollectionName)
                .document(bottleneck.id)
                .set(dataMap, SetOptions.merge())
                .await()

            Log.d(TAG, "Successfully updated bottleneck ${bottleneck.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating bottleneck ${bottleneck.id}: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Updates a single specific field in a bottleneck document.
     */
    override suspend fun updateBottleneckField(id: String, fieldName: String, value: Any?): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val firestore = getFirestore() ?: return@withContext Result.failure(
                IllegalStateException("Firebase Firestore is not initialized")
            )

            val updatePayload = mapOf(
                fieldName to value,
                "updatedAt" to System.currentTimeMillis()
            )

            firestore.collection(customCollectionName)
                .document(id)
                .update(updatePayload)
                .await()

            Log.d(TAG, "Successfully updated field '$fieldName' on bottleneck $id")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating field '$fieldName' on bottleneck $id: ${e.message}", e)
            Result.failure(e)
        }
    }

    // =========================================================================
    // DELETE OPERATIONS
    // =========================================================================

    /**
     * Deletes a process bottleneck document from Firestore.
     */
    override suspend fun deleteBottleneck(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val firestore = getFirestore() ?: return@withContext Result.failure(
                IllegalStateException("Firebase Firestore is not initialized")
            )

            firestore.collection(customCollectionName)
                .document(id)
                .delete()
                .await()

            Log.d(TAG, "Successfully deleted bottleneck $id")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting bottleneck $id: ${e.message}", e)
            Result.failure(e)
        }
    }

    // =========================================================================
    // SERIALIZATION / MAPPING HELPERS
    // =========================================================================

    private fun bottleneckToMap(b: ErpBottleneck): Map<String, Any?> {
        val venture = b.suggestedVentureIdea
        return mapOf(
            "id" to b.id,
            "title" to b.title,
            "domain" to b.domain.name,
            "severity" to b.severity.name,
            "problemScope" to b.problemScope.name,
            "department" to b.department,
            "affectedErpSystems" to b.affectedErpSystems,
            "targetIndustry" to b.targetIndustry,
            "traditionalMethod" to b.traditionalMethod,
            "traditionalFlaw" to b.traditionalFlaw,
            "frontierLogic" to b.frontierLogic,
            "adoptionFriction" to b.adoptionFriction,
            "annualIndustryWasteMillions" to b.annualIndustryWasteMillions,
            "potentialEfficiencyGainPercent" to b.potentialEfficiencyGainPercent,
            "venture" to mapOf(
                "id" to venture.id,
                "name" to venture.name,
                "tagline" to venture.tagline,
                "category" to venture.category,
                "oneSentencePitch" to venture.oneSentencePitch,
                "coreMoat" to venture.coreMoat,
                "targetIcp" to venture.targetIcp,
                "beachheadMarket" to venture.beachheadMarket,
                "frictionBypassStrategy" to venture.frictionBypassStrategy,
                "architectureSteps" to venture.architectureSteps.map { step ->
                    mapOf(
                        "stepNumber" to step.stepNumber,
                        "layerName" to step.layerName,
                        "description" to step.description,
                        "techStack" to step.techStack
                    )
                },
                "pitchDeck" to mapOf(
                    "title" to venture.pitchDeck.title,
                    "subtitle" to venture.pitchDeck.subtitle,
                    "founderName" to venture.pitchDeck.founderName,
                    "fundingStage" to venture.pitchDeck.fundingStage,
                    "targetRaiseAmountMillions" to venture.pitchDeck.targetRaiseAmountMillions,
                    "slides" to venture.pitchDeck.slides.map { slide ->
                        mapOf(
                            "slideNumber" to slide.slideNumber,
                            "title" to slide.title,
                            "subtitle" to slide.subtitle,
                            "keyPoints" to slide.keyPoints,
                            "metricHighlight" to slide.metricHighlight,
                            "metricLabel" to slide.metricLabel,
                            "visualType" to slide.visualType.name,
                            "presenterNotes" to slide.presenterNotes
                        )
                    }
                ),
                "valuationReport" to mapOf(
                    "ventureName" to venture.valuationReport.ventureName,
                    "postMoneySeedValuationMillions" to venture.valuationReport.postMoneySeedValuationMillions,
                    "seriesATargetValuationMillions" to venture.valuationReport.seriesATargetValuationMillions,
                    "year3ProjectedValuationMillions" to venture.valuationReport.year3ProjectedValuationMillions,
                    "year5ProjectedValuationMillions" to venture.valuationReport.year5ProjectedValuationMillions,
                    "valuationSummaryNotes" to venture.valuationReport.valuationSummaryNotes,
                    "unitEconomics" to mapOf(
                        "targetEnterpriseAcvThousands" to venture.valuationReport.unitEconomics.targetEnterpriseAcvThousands,
                        "customerAcquisitionCostThousands" to venture.valuationReport.unitEconomics.customerAcquisitionCostThousands,
                        "customerLifetimeYears" to venture.valuationReport.unitEconomics.customerLifetimeYears,
                        "ltvThousands" to venture.valuationReport.unitEconomics.ltvThousands,
                        "ltvToCacRatio" to venture.valuationReport.unitEconomics.ltvToCacRatio,
                        "paybackPeriodMonths" to venture.valuationReport.unitEconomics.paybackPeriodMonths,
                        "netRevenueRetentionPercent" to venture.valuationReport.unitEconomics.netRevenueRetentionPercent
                    ),
                    "customerRoi" to mapOf(
                        "annualClientCostSavingsMillions" to venture.valuationReport.customerRoi.annualClientCostSavingsMillions,
                        "implementationTimeWeeks" to venture.valuationReport.customerRoi.implementationTimeWeeks,
                        "enterpriseRoiMultiple" to venture.valuationReport.customerRoi.enterpriseRoiMultiple,
                        "paybackDays" to venture.valuationReport.customerRoi.paybackDays
                    )
                )
            ),
            "verificationSource" to b.verificationSource?.let { src ->
                mapOf(
                    "primarySystemDoc" to src.primarySystemDoc,
                    "verifiedEndpointUrl" to src.verifiedEndpointUrl,
                    "secondaryValidationMethod" to src.secondaryValidationMethod,
                    "verificationAuditTimestamp" to src.verificationAuditTimestamp,
                    "auditConfidenceScore" to src.auditConfidenceScore
                )
            },
            "updatedAt" to System.currentTimeMillis()
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun documentToBottleneck(doc: DocumentSnapshot): ErpBottleneck? {
        return try {
            val data = doc.data ?: return null
            val id = doc.id
            val title = data["title"] as? String ?: "Untitled Process Bottleneck"
            val domainStr = data["domain"] as? String ?: BottleneckDomain.ERP_LOGIC.name
            val domain = try { BottleneckDomain.valueOf(domainStr) } catch (e: Exception) { BottleneckDomain.ERP_LOGIC }
            val severityStr = data["severity"] as? String ?: SeverityLevel.HIGH.name
            val severity = try { SeverityLevel.valueOf(severityStr) } catch (e: Exception) { SeverityLevel.HIGH }
            val scopeStr = data["problemScope"] as? String ?: ProblemScope.MODULAR_BOTTLENECK.name
            val problemScope = try { ProblemScope.valueOf(scopeStr) } catch (e: Exception) { ProblemScope.MODULAR_BOTTLENECK }
            val department = data["department"] as? String ?: "Manufacturing & Operations"
            val affectedErp = (data["affectedErpSystems"] as? List<String>) ?: listOf("SAP S/4HANA", "Oracle Cloud ERP")
            val targetIndustry = data["targetIndustry"] as? String ?: "Enterprise Manufacturing"
            val traditionalMethod = data["traditionalMethod"] as? String ?: ""
            val traditionalFlaw = data["traditionalFlaw"] as? String ?: ""
            val frontierLogic = data["frontierLogic"] as? String ?: ""
            val adoptionFriction = data["adoptionFriction"] as? String ?: ""
            val annualWaste = (data["annualIndustryWasteMillions"] as? Number)?.toDouble() ?: 240.0
            val efficiencyGain = (data["potentialEfficiencyGainPercent"] as? Number)?.toDouble() ?: 35.0

            // Parse Venture
            val ventureMap = data["venture"] as? Map<String, Any?>
            val ventureId = (ventureMap?.get("id") as? String) ?: "vnt_$id"
            val ventureName = (ventureMap?.get("name") as? String) ?: title
            val tagline = (ventureMap?.get("tagline") as? String) ?: "Next-Gen Process Automation"
            val category = (ventureMap?.get("category") as? String) ?: "Enterprise AI"
            val oneSentencePitch = (ventureMap?.get("oneSentencePitch") as? String) ?: ""
            val coreMoat = (ventureMap?.get("coreMoat") as? String) ?: "Proprietary Shadow Telemetry"
            val targetIcp = (ventureMap?.get("targetIcp") as? String) ?: "VP of Operations"
            val beachheadMarket = (ventureMap?.get("beachheadMarket") as? String) ?: targetIndustry
            val frictionBypassStrategy = (ventureMap?.get("frictionBypassStrategy") as? String) ?: "Non-invasive sidecar architecture"

            // Architecture steps
            val rawSteps = ventureMap?.get("architectureSteps") as? List<Map<String, Any?>>
            val architectureSteps = rawSteps?.mapIndexed { idx, sMap ->
                ArchitectureStep(
                    stepNumber = (sMap["stepNumber"] as? Number)?.toInt() ?: (idx + 1),
                    layerName = (sMap["layerName"] as? String) ?: "Layer ${idx + 1}",
                    description = (sMap["description"] as? String) ?: "",
                    techStack = (sMap["techStack"] as? String) ?: "gRPC / Kafka / Kotlin"
                )
            } ?: listOf(
                ArchitectureStep(1, "Telemetry Sidecar", "Captures live ERP events", "Debezium / Apache Kafka"),
                ArchitectureStep(2, "Frontier Inference Core", "Processes real-time constraint graphs", "C++ / ONNX / Ray"),
                ArchitectureStep(3, "Reconciliation Adapter", "Synchronizes validated state back to ERP", "OData / BAPI / REST")
            )

            // Pitch deck
            val pitchMap = ventureMap?.get("pitchDeck") as? Map<String, Any?>
            val rawSlides = pitchMap?.get("slides") as? List<Map<String, Any?>>
            val slides = rawSlides?.mapIndexed { sIdx, slMap ->
                val visualTypeStr = slMap["visualType"] as? String ?: SlideVisualType.BULLETS.name
                val visualType = try { SlideVisualType.valueOf(visualTypeStr) } catch (e: Exception) { SlideVisualType.BULLETS }
                PitchDeckSlide(
                    slideNumber = (slMap["slideNumber"] as? Number)?.toInt() ?: (sIdx + 1),
                    title = (slMap["title"] as? String) ?: "Executive Slide",
                    subtitle = (slMap["subtitle"] as? String) ?: "",
                    keyPoints = (slMap["keyPoints"] as? List<String>) ?: emptyList(),
                    metricHighlight = slMap["metricHighlight"] as? String,
                    metricLabel = slMap["metricLabel"] as? String,
                    visualType = visualType,
                    presenterNotes = (slMap["presenterNotes"] as? String) ?: ""
                )
            } ?: emptyList()

            val pitchDeck = PitchDeck(
                title = (pitchMap?.get("title") as? String) ?: "$ventureName Investor Deck",
                subtitle = (pitchMap?.get("subtitle") as? String) ?: tagline,
                founderName = (pitchMap?.get("founderName") as? String) ?: "Enterprise Founders",
                fundingStage = (pitchMap?.get("fundingStage") as? String) ?: "Seed Round",
                targetRaiseAmountMillions = (pitchMap?.get("targetRaiseAmountMillions") as? Number)?.toDouble() ?: 3.5,
                slides = slides
            )

            // Valuation report
            val valMap = ventureMap?.get("valuationReport") as? Map<String, Any?>
            val unitMap = valMap?.get("unitEconomics") as? Map<String, Any?>
            val roiMap = valMap?.get("customerRoi") as? Map<String, Any?>

            val unitEconomics = UnitEconomics(
                targetEnterpriseAcvThousands = (unitMap?.get("targetEnterpriseAcvThousands") as? Number)?.toDouble() ?: 180.0,
                customerAcquisitionCostThousands = (unitMap?.get("customerAcquisitionCostThousands") as? Number)?.toDouble() ?: 45.0,
                customerLifetimeYears = (unitMap?.get("customerLifetimeYears") as? Number)?.toDouble() ?: 7.5,
                ltvThousands = (unitMap?.get("ltvThousands") as? Number)?.toDouble() ?: 1147.5,
                ltvToCacRatio = (unitMap?.get("ltvToCacRatio") as? Number)?.toDouble() ?: 6.3,
                paybackPeriodMonths = (unitMap?.get("paybackPeriodMonths") as? Number)?.toInt() ?: 8,
                netRevenueRetentionPercent = (unitMap?.get("netRevenueRetentionPercent") as? Number)?.toDouble() ?: 132.0
            )

            val customerRoi = CustomerRoiAnalysis(
                annualClientCostSavingsMillions = (roiMap?.get("annualClientCostSavingsMillions") as? Number)?.toDouble() ?: 4.2,
                implementationTimeWeeks = (roiMap?.get("implementationTimeWeeks") as? Number)?.toInt() ?: 4,
                enterpriseRoiMultiple = (roiMap?.get("enterpriseRoiMultiple") as? Number)?.toDouble() ?: 8.4,
                paybackDays = (roiMap?.get("paybackDays") as? Number)?.toInt() ?: 60
            )

            val valuationReport = ValuationReport(
                ventureName = (valMap?.get("ventureName") as? String) ?: ventureName,
                postMoneySeedValuationMillions = (valMap?.get("postMoneySeedValuationMillions") as? Number)?.toDouble() ?: 14.0,
                seriesATargetValuationMillions = (valMap?.get("seriesATargetValuationMillions") as? Number)?.toDouble() ?: 48.0,
                year3ProjectedValuationMillions = (valMap?.get("year3ProjectedValuationMillions") as? Number)?.toDouble() ?: 85.0,
                year5ProjectedValuationMillions = (valMap?.get("year5ProjectedValuationMillions") as? Number)?.toDouble() ?: 240.0,
                valuationMethodologiesUsed = listOf("Forward ARR Multiple (12.0x)", "DCF with 14% WACC", "First Chicago VC Method"),
                unitEconomics = unitEconomics,
                customerRoi = customerRoi,
                fiveYearFinancials = emptyList(),
                sensitivityScenarios = emptyList(),
                valuationSummaryNotes = (valMap?.get("valuationSummaryNotes") as? String) ?: "Top quartile SaaS unit economics with net retention > 130%."
            )

            val startupVenture = StartupVenture(
                id = ventureId,
                name = ventureName,
                tagline = tagline,
                category = category,
                oneSentencePitch = oneSentencePitch,
                coreMoat = coreMoat,
                architectureSteps = architectureSteps,
                targetIcp = targetIcp,
                beachheadMarket = beachheadMarket,
                frictionBypassStrategy = frictionBypassStrategy,
                pitchDeck = pitchDeck,
                valuationReport = valuationReport
            )

            // Verification source
            val verMap = data["verificationSource"] as? Map<String, Any?>
            val verificationSource = verMap?.let {
                EndpointVerificationSource(
                    primarySystemDoc = (it["primarySystemDoc"] as? String) ?: "Enterprise API Doc",
                    verifiedEndpointUrl = (it["verifiedEndpointUrl"] as? String) ?: "",
                    secondaryValidationMethod = (it["secondaryValidationMethod"] as? String) ?: "Transaction Trace",
                    verificationAuditTimestamp = (it["verificationAuditTimestamp"] as? String) ?: "2026-Q1",
                    auditConfidenceScore = (it["auditConfidenceScore"] as? Number)?.toDouble() ?: 98.5
                )
            }

            ErpBottleneck(
                id = id,
                title = title,
                domain = domain,
                severity = severity,
                problemScope = problemScope,
                department = department,
                affectedErpSystems = affectedErp,
                targetIndustry = targetIndustry,
                traditionalMethod = traditionalMethod,
                traditionalFlaw = traditionalFlaw,
                frontierLogic = frontierLogic,
                adoptionFriction = adoptionFriction,
                annualIndustryWasteMillions = annualWaste,
                potentialEfficiencyGainPercent = efficiencyGain,
                suggestedVentureIdea = startupVenture,
                verificationSource = verificationSource
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error deserializing bottleneck document ${doc.id}: ${e.message}", e)
            null
        }
    }
}
