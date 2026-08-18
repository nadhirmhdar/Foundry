package com.example.data.sync

import android.util.Log
import com.example.data.local.VentureDao
import com.example.data.local.VentureEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

enum class SyncStatus {
    IDLE,
    SYNCING,
    SYNCED,
    OFFLINE,
    ERROR
}

data class SyncInfo(
    val status: SyncStatus = SyncStatus.IDLE,
    val lastSyncTimestamp: Long? = null,
    val message: String = "Ready to sync with Cloud Firestore",
    val syncedCount: Int = 0
)

class FirestoreSyncService(
    private val ventureDao: VentureDao,
    private val scope: CoroutineScope
) {
    companion object {
        private const val TAG = "FirestoreSyncService"
        private const val COLLECTION_NAME = "saved_ventures"
    }

    private fun getFirestoreInstance(): FirebaseFirestore? {
        return try {
            FirebaseFirestore.getInstance()
        } catch (e: Throwable) {
            Log.d(TAG, "Firestore instance not available yet: ${e.message}")
            null
        }
    }

    private val _syncInfo = MutableStateFlow(SyncInfo())
    val syncInfo: StateFlow<SyncInfo> = _syncInfo.asStateFlow()

    private var realtimeListener: ListenerRegistration? = null

    /**
     * Performs a full bi-directional synchronization between local Room DB and Cloud Firestore.
     */
    suspend fun syncAll(): Result<Int> = withContext(Dispatchers.IO) {
        _syncInfo.update {
            it.copy(status = SyncStatus.SYNCING, message = "Synchronizing with Cloud Firestore...")
        }

        try {
            val db = getFirestoreInstance()
            if (db == null) {
                _syncInfo.update {
                    it.copy(
                        status = SyncStatus.OFFLINE,
                        message = "Local Room DB Active (Cloud sync ready)"
                    )
                }
                return@withContext Result.success(0)
            }

            val localVentures = ventureDao.getAllSavedVenturesList()
            val remoteSnapshot = db.collection(COLLECTION_NAME).get().await()

            val remoteVenturesMap = mutableMapOf<String, VentureEntity>()
            for (doc in remoteSnapshot.documents) {
                val entity = docToVentureEntity(doc.id, doc.data)
                if (entity != null) {
                    remoteVenturesMap[entity.id] = entity
                }
            }

            // 1. Push local ventures to Firestore if not present or updated
            for (local in localVentures) {
                val remote = remoteVenturesMap[local.id]
                if (remote == null || local.createdAt >= remote.createdAt) {
                    db.collection(COLLECTION_NAME)
                        .document(local.id)
                        .set(ventureEntityToMap(local), SetOptions.merge())
                        .await()
                }
            }

            // 2. Insert any remote ventures into local Room that are missing locally
            val localIds = localVentures.map { it.id }.toSet()
            val newFromRemote = remoteVenturesMap.values.filter { it.id !in localIds }
            if (newFromRemote.isNotEmpty()) {
                ventureDao.insertVentures(newFromRemote)
            }

            val totalSynced = (localVentures.map { it.id } + remoteVenturesMap.keys).distinct().size

            _syncInfo.update {
                it.copy(
                    status = SyncStatus.SYNCED,
                    lastSyncTimestamp = System.currentTimeMillis(),
                    message = "Cloud sync active ($totalSynced models unified)",
                    syncedCount = totalSynced
                )
            }
            Log.d(TAG, "Sync complete. Unified $totalSynced venture models.")
            Result.success(totalSynced)
        } catch (e: Exception) {
            Log.w(TAG, "Firestore sync failed or device offline: ${e.message}", e)
            val isNetworkIssue = e.message?.contains("network", ignoreCase = true) == true ||
                    e.message?.contains("offline", ignoreCase = true) == true ||
                    e.message?.contains("unavailable", ignoreCase = true) == true ||
                    e.message?.contains("FirebaseApp", ignoreCase = true) == true

            _syncInfo.update {
                it.copy(
                    status = if (isNetworkIssue) SyncStatus.OFFLINE else SyncStatus.ERROR,
                    message = if (isNetworkIssue) "Working offline (Room local active)" else (e.localizedMessage ?: "Sync error")
                )
            }
            Result.failure(e)
        }
    }

    /**
     * Pushes a single saved venture entity directly to Firestore.
     */
    suspend fun pushVenture(venture: VentureEntity) = withContext(Dispatchers.IO) {
        try {
            val db = getFirestoreInstance() ?: return@withContext
            db.collection(COLLECTION_NAME)
                .document(venture.id)
                .set(ventureEntityToMap(venture), SetOptions.merge())
                .await()
            Log.d(TAG, "Pushed venture ${venture.id} to Firestore")
        } catch (e: Exception) {
            Log.w(TAG, "Could not push venture ${venture.id} to Firestore (offline fallback)", e)
        }
    }

    /**
     * Deletes a venture entity from Firestore.
     */
    suspend fun deleteVenture(ventureId: String) = withContext(Dispatchers.IO) {
        try {
            val db = getFirestoreInstance() ?: return@withContext
            db.collection(COLLECTION_NAME)
                .document(ventureId)
                .delete()
                .await()
            Log.d(TAG, "Deleted venture $ventureId from Firestore")
        } catch (e: Exception) {
            Log.w(TAG, "Could not delete venture $ventureId from Firestore", e)
        }
    }

    /**
     * Starts a realtime listener to automatically receive new ventures added from other devices.
     */
    fun startRealtimeListener() {
        if (realtimeListener != null) return

        try {
            val db = getFirestoreInstance() ?: return
            realtimeListener = db.collection(COLLECTION_NAME)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "Realtime listener error: ${error.message}")
                        return@addSnapshotListener
                    }

                    if (snapshot != null && !snapshot.isEmpty) {
                        scope.launch(Dispatchers.IO) {
                            val remoteList = mutableListOf<VentureEntity>()
                            for (doc in snapshot.documents) {
                                val entity = docToVentureEntity(doc.id, doc.data)
                                if (entity != null) {
                                    remoteList.add(entity)
                                }
                            }
                            if (remoteList.isNotEmpty()) {
                                ventureDao.insertVentures(remoteList)
                                _syncInfo.update {
                                    it.copy(
                                        status = SyncStatus.SYNCED,
                                        lastSyncTimestamp = System.currentTimeMillis(),
                                        message = "Cloud synced in real-time (${remoteList.size} models)",
                                        syncedCount = remoteList.size
                                    )
                                }
                            }
                        }
                    }
                }
        } catch (e: Exception) {
            Log.w(TAG, "Unable to attach realtime listener: ${e.message}")
        }
    }

    fun stopRealtimeListener() {
        realtimeListener?.remove()
        realtimeListener = null
    }

    private fun ventureEntityToMap(v: VentureEntity): Map<String, Any> {
        return mapOf(
            "id" to v.id,
            "ventureName" to v.ventureName,
            "tagline" to v.tagline,
            "category" to v.category,
            "targetIndustry" to v.targetIndustry,
            "bottleneckTitle" to v.bottleneckTitle,
            "domain" to v.domain,
            "severity" to v.severity,
            "traditionalFlaw" to v.traditionalFlaw,
            "frontierLogic" to v.frontierLogic,
            "seedValuationMillions" to v.seedValuationMillions,
            "year3ValuationMillions" to v.year3ValuationMillions,
            "targetRaiseMillions" to v.targetRaiseMillions,
            "pitchDeckJson" to v.pitchDeckJson,
            "valuationJson" to v.valuationJson,
            "architectureJson" to v.architectureJson,
            "isCustomAiGenerated" to v.isCustomAiGenerated,
            "createdAt" to v.createdAt,
            "updatedAt" to System.currentTimeMillis()
        )
    }

    private fun docToVentureEntity(id: String, data: Map<String, Any>?): VentureEntity? {
        if (data == null) return null
        return try {
            VentureEntity(
                id = id,
                ventureName = (data["ventureName"] as? String) ?: "Unknown Venture",
                tagline = (data["tagline"] as? String) ?: "",
                category = (data["category"] as? String) ?: "Enterprise AI",
                targetIndustry = (data["targetIndustry"] as? String) ?: "",
                bottleneckTitle = (data["bottleneckTitle"] as? String) ?: "",
                domain = (data["domain"] as? String) ?: "ERP_LOGIC",
                severity = (data["severity"] as? String) ?: "HIGH",
                traditionalFlaw = (data["traditionalFlaw"] as? String) ?: "",
                frontierLogic = (data["frontierLogic"] as? String) ?: "",
                seedValuationMillions = (data["seedValuationMillions"] as? Number)?.toDouble() ?: 12.0,
                year3ValuationMillions = (data["year3ValuationMillions"] as? Number)?.toDouble() ?: 80.0,
                targetRaiseMillions = (data["targetRaiseMillions"] as? Number)?.toDouble() ?: 3.0,
                pitchDeckJson = (data["pitchDeckJson"] as? String) ?: "",
                valuationJson = (data["valuationJson"] as? String) ?: "",
                architectureJson = (data["architectureJson"] as? String) ?: "",
                isCustomAiGenerated = (data["isCustomAiGenerated"] as? Boolean) ?: false,
                createdAt = (data["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing doc $id to VentureEntity", e)
            null
        }
    }
}
