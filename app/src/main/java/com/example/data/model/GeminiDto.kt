package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val generationConfig: GeminiGenerationConfig? = null,
    val systemInstruction: GeminiContent? = null,
    val tools: List<GeminiTool>? = null
)

@JsonClass(generateAdapter = true)
data class GeminiTool(
    val googleSearch: Map<String, String>? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    val parts: List<GeminiPart>,
    val role: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiPart(
    val text: String? = null,
    val inlineData: GeminiInlineData? = null
)

@JsonClass(generateAdapter = true)
data class GeminiInlineData(
    val mimeType: String,
    val data: String
)

@JsonClass(generateAdapter = true)
data class GeminiThinkingConfig(
    val thinkingLevel: String = "HIGH"
)

@JsonClass(generateAdapter = true)
data class GeminiImageConfig(
    val aspectRatio: String = "1:1",
    val imageSize: String = "1K"
)

@JsonClass(generateAdapter = true)
data class GeminiGenerationConfig(
    val temperature: Float? = null,
    val topP: Float? = null,
    val topK: Int? = null,
    val thinkingConfig: GeminiThinkingConfig? = null,
    val imageConfig: GeminiImageConfig? = null,
    val responseModalities: List<String>? = null
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<GeminiCandidate>? = null,
    val usageMetadata: GeminiUsageMetadata? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    val content: GeminiContent? = null,
    val finishReason: String? = null,
    val groundingMetadata: GeminiGroundingMetadata? = null
)

@JsonClass(generateAdapter = true)
data class GeminiGroundingMetadata(
    val webSearchQueries: List<String>? = null,
    val searchEntryPoint: GeminiSearchEntryPoint? = null,
    val groundingChunks: List<GeminiGroundingChunk>? = null
)

@JsonClass(generateAdapter = true)
data class GeminiSearchEntryPoint(
    val renderedContent: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiGroundingChunk(
    val web: GeminiWebSource? = null
)

@JsonClass(generateAdapter = true)
data class GeminiWebSource(
    val uri: String? = null,
    val title: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiUsageMetadata(
    val promptTokenCount: Int? = null,
    val candidatesTokenCount: Int? = null,
    val totalTokenCount: Int? = null,
    val thoughtsTokenCount: Int? = null
)

// --- Veo 3 Video Generation Models ---

@JsonClass(generateAdapter = true)
data class VeoGenerateVideosRequest(
    val prompt: String,
    val config: VeoConfig? = null
)

@JsonClass(generateAdapter = true)
data class VeoConfig(
    val numberOfVideos: Int = 1,
    val resolution: String = "1080p",
    val aspectRatio: String = "16:9"
)

@JsonClass(generateAdapter = true)
data class VeoOperationResponse(
    val name: String? = null,
    val done: Boolean? = null
)
