package com.paradox543.malankaraorthodoxliturgica.data.bible.datasource

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
import kotlin.test.assertTrue
import com.paradox543.malankaraorthodoxliturgica.data.core.exceptions.AssetReadException

/**
 * Tests for [BibleSource].
 *
 * [AssetJsonReader.loadJsonAsset] is an `inline reified` function — Kotlin inlines its body at
 * the call site, so mockk cannot intercept a call on a mocked [AssetJsonReader]. Instead we
 * construct a *real* [AssetJsonReader] whose [Context] / [AssetManager] are mocked, and feed
 * test JSON through them. This lets us verify:
 *   - the correct asset path is opened for each method, and
 *   - deserialized DTOs are returned (or AssetReadException thrown on failure).
 */
class BibleSourceTest {
    private val assetManager: AssetManager = mockk()
    private val context: Context = mockk()

    // Use lenient JSON so tests don't break if new optional fields are added to the DTOs
    private val json = Json { ignoreUnknownKeys = true }

    private lateinit var source: BibleSource

    @BeforeTest
    fun setup() {
        every { context.assets } returns assetManager
        source = BibleSource(AssetJsonReader(context, json))
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private fun stubAsset(
        path: String,
        jsonContent: String,
    ) {
        every { assetManager.open(path) } returns ByteArrayInputStream(jsonContent.toByteArray())
    }

    private fun stubAssetThrows(path: String) {
        every { assetManager.open(path) } throws IOException("Asset not found: $path")
    }

    // ─── readBibleDetails ────────────────────────────────────────────────────

    @Test
    fun `readBibleDetails opens the correct asset path`() {
        stubAsset(
            "bibleBookMetadata.json",
            """[{"book":{"en":"Genesis","ml":"ഉൽപ്പത്തി"},"folder":"genesis","verseCount":[31]}]""",
        )

        source.readBibleDetails()

        verify { assetManager.open("bibleBookMetadata.json") }
    }

    @Test
    fun `readBibleDetails returns parsed list when asset is available`() {
        stubAsset(
            "bibleBookMetadata.json",
            """[{"book":{"en":"Genesis","ml":"ഉൽപ്പത്തി"},"folder":"genesis","verseCount":[31]}]""",
        )

        val result = source.readBibleDetails()

        assertEquals(1, result.size)
        assertEquals("Genesis", result[0].book?.en)
        assertEquals("genesis", result[0].folder)
    }

    @Test
    fun `readBibleDetails throws AssetReadException when asset cannot be opened`() {
        stubAssetThrows("bibleBookMetadata.json")

        assertFailsWith<AssetReadException> { source.readBibleDetails() }
    }

    // ─── readPrefaceTemplates ────────────────────────────────────────────────

    @Test
    fun `readPrefaceTemplates opens the correct asset path`() {
        stubAsset(
            "bible_preface_templates.json",
            """{"prophets":{"en":[],"ml":[]},"generalEpistle":{"en":[],"ml":[]},"paulineEpistle":{"en":[],"ml":[]}}""",
        )

        source.readPrefaceTemplates()

        verify { assetManager.open("bible_preface_templates.json") }
    }

    @Test
    fun `readPrefaceTemplates returns parsed templates when asset is available`() {
        stubAsset(
            "bible_preface_templates.json",
            """{"prophets":{"en":[],"ml":[]},"generalEpistle":{"en":[],"ml":[]},"paulineEpistle":{"en":[],"ml":[]}}""",
        )

        val result = source.readPrefaceTemplates()

        assertTrue(result.prophets?.en?.isEmpty() == true)
        assertTrue(result.paulineEpistle?.ml?.isEmpty() == true)
    }

    @Test
    fun `readPrefaceTemplates throws AssetReadException when asset cannot be opened`() {
        stubAssetThrows("bible_preface_templates.json")

        assertFailsWith<AssetReadException> { source.readPrefaceTemplates() }
    }

    // ─── readBibleChapter ────────────────────────────────────────────────────

    @Test
    fun `readBibleChapter opens the exact path it receives`() {
        val path = "en/bible/genesis/001.json"
        stubAsset(path, """{"book":"Genesis","chapter":1,"verses":[{"id":1,"verse":"In the beginning"}]}""")

        source.readBibleChapter(path)

        // BibleSource must forward the path untouched — construction is BibleRepositoryImpl's job
        verify { assetManager.open(path) }
    }

    @Test
    fun `readBibleChapter returns parsed chapter when asset is available`() {
        val path = "en/bible/genesis/001.json"
        stubAsset(path, """{"book":"Genesis","chapter":1,"verses":[{"id":1,"verse":"In the beginning"}]}""")

        val result = source.readBibleChapter(path)

        assertEquals("Genesis", result.book)
        assertEquals(1, result.chapter)
        assertEquals(1, result.verses?.size)
        assertEquals("In the beginning", result.verses?.get(0)?.verse)
    }

    @Test
    fun `readBibleChapter throws AssetReadException when asset cannot be opened`() {
        val path = "en/bible/genesis/001.json"
        stubAssetThrows(path)

        assertFailsWith<AssetReadException> { source.readBibleChapter(path) }
    }

    @Test
    fun `readBibleChapter forwards arbitrary paths untransformed`() {
        val path = "ml/bible/exodus/012.json"
        stubAsset(path, """{"book":"Exodus","chapter":12,"verses":[]}""")

        source.readBibleChapter(path)

        verify { assetManager.open(path) }
    }
}
