package com.example.data.remote

import com.example.data.model.GeminiRequest
import com.example.data.model.GeminiResponse
import com.example.data.model.VeoGenerateVideosRequest
import com.example.data.model.VeoOperationResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface GeminiApiService {
    // Standard fast generation (gemini-3.5-flash)
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateVentureAnalysis(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse

    // High Thinking Mode with gemini-3.1-pro-preview
    @POST("v1beta/models/gemini-3.1-pro-preview:generateContent")
    suspend fun generateHighThinkingAnalysis(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse

    // Image Creation & Editing with gemini-3.1-flash-image-preview
    @POST("v1beta/models/gemini-3.1-flash-image-preview:generateContent")
    suspend fun generateOrEditImage(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse

    // Veo 3 Video Generation with veo-3.1-fast-generate-preview
    @POST("v1beta/models/veo-3.1-fast-generate-preview:generateVideos")
    suspend fun generateVeoVideo(
        @Query("key") apiKey: String,
        @Body request: VeoGenerateVideosRequest
    ): VeoOperationResponse

    // Veo Operation Status Check
    @GET("v1beta/{operationName}")
    suspend fun getVeoOperationStatus(
        @Path(value = "operationName", encoded = true) operationName: String,
        @Query("key") apiKey: String
    ): Map<String, Any>
}
