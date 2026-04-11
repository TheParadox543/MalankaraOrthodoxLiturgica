package com.paradox543.malankaraorthodoxliturgica.data.prayer.datasource

import com.paradox543.malankaraorthodoxliturgica.data.core.datasource.ResourceTextReader
import com.paradox543.malankaraorthodoxliturgica.data.prayer.model.PrayerContentNotFoundException
import com.paradox543.malankaraorthodoxliturgica.data.prayer.model.PrayerElementDto
import com.paradox543.malankaraorthodoxliturgica.data.prayer.model.PrayerParsingException
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.io.IOException
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

/**
 * Unit tests for [PrayerSource].
 */
class PrayerSourceTest {
    private val reader: ResourceTextReader = mockk()
    private val json = Json { ignoreUnknownKeys = true }

    private lateinit var source: PrayerSource

    @BeforeTest
    fun setup() {
        source = PrayerSource(reader, json)
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private fun stubText(
        path: String,
        jsonContent: String,
    ) {
        coEvery { reader.readText(path) } returns jsonContent
    }

    private fun stubThrows(path: String) {
        coEvery { reader.readText(path) } throws IOException("Asset not found: $path")
    }

    // ─── loadPrayerElements: path construction ────────────────────────────────

    @Test
    fun `loadPrayerElements opens correct path for ENGLISH`(): Unit =
        runBlocking {
            stubText("en/prayers/vespers.json", "[]")

            source.loadPrayerElements("vespers.json", AppLanguage.ENGLISH)

            coVerify { reader.readText("en/prayers/vespers.json") }
        }

    @Test
    fun `loadPrayerElements opens correct path for MALAYALAM`(): Unit =
        runBlocking {
            stubText("ml/prayers/vespers.json", "[]")

            source.loadPrayerElements("vespers.json", AppLanguage.MALAYALAM)

            coVerify { reader.readText("ml/prayers/vespers.json") }
        }

    @Test
    fun `loadPrayerElements passes fileName through unmodified`(): Unit =
        runBlocking {
            val fileName = "sub/compline.json"
            stubText("en/prayers/$fileName", "[]")

            source.loadPrayerElements(fileName, AppLanguage.ENGLISH)

            coVerify { reader.readText("en/prayers/$fileName") }
        }

    // ─── loadPrayerElements: deserialization ─────────────────────────────────

    @Test
    fun `loadPrayerElements returns empty list for empty JSON array`(): Unit =
        runBlocking {
            stubText("en/prayers/empty.json", "[]")

            val result = source.loadPrayerElements("empty.json", AppLanguage.ENGLISH)

            assertEquals(emptyList(), result)
        }

    @Test
    fun `loadPrayerElements parses a prose element`(): Unit =
        runBlocking {
            stubText(
                "en/prayers/vespers.json",
                """[{"type":"prose","content":"Glory to God"}]""",
            )

            val result = source.loadPrayerElements("vespers.json", AppLanguage.ENGLISH)

            assertEquals(1, result.size)
            assertIs<PrayerElementDto.Prose>(result[0])
            assertEquals("Glory to God", (result[0] as PrayerElementDto.Prose).content)
        }

    @Test
    fun `loadPrayerElements parses multiple element types`(): Unit =
        runBlocking {
            val json =
                """
                [
                  {"type":"title","content":"Vespers"},
                  {"type":"heading","content":"Opening"},
                  {"type":"prose","content":"Body text"},
                  {"type":"link","file":"common.json"}
                ]
                """.trimIndent()
            stubText("en/prayers/vespers.json", json)

            val result = source.loadPrayerElements("vespers.json", AppLanguage.ENGLISH)

            assertEquals(4, result.size)
            assertIs<PrayerElementDto.Title>(result[0])
            assertIs<PrayerElementDto.Heading>(result[1])
            assertIs<PrayerElementDto.Prose>(result[2])
            assertIs<PrayerElementDto.Link>(result[3])
            assertEquals("common.json", (result[3] as PrayerElementDto.Link).file)
        }

    @Test
    fun `loadPrayerElements throws PrayerParsingException when asset is missing`(): Unit =
        runBlocking {
            stubThrows("en/prayers/missing.json")

            assertFailsWith<PrayerParsingException> {
                source.loadPrayerElements("missing.json", AppLanguage.ENGLISH)
            }
        }

    // ─── loadPrayerNavigationTree: path construction ──────────────────────────

    @Test
    fun `loadPrayerNavigationTree opens correct path for ENGLISH`(): Unit =
        runBlocking {
            stubText(
                "en/prayers_tree.json",
                """{"route":"root","parent":null}""",
            )

            source.loadPrayerNavigationTree(AppLanguage.ENGLISH)

            coVerify { reader.readText("en/prayers_tree.json") }
        }

    @Test
    fun `loadPrayerNavigationTree opens correct path for MALAYALAM`(): Unit =
        runBlocking {
            stubText(
                "ml/prayers_tree.json",
                """{"route":"root","parent":null}""",
            )

            source.loadPrayerNavigationTree(AppLanguage.MALAYALAM)

            coVerify { reader.readText("ml/prayers_tree.json") }
        }

    // ─── loadPrayerNavigationTree: deserialization ────────────────────────────

    @Test
    fun `loadPrayerNavigationTree parses route and parent`(): Unit =
        runBlocking {
            stubText(
                "en/prayers_tree.json",
                """{"route":"root","type":"section","parent":null,"children":[],"languages":["en","ml"]}""",
            )

            val result = source.loadPrayerNavigationTree(AppLanguage.ENGLISH)

            assertEquals("root", result.route)
            assertEquals("section", result.type)
            assertEquals(null, result.parent)
            assertEquals(listOf("en", "ml"), result.languages)
        }

    @Test
    fun `loadPrayerNavigationTree parses nested children`(): Unit =
        runBlocking {
            val treeJson =
                """
                {
                  "route": "root",
                  "parent": null,
                  "children": [
                    {"route":"root/vespers","type":"prayer","filename":"vespers.json","parent":"root"}
                  ]
                }
                """.trimIndent()
            stubText("en/prayers_tree.json", treeJson)

            val result = source.loadPrayerNavigationTree(AppLanguage.ENGLISH)

            assertEquals(1, result.children.size)
            assertEquals("root/vespers", result.children[0].route)
            assertEquals("vespers.json", result.children[0].filename)
        }

    @Test
    fun `loadPrayerNavigationTree throws PrayerContentNotFoundException when asset is missing`(): Unit =
        runBlocking {
            stubThrows("en/prayers_tree.json")

            assertFailsWith<PrayerContentNotFoundException> {
                source.loadPrayerNavigationTree(AppLanguage.ENGLISH)
            }
        }
}
