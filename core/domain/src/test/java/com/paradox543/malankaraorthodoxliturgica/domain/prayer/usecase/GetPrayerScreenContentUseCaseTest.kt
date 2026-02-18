package com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.fakes.FakeCalendarRepository
import com.paradox543.malankaraorthodoxliturgica.domain.fakes.FakePrayerRepository
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElementDomain
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerLinkDepthExceededException
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GetPrayerScreenContentUseCaseTest {
    private fun makeUseCase(elementsMap: Map<String, List<PrayerElementDomain>>): GetPrayerScreenContentUseCase {
        val prayerRepo = FakePrayerRepository(elementsMap = elementsMap)
        val calendarRepo = FakeCalendarRepository()
        val dynamicUseCase = GetDynamicSongsUseCase(prayerRepo, calendarRepo)
        return GetPrayerScreenContentUseCase(prayerRepo, dynamicUseCase)
    }

    @Test
    fun `inlines Link elements by loading and flattening the linked file`() =
        runBlocking {
            val elementsMap =
                mapOf(
                    "main.json" to listOf(PrayerElementDomain.Link("linked.json")),
                    "linked.json" to listOf(
                        PrayerElementDomain.Title("Linked Title"),
                        PrayerElementDomain.Prose("Some prose"),
                    ),
                )
            val result = makeUseCase(elementsMap)("main.json", AppLanguage.ENGLISH)

            assertTrue(result.any { it is PrayerElementDomain.Title && it.content == "Linked Title" })
            assertTrue(result.any { it is PrayerElementDomain.Prose && it.content == "Some prose" })
        }

    @Test
    fun `LinkCollapsible extracts Title as collapsible heading`() =
        runBlocking {
            val elementsMap =
                mapOf(
                    "main.json" to listOf(PrayerElementDomain.LinkCollapsible("linked.json")),
                    "linked.json" to listOf(
                        PrayerElementDomain.Title("Section Title"),
                        PrayerElementDomain.Prose("Body text"),
                    ),
                )
            val result = makeUseCase(elementsMap)("main.json", AppLanguage.ENGLISH)

            val block = result.filterIsInstance<PrayerElementDomain.CollapsibleBlock>().firstOrNull()
            assertNotNull(block)
            assertEquals("Section Title", block!!.title)
            assertTrue(block.items.any { it is PrayerElementDomain.Prose && it.content == "Body text" })
        }

    @Test
    fun `LinkCollapsible falls back to Heading when no Title present`() =
        runBlocking {
            val elementsMap =
                mapOf(
                    "main.json" to listOf(PrayerElementDomain.LinkCollapsible("linked.json")),
                    "linked.json" to listOf(
                        PrayerElementDomain.Heading("Section Heading"),
                        PrayerElementDomain.Prose("Body text"),
                    ),
                )
            val result = makeUseCase(elementsMap)("main.json", AppLanguage.ENGLISH)

            val block = result.filterIsInstance<PrayerElementDomain.CollapsibleBlock>().firstOrNull()
            assertNotNull(block)
            assertEquals("Section Heading", block!!.title)
        }

    @Test
    fun `LinkCollapsible uses default title when no Title or Heading present`() =
        runBlocking {
            val elementsMap =
                mapOf(
                    "main.json" to listOf(PrayerElementDomain.LinkCollapsible("linked.json")),
                    "linked.json" to listOf(PrayerElementDomain.Prose("Body text")),
                )
            val result = makeUseCase(elementsMap)("main.json", AppLanguage.ENGLISH)

            val block = result.filterIsInstance<PrayerElementDomain.CollapsibleBlock>().firstOrNull()
            assertNotNull(block)
            assertEquals("Expandable Block", block!!.title)
        }

    @Test
    fun `LinkCollapsible emits Error when linked file has no displayable items`() =
        runBlocking {
            val elementsMap =
                mapOf(
                    "main.json" to listOf(PrayerElementDomain.LinkCollapsible("linked.json")),
                    // linked.json only has a Title, which is consumed as the heading, leaving no items
                    "linked.json" to listOf(PrayerElementDomain.Title("Only Title")),
                )
            val result = makeUseCase(elementsMap)("main.json", AppLanguage.ENGLISH)

            assertTrue(result.any { it is PrayerElementDomain.Error })
        }

    @Test
    fun `Link emits Error when linked file is not found`() =
        runBlocking {
            val elementsMap =
                mapOf("main.json" to listOf(PrayerElementDomain.Link("missing.json")))
            // missing.json is not in the map, so loadPrayerElements returns emptyList — no error expected
            // But if we throw, it should be caught. Use a repo that throws for missing keys.
            val prayerRepo = FakePrayerRepository(
                elementsMap = elementsMap,
                throwOnMissing = true,
            )
            val calendarRepo = FakeCalendarRepository()
            val dynamicUseCase = GetDynamicSongsUseCase(prayerRepo, calendarRepo)
            val useCase = GetPrayerScreenContentUseCase(prayerRepo, dynamicUseCase)

            val result = useCase("main.json", AppLanguage.ENGLISH)

            assertTrue(result.any { it is PrayerElementDomain.Error })
        }

    @Test
    fun `throws PrayerLinkDepthExceededException when max depth exceeded`() =
        runBlocking {
            // The depth check is in invoke() itself, not in the recursive resolveList.
            // Link resolution calls resolveList directly, so the only way to trigger
            // the exception is to call invoke() with currentDepth already above maxLinkDepth (5).
            val elementsMap = mapOf("main.json" to listOf(PrayerElementDomain.Prose("content")))
            val useCase = makeUseCase(elementsMap)

            var threw = false
            try {
                useCase("main.json", AppLanguage.ENGLISH, currentDepth = 6)
            } catch (e: PrayerLinkDepthExceededException) {
                threw = true
            }
            assertTrue(threw)
        }

    @Test
    fun `resolves nested CollapsibleBlock items recursively`() =
        runBlocking {
            val innerBlock = PrayerElementDomain.CollapsibleBlock(
                title = "Inner",
                items = listOf(PrayerElementDomain.Link("linked.json")),
            )
            val elementsMap =
                mapOf(
                    "main.json" to listOf(innerBlock),
                    "linked.json" to listOf(PrayerElementDomain.Prose("Resolved prose")),
                )
            val result = makeUseCase(elementsMap)("main.json", AppLanguage.ENGLISH)

            val block = result.filterIsInstance<PrayerElementDomain.CollapsibleBlock>().firstOrNull()
            assertNotNull(block)
            assertTrue(block!!.items.any { it is PrayerElementDomain.Prose && it.content == "Resolved prose" })
        }

    @Test
    fun `resolves AlternativePrayersBlock options recursively`() =
        runBlocking {
            val option = PrayerElementDomain.AlternativeOption(
                label = "Option A",
                items = listOf(PrayerElementDomain.Link("linked.json")),
            )
            val block = PrayerElementDomain.AlternativePrayersBlock(
                title = "Choose one",
                options = listOf(option),
            )
            val elementsMap =
                mapOf(
                    "main.json" to listOf(block),
                    "linked.json" to listOf(PrayerElementDomain.Prose("Option content")),
                )
            val result = makeUseCase(elementsMap)("main.json", AppLanguage.ENGLISH)

            val altBlock = result.filterIsInstance<PrayerElementDomain.AlternativePrayersBlock>().firstOrNull()
            assertNotNull(altBlock)
            val resolvedOption = altBlock!!.options.firstOrNull { it.label == "Option A" }
            assertNotNull(resolvedOption)
            assertTrue(resolvedOption!!.items.any { it is PrayerElementDomain.Prose && it.content == "Option content" })
        }
}
