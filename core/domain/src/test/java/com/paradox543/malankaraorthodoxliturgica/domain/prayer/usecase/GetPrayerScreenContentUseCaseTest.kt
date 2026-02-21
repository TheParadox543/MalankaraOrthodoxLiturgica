package com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.fakes.FakeCalendarRepository
import com.paradox543.malankaraorthodoxliturgica.domain.fakes.FakePrayerRepository
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElement
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerLinkDepthExceededException
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GetPrayerScreenContentUseCaseTest {
    private fun makeUseCase(elementsMap: Map<String, List<PrayerElement>>): GetPrayerScreenContentUseCase {
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
                    "main.json" to listOf(PrayerElement.Link("linked.json")),
                    "linked.json" to
                        listOf(
                            PrayerElement.Title("Linked Title"),
                            PrayerElement.Prose("Some prose"),
                        ),
                )
            val result = makeUseCase(elementsMap)("main.json", AppLanguage.ENGLISH)

            assertTrue(result.any { it is PrayerElement.Title && it.content == "Linked Title" })
            assertTrue(result.any { it is PrayerElement.Prose && it.content == "Some prose" })
        }

    @Test
    fun `LinkCollapsible extracts Title as collapsible heading`() =
        runBlocking {
            val elementsMap =
                mapOf(
                    "main.json" to listOf(PrayerElement.LinkCollapsible("linked.json")),
                    "linked.json" to
                        listOf(
                            PrayerElement.Title("Section Title"),
                            PrayerElement.Prose("Body text"),
                        ),
                )
            val result = makeUseCase(elementsMap)("main.json", AppLanguage.ENGLISH)

            val block = result.filterIsInstance<PrayerElement.CollapsibleBlock>().firstOrNull()
            assertNotNull(block)
            assertEquals("Section Title", block!!.title)
            assertTrue(block.items.any { it is PrayerElement.Prose && it.content == "Body text" })
        }

    @Test
    fun `LinkCollapsible falls back to Heading when no Title present`() =
        runBlocking {
            val elementsMap =
                mapOf(
                    "main.json" to listOf(PrayerElement.LinkCollapsible("linked.json")),
                    "linked.json" to
                        listOf(
                            PrayerElement.Heading("Section Heading"),
                            PrayerElement.Prose("Body text"),
                        ),
                )
            val result = makeUseCase(elementsMap)("main.json", AppLanguage.ENGLISH)

            val block = result.filterIsInstance<PrayerElement.CollapsibleBlock>().firstOrNull()
            assertNotNull(block)
            assertEquals("Section Heading", block!!.title)
        }

    @Test
    fun `LinkCollapsible uses default title when no Title or Heading present`() =
        runBlocking {
            val elementsMap =
                mapOf(
                    "main.json" to listOf(PrayerElement.LinkCollapsible("linked.json")),
                    "linked.json" to listOf(PrayerElement.Prose("Body text")),
                )
            val result = makeUseCase(elementsMap)("main.json", AppLanguage.ENGLISH)

            val block = result.filterIsInstance<PrayerElement.CollapsibleBlock>().firstOrNull()
            assertNotNull(block)
            assertEquals("Expandable Block", block!!.title)
        }

    @Test
    fun `LinkCollapsible emits Error when linked file has no displayable items`() =
        runBlocking {
            val elementsMap =
                mapOf(
                    "main.json" to listOf(PrayerElement.LinkCollapsible("linked.json")),
                    // linked.json only has a Title, which is consumed as the heading, leaving no items
                    "linked.json" to listOf(PrayerElement.Title("Only Title")),
                )
            val result = makeUseCase(elementsMap)("main.json", AppLanguage.ENGLISH)

            assertTrue(result.any { it is PrayerElement.Error })
        }

    @Test
    fun `Link emits Error when linked file is not found`() =
        runBlocking {
            val elementsMap =
                mapOf("main.json" to listOf(PrayerElement.Link("missing.json")))
            // missing.json is not in the map, so loadPrayerElements returns emptyList — no error expected
            // But if we throw, it should be caught. Use a repo that throws for missing keys.
            val prayerRepo =
                FakePrayerRepository(
                    elementsMap = elementsMap,
                    throwOnMissing = true,
                )
            val calendarRepo = FakeCalendarRepository()
            val dynamicUseCase = GetDynamicSongsUseCase(prayerRepo, calendarRepo)
            val useCase = GetPrayerScreenContentUseCase(prayerRepo, dynamicUseCase)

            val result = useCase("main.json", AppLanguage.ENGLISH)

            assertTrue(result.any { it is PrayerElement.Error })
        }

    @Test
    fun `throws PrayerLinkDepthExceededException when max depth exceeded`() =
        runBlocking {
            // The depth check is in invoke() itself, not in the recursive resolveList.
            // Link resolution calls resolveList directly, so the only way to trigger
            // the exception is to call invoke() with currentDepth already above maxLinkDepth (5).
            val elementsMap = mapOf("main.json" to listOf(PrayerElement.Prose("content")))
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
            val innerBlock =
                PrayerElement.CollapsibleBlock(
                    title = "Inner",
                    items = listOf(PrayerElement.Link("linked.json")),
                )
            val elementsMap =
                mapOf(
                    "main.json" to listOf(innerBlock),
                    "linked.json" to listOf(PrayerElement.Prose("Resolved prose")),
                )
            val result = makeUseCase(elementsMap)("main.json", AppLanguage.ENGLISH)

            val block = result.filterIsInstance<PrayerElement.CollapsibleBlock>().firstOrNull()
            assertNotNull(block)
            assertTrue(block!!.items.any { it is PrayerElement.Prose && it.content == "Resolved prose" })
        }

    @Test
    fun `resolves AlternativePrayersBlock options recursively`() =
        runBlocking {
            val option =
                PrayerElement.AlternativeOption(
                    label = "Option A",
                    items = listOf(PrayerElement.Link("linked.json")),
                )
            val block =
                PrayerElement.AlternativePrayersBlock(
                    title = "Choose one",
                    options = listOf(option),
                )
            val elementsMap =
                mapOf(
                    "main.json" to listOf(block),
                    "linked.json" to listOf(PrayerElement.Prose("Option content")),
                )
            val result = makeUseCase(elementsMap)("main.json", AppLanguage.ENGLISH)

            val altBlock = result.filterIsInstance<PrayerElement.AlternativePrayersBlock>().firstOrNull()
            assertNotNull(altBlock)
            val resolvedOption = altBlock!!.options.firstOrNull { it.label == "Option A" }
            assertNotNull(resolvedOption)
            assertTrue(resolvedOption!!.items.any { it is PrayerElement.Prose && it.content == "Option content" })
        }
}
