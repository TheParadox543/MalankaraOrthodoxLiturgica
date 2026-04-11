package com.paradox543.malankaraorthodoxliturgica.data.calendar.datasource

import com.paradox543.malankaraorthodoxliturgica.data.core.datasource.ResourceTextReader
import com.paradox543.malankaraorthodoxliturgica.data.core.exceptions.AssetReadException
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Tests for [CalendarSource].
 *
 * [CalendarSource] now uses [ResourceTextReader] which is suspend.
 * We mock the reader and test the JSON parsing.
 */
class CalendarSourceTest {
    private val reader: ResourceTextReader = mockk()
    private val json = Json { ignoreUnknownKeys = true }
    private lateinit var source: CalendarSource

    @BeforeTest
    fun setup() {
        source = CalendarSource(reader = reader, json = json)
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private fun stubAsset(
        path: String,
        jsonContent: String,
    ) {
        coEvery { reader.readText(path) } returns jsonContent
    }

    private fun stubAssetThrows(path: String) {
        coEvery { reader.readText(path) } throws Exception("Asset not found: $path")
    }

    // ─── readLiturgicalDates ─────────────────────────────────────────────────

    @Test
    fun `readLiturgicalDates opens the correct asset path`() =
        runBlocking {
            stubAsset("calendar/liturgical_calendar.json", """{}""")

            source.readLiturgicalDates()

            verify { runBlocking { reader.readText("calendar/liturgical_calendar.json") } }
        }

    @Test
    fun `readLiturgicalDates returns parsed map when asset is available`() =
        runBlocking {
            // LiturgicalCalendarDates = Map<String, YearEvents>
            // YearEvents = Map<String, MonthEvents> = Map<String, Map<String, List<String>>>
            stubAsset(
                "calendar/liturgical_calendar.json",
                """{"2025":{"4":{"20":["easter"]}}}""",
            )

            val result = source.readLiturgicalDates()

            assertEquals(listOf("easter"), result["2025"]?.get("4")?.get("20"))
        }

    @Test
    fun `readLiturgicalDates throws AssetReadException when asset cannot be opened`(): Unit =
        runBlocking {
            stubAssetThrows("calendar/liturgical_calendar.json")

            assertFailsWith<AssetReadException> { source.readLiturgicalDates() }
        }

    // ─── readLiturgicalData ──────────────────────────────────────────────────

    @Test
    fun `readLiturgicalData opens the correct asset path`() =
        runBlocking {
            stubAsset("calendar/liturgical_data.json", """{}""")

            source.readLiturgicalData()

            verify { runBlocking { reader.readText("calendar/liturgical_data.json") } }
        }

    @Test
    fun `readLiturgicalData returns parsed map when asset is available`() =
        runBlocking {
            // LiturgicalDataStore = Map<String, LiturgicalEventDetailsDto>
            stubAsset(
                "calendar/liturgical_data.json",
                """{"easter":{"type":"feast","title":{"en":"Easter Sunday","ml":"ഉയിർപ്പ്"}}}""",
            )

            val result = source.readLiturgicalData()

            assertEquals("feast", result["easter"]?.type)
            assertEquals("Easter Sunday", result["easter"]?.title?.en)
            assertEquals("ഉയിർപ്പ്", result["easter"]?.title?.ml)
        }

    @Test
    fun `readLiturgicalData throws AssetReadException when asset cannot be opened`(): Unit =
        runBlocking {
            stubAssetThrows("calendar/liturgical_data.json")

            assertFailsWith<AssetReadException> { source.readLiturgicalData() }
        }
}
