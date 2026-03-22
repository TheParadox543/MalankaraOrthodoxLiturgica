package com.paradox543.malankaraorthodoxliturgica.data.prayer.datasource

import android.content.Context
import android.content.res.AssetManager
import com.paradox543.malankaraorthodoxliturgica.data.core.datasource.AssetJsonReader
import com.paradox543.malankaraorthodoxliturgica.data.prayer.model.PrayerContentNotFoundException
import com.paradox543.malankaraorthodoxliturgica.data.prayer.model.PrayerElementDto
import com.paradox543.malankaraorthodoxliturgica.data.prayer.model.PrayerParsingException
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.serialization.json.Json
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayInputStream
import java.io.IOException

/**
 * Unit tests for [PrayerSource].
 *
 * [AssetJsonReader.loadJsonAsset] is `inline reified` — mockk cannot intercept it on a mocked
 * [AssetJsonReader]. Instead we build a *real* [AssetJsonReader] whose [Context]/[AssetManager]
 * are mocked, and feed JSON through [ByteArrayInputStream].
 *
 * android.util.Log stubs are suppressed via
 *   testOptions { unitTests { isReturnDefaultValues = true } }
 * in build.gradle.kts.
 */
class PrayerSourceTest {

    private val assetManager: AssetManager = mockk()
    private val context: Context = mockk()
    private val json = Json { ignoreUnknownKeys = true }

    private lateinit var source: PrayerSource

    @BeforeTest
    fun setup() {
        every { context.assets } returns assetManager
        source = PrayerSource(AssetJsonReader(context, json))
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private fun stubAsset(path: String, jsonContent: String) {
        every { assetManager.open(path) } returns ByteArrayInputStream(jsonContent.toByteArray())
    }

    private fun stubAssetThrows(path: String) {
        every { assetManager.open(path) } throws IOException("Asset not found: $path")
    }

    // ─── loadPrayerElements: path construction ────────────────────────────────

    @Test
    fun `loadPrayerElements opens correct path for ENGLISH`(): Unit = runBlocking {
        stubAsset("en/prayers/vespers.json", "[]")

        source.loadPrayerElements("vespers.json", AppLanguage.ENGLISH)

        verify { assetManager.open("en/prayers/vespers.json") }
    }

    @Test
    fun `loadPrayerElements opens correct path for MALAYALAM`(): Unit = runBlocking {
        stubAsset("ml/prayers/vespers.json", "[]")

        source.loadPrayerElements("vespers.json", AppLanguage.MALAYALAM)

        verify { assetManager.open("ml/prayers/vespers.json") }
    }

    @Test
    fun `loadPrayerElements passes fileName through unmodified`(): Unit = runBlocking {
        val fileName = "sub/compline.json"
        stubAsset("en/prayers/$fileName", "[]")

        source.loadPrayerElements(fileName, AppLanguage.ENGLISH)

        verify { assetManager.open("en/prayers/$fileName") }
    }

    // ─── loadPrayerElements: deserialization ─────────────────────────────────

    @Test
    fun `loadPrayerElements returns empty list for empty JSON array`(): Unit = runBlocking {
        stubAsset("en/prayers/empty.json", "[]")

        val result = source.loadPrayerElements("empty.json", AppLanguage.ENGLISH)

        assertEquals(emptyList(), result)
    }

    @Test
    fun `loadPrayerElements parses a prose element`(): Unit = runBlocking {
        stubAsset(
            "en/prayers/vespers.json",
            """[{"type":"prose","content":"Glory to God"}]""",
        )

        val result = source.loadPrayerElements("vespers.json", AppLanguage.ENGLISH)

        assertEquals(1, result.size)
        assertIs<PrayerElementDto.Prose>(result[0])
        assertEquals("Glory to God", (result[0] as PrayerElementDto.Prose).content)
    }

    @Test
    fun `loadPrayerElements parses multiple element types`(): Unit = runBlocking {
        val json = """
            [
              {"type":"title","content":"Vespers"},
              {"type":"heading","content":"Opening"},
              {"type":"prose","content":"Body text"},
              {"type":"link","file":"common.json"}
            ]
        """.trimIndent()
        stubAsset("en/prayers/vespers.json", json)

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
            stubAssetThrows("en/prayers/missing.json")

            assertFailsWith<PrayerParsingException> {
                source.loadPrayerElements("missing.json", AppLanguage.ENGLISH)
            }
        }

    // ─── loadPrayerNavigationTree: path construction ──────────────────────────

    @Test
    fun `loadPrayerNavigationTree opens correct path for ENGLISH`(): Unit = runBlocking {
        stubAsset(
            "en/prayers_tree.json",
            """{"route":"root","parent":null}""",
        )

        source.loadPrayerNavigationTree(AppLanguage.ENGLISH)

        verify { assetManager.open("en/prayers_tree.json") }
    }

    @Test
    fun `loadPrayerNavigationTree opens correct path for MALAYALAM`(): Unit = runBlocking {
        stubAsset(
            "ml/prayers_tree.json",
            """{"route":"root","parent":null}""",
        )

        source.loadPrayerNavigationTree(AppLanguage.MALAYALAM)

        verify { assetManager.open("ml/prayers_tree.json") }
    }

    // ─── loadPrayerNavigationTree: deserialization ────────────────────────────

    @Test
    fun `loadPrayerNavigationTree parses route and parent`(): Unit = runBlocking {
        stubAsset(
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
    fun `loadPrayerNavigationTree parses nested children`(): Unit = runBlocking {
        val treeJson = """
            {
              "route": "root",
              "parent": null,
              "children": [
                {"route":"root/vespers","type":"prayer","filename":"vespers.json","parent":"root"}
              ]
            }
        """.trimIndent()
        stubAsset("en/prayers_tree.json", treeJson)

        val result = source.loadPrayerNavigationTree(AppLanguage.ENGLISH)

        assertEquals(1, result.children.size)
        assertEquals("root/vespers", result.children[0].route)
        assertEquals("vespers.json", result.children[0].filename)
    }

    @Test
    fun `loadPrayerNavigationTree throws PrayerContentNotFoundException when asset is missing`(): Unit =
        runBlocking {
            stubAssetThrows("en/prayers_tree.json")

            assertFailsWith<PrayerContentNotFoundException> {
                source.loadPrayerNavigationTree(AppLanguage.ENGLISH)
            }
        }
}
