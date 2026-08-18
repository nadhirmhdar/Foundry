package com.example.data.model

data class SearchGroundingSource(
    val title: String,
    val url: String,
    val domain: String = extractDomain(url)
)

private fun extractDomain(url: String): String {
    return try {
        val clean = url.removePrefix("https://").removePrefix("http://").removePrefix("www.")
        clean.substringBefore("/")
    } catch (e: Exception) {
        url
    }
}

data class GroundedMarketIntelligenceResult(
    val query: String,
    val domain: String,
    val synthesisText: String,
    val keySignals: List<String>,
    val verifiedMultiples: List<String>,
    val searchQueriesUsed: List<String>,
    val sources: List<SearchGroundingSource>,
    val timestamp: Long = System.currentTimeMillis()
)
