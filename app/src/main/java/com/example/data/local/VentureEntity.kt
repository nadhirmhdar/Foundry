package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_ventures")
data class VentureEntity(
    @PrimaryKey val id: String,
    val ventureName: String,
    val tagline: String,
    val category: String,
    val targetIndustry: String,
    val bottleneckTitle: String,
    val domain: String,
    val severity: String,
    val traditionalFlaw: String,
    val frontierLogic: String,
    val seedValuationMillions: Double,
    val year3ValuationMillions: Double,
    val targetRaiseMillions: Double,
    val pitchDeckJson: String,
    val valuationJson: String,
    val architectureJson: String,
    val isCustomAiGenerated: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
