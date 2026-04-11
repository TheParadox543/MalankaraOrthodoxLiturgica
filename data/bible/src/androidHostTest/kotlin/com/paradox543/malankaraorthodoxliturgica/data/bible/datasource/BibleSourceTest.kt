package com.paradox543.malankaraorthodoxliturgica.data.bible.datasource

import com.paradox543.malankaraorthodoxliturgica.data.core.datasource.ResourceTextReader
import com.paradox543.malankaraorthodoxliturgica.data.core.exceptions.AssetReadException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.fail

/**
 * Tests for [BibleSource].
 *
 */
class BibleSourceTest {
    private val reader: ResourceTextReader = mockk()

    // Use lenient JSON so tests don't break if new optional fields are added to the DTOs
    private val json = Json { ignoreUnknownKeys = true }

    private lateinit var source: BibleSource

    @BeforeTest
    fun setup() {
        source = BibleSource(reader, json)
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private fun stubContent(
        path: String,
        jsonContent: String,
    ) {
        coEvery { reader.readText(path) } returns jsonContent
    }

    private fun stubThrows(path: String) {
        coEvery { reader.readText(path) } throws RuntimeException("File not found: $path")
    }

    private suspend inline fun <reified T : Throwable> assertFailsWithSuspend(crossinline block: suspend () -> Unit): T =
        try {
            block()
            fail("Expected ${T::class.simpleName} to be thrown")
        } catch (t: Throwable) {
            assertIs<T>(t)
            t
        }

    // ─── readBibleDetails ────────────────────────────────────────────────────

    @Test
    fun `readBibleDetails opens the correct asset path`(): Unit =
        runTest {
            stubContent(
                "bibleBookMetadata.json",
                """[{"book":{"en":"Genesis","ml":"ഉൽപ്പത്തി"},"folder":"genesis","verseCount":[31]}]""",
            )

            source.readBibleDetails()

            coVerify { reader.readText("bibleBookMetadata.json") }
        }

    @Test
    fun `readBibleDetails returns parsed list when asset is available`(): Unit =
        runTest {
            stubContent(
                "bibleBookMetadata.json",
                """[{"book":{"en":"Genesis","ml":"ഉൽപ്പത്തി"},"folder":"genesis","verseCount":[31]}]""",
            )

            val result = source.readBibleDetails()

            assertEquals(1, result.size)
            assertEquals("Genesis", result[0].book.en)
            assertEquals("genesis", result[0].folder)
        }

    @Test
    fun `readBibleDetails throws AssetReadException when asset cannot be opened`(): Unit =
        runTest {
            stubThrows("bibleBookMetadata.json")

            assertFailsWith<AssetReadException> { source.readBibleDetails() }
        }

    // ─── readPrefaceTemplates ────────────────────────────────────────────────

    @Test
    fun `readPrefaceTemplates opens the correct asset path`(): Unit =
        runTest {
            stubContent(
                "bible_preface_templates.json",
                """{"prophets":{"en":[],"ml":[]},"generalEpistle":{"en":[],"ml":[]},"paulineEpistle":{"en":[],"ml":[]}}""",
            )

            source.readPrefaceTemplates()

            coVerify { reader.readText("bible_preface_templates.json") }
        }

    @Test
    fun `readPrefaceTemplates returns parsed templates when asset is available`(): Unit =
        runTest {
            stubContent(
                "bible_preface_templates.json",
                """{"prophets":{"en":[],"ml":[]},"generalEpistle":{"en":[],"ml":[]},"paulineEpistle":{"en":[],"ml":[]}}""",
            )

            val result = source.readPrefaceTemplates()

            assertEquals(result.prophets.en.isEmpty(), true)
            assertEquals(result.paulineEpistle.ml.isEmpty(), true)
        }

    @Test
    fun `readPrefaceTemplates throws AssetReadException when asset cannot be opened`(): Unit =
        runTest {
            stubThrows("bible_preface_templates.json")

            assertFailsWith<AssetReadException> { source.readPrefaceTemplates() }
        }

    // ─── readBibleChapter ────────────────────────────────────────────────────

    @Test
    fun `readBibleChapter opens the exact path it receives`(): Unit =
        runTest {
            val path = "en/bible/genesis/001.json"
            stubContent(path, """{"book":"Genesis","chapter":1,"verses":[{"id":1,"verse":"In the beginning"}]}""")

            source.readBibleChapter(path)

            // BibleSource must forward the path untouched — construction is BibleRepositoryImpl's job
            coVerify { reader.readText(path) }
        }

    @Test
    fun `readBibleChapter returns parsed chapter when asset is available`(): Unit =
        runTest {
            val path = "en/bible/genesis/001.json"
            stubContent(path, """{"book":"Genesis","chapter":1,"verses":[{"id":1,"verse":"In the beginning"}]}""")

            val result = source.readBibleChapter(path)

            assertEquals("Genesis", result.book)
            assertEquals(1, result.chapter)
            assertEquals(1, result.verses.size)
            assertEquals("In the beginning", result.verses[0].verse)
        }

    @Test
    fun `readBibleChapter throws AssetReadException when asset cannot be opened`(): Unit =
        runTest {
            val path = "en/bible/genesis/001.json"
            stubThrows(path)

            assertFailsWithSuspend<AssetReadException> { source.readBibleChapter(path) }
        }

    @Test
    fun `readBibleChapter forwards arbitrary paths untransformed`(): Unit =
        runTest {
            val path = "ml/bible/exodus/012.json"
            stubContent(path, """{"book":"Exodus","chapter":12,"verses":[]}""")

            source.readBibleChapter(path)

            coVerify { reader.readText(path) }
        }
}
