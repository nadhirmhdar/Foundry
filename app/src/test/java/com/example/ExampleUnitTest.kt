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
}

