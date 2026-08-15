package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VentureDao {
    @Query("SELECT * FROM saved_ventures ORDER BY createdAt DESC")
    fun getAllSavedVentures(): Flow<List<VentureEntity>>

    @Query("SELECT * FROM saved_ventures WHERE id = :id LIMIT 1")
    suspend fun getVentureById(id: String): VentureEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVenture(venture: VentureEntity)

    @Query("DELETE FROM saved_ventures WHERE id = :id")
    suspend fun deleteVentureById(id: String)

    @Query("SELECT COUNT(*) FROM saved_ventures")
    fun getSavedCount(): Flow<Int>
}
