package com.paradox543.malankaraorthodoxliturgica.data.calendar.datasource

import android.content.Context
import android.content.res.AssetManager
import com.paradox543.malankaraorthodoxliturgica.data.core.datasource.AssetJsonReader
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.serialization.json.Json
import java.io.ByteArrayInputStream
import java.io.IOException
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import com.paradox543.malankaraorthodoxliturgica.data.core.exceptions.AssetReadException

/**
 * Tests for [CalendarSource].
 *
 * [AssetJsonReader.loadJsonAsset] is `inline reified` — Kotlin inlines the body at the call
 * site so mockk cannot intercept a call on a mocked [AssetJsonReader]. Instead we back a real
 * [AssetJsonReader] with mocked [Context]/[AssetManager] and feed test JSON through them.
 */
class CalendarSourceTest {

    private val assetManager: AssetManager = mockk()
    private val context: Context = mockk()
    private val json = Json { ignoreUnknownKeys = true }
    private lateinit var source: CalendarSource

    @BeforeTest
    fun setup() {
        every { context.assets } returns assetManager
        source = CalendarSource(AssetJsonReader(context, json))
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private fun stubAsset(path: String, jsonContent: String) {
        every { assetManager.open(path) } returns ByteArrayInputStream(jsonContent.toByteArray())
    }

    private fun stubAssetThrows(path: String) {
        every { assetManager.open(path) } throws IOException("Asset not found: $path")
    }

    // ─── readLiturgicalDates ─────────────────────────────────────────────────

    @Test
    fun `readLiturgicalDates opens the correct asset path`() {
        stubAsset("calendar/liturgical_calendar.json", """{}""")

        source.readLiturgicalDates()

        verify { assetManager.open("calendar/liturgical_calendar.json") }
    }

    @Test
    fun `readLiturgicalDates returns parsed map when asset is available`() {
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
    fun `readLiturgicalDates throws AssetReadException when asset cannot be opened`() {
        stubAssetThrows("calendar/liturgical_calendar.json")

        assertFailsWith<AssetReadException> { source.readLiturgicalDates() }
    }

    // ─── readLiturgicalData ──────────────────────────────────────────────────

    @Test
    fun `readLiturgicalData opens the correct asset path`() {
        stubAsset("calendar/liturgical_data.json", """{}""")

        source.readLiturgicalData()

        verify { assetManager.open("calendar/liturgical_data.json") }
    }

    @Test
    fun `readLiturgicalData returns parsed map when asset is available`() {
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
    fun `readLiturgicalData throws AssetReadException when asset cannot be opened`() {
        stubAssetThrows("calendar/liturgical_data.json")

        assertFailsWith<AssetReadException> { source.readLiturgicalData() }
    }
}
