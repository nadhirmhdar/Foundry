package com.example

import com.example.data.model.PitchPerspective
import com.example.data.repository.CuratedBottlenecksData
import com.example.data.repository.PitchDeckArchitectSynthesizer
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testPitchDeckArchitectSynthesis_allFourPillarsGenerated() {
        val sampleBottleneck = CuratedBottlenecksData.getAll().first()
        val blueprint = PitchDeckArchitectSynthesizer.synthesizeBlueprint(
            bottleneck = sampleBottleneck,
            perspective = PitchPerspective.VENTURE_CAPITAL
        )

        assertNotNull(blueprint)
        assertEquals(4, blueprint.allSections.size)
        assertTrue(blueprint.problemSection.executiveSummary.isNotBlank())
        assertTrue(blueprint.solutionSection.executiveSummary.isNotBlank())
        assertTrue(blueprint.marketSection.executiveSummary.isNotBlank())
        assertTrue(blueprint.financialsSection.executiveSummary.isNotBlank())

        assertEquals(4, blueprint.problemSection.items.size)
        assertEquals(4, blueprint.solutionSection.items.size)
        assertEquals(4, blueprint.marketSection.items.size)
        assertEquals(4, blueprint.financialsSection.items.size)

        val markdown = blueprint.toFormattedMarkdown()
        assertTrue(markdown.contains("Pitch Deck Architect Blueprint"))
        assertTrue(markdown.contains("Problem & Core Inefficiency"))
        assertTrue(markdown.contains("Frontier Solution Architecture"))
        assertTrue(markdown.contains("Market Opportunity & Moat"))
        assertTrue(markdown.contains("Venture Financials & Economics"))
    }

    @Test
    fun testCognitiveLoadSynthesizer_generates10ConcisePoints() {
        val sampleBottleneck = CuratedBottlenecksData.getAll().first()
        val briefing = com.example.data.repository.CognitiveLoadSynthesizer.synthesizeBriefing(sampleBottleneck)

        assertNotNull(briefing)
        assertEquals(10, briefing.points.size)
        assertTrue(briefing.estimatedReadTimeSeconds in 20..60)
        assertTrue(briefing.signalToNoiseRatioPercent > 90.0)

        // Verify each point has a non-blank one-liner lead and key category
        briefing.points.forEachIndexed { index, point ->
            assertEquals(index + 1, point.index)
            assertTrue(point.categoryTag.isNotBlank())
            assertTrue(point.oneLinerLead.isNotBlank())
            assertTrue(point.executiveSummary.isNotBlank())
        }

        val markdown = com.example.data.repository.CognitiveLoadSynthesizer.generate10BulletMarkdown(briefing)
        assertTrue(markdown.contains("10-Point Executive Cognitive Briefing"))
        assertTrue(markdown.contains("01."))
        assertTrue(markdown.contains("10."))
    }
}

