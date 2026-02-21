package com.paradox543.malankaraorthodoxliturgica.data.prayer.mapping

import com.paradox543.malankaraorthodoxliturgica.data.prayer.model.PageNodeDto
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PageNode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PageNodeMappersTest {

    // ─── toPageNodeDomain ────────────────────────────────────────────────────

    @Test
    fun `toPageNodeDomain maps all flat fields`() {
        val dto = PageNodeDto(
            route = "prayers/vespers",
            type = "prayer",
            filename = "vespers.json",
            parent = "prayers",
            children = emptyList(),
            languages = listOf("en", "ml"),
        )

        val domain = dto.toPageNodeDomain()

        assertEquals("prayers/vespers", domain.route)
        assertEquals("prayer", domain.type)
        assertEquals("vespers.json", domain.filename)
        assertEquals("prayers", domain.parent)
        assertEquals(listOf("en", "ml"), domain.languages)
    }

    @Test
    fun `toPageNodeDomain handles null filename and parent`() {
        val dto = PageNodeDto(
            route = "root",
            type = "section",
            filename = null,
            parent = null,
        )

        val domain = dto.toPageNodeDomain()

        assertNull(domain.filename)
        assertNull(domain.parent)
    }

    @Test
    fun `toPageNodeDomain recursively maps children`() {
        val childDto = PageNodeDto(
            route = "prayers/vespers/psalm",
            type = "prayer",
            filename = "psalm.json",
            parent = "prayers/vespers",
        )
        val parentDto = PageNodeDto(
            route = "prayers/vespers",
            type = "section",
            filename = null,
            parent = "prayers",
            children = listOf(childDto),
        )

        val domain = parentDto.toPageNodeDomain()

        assertEquals(1, domain.children.size)
        assertEquals("prayers/vespers/psalm", domain.children[0].route)
        assertEquals("psalm.json", domain.children[0].filename)
    }

    @Test
    fun `toPageNodeDomain with empty children`() {
        val dto = PageNodeDto(route = "root", parent = null, children = emptyList())
        assertEquals(emptyList(), dto.toPageNodeDomain().children)
    }

    // ─── toData ──────────────────────────────────────────────────────────────

    @Test
    fun `toData maps all flat fields`() {
        val domain = PageNode(
            route = "prayers/vespers",
            type = "prayer",
            filename = "vespers.json",
            parent = "prayers",
            children = emptyList(),
            languages = listOf("en", "ml"),
        )

        val dto = domain.toData()

        assertEquals("prayers/vespers", dto.route)
        assertEquals("prayer", dto.type)
        assertEquals("vespers.json", dto.filename)
        assertEquals("prayers", dto.parent)
        assertEquals(listOf("en", "ml"), dto.languages)
    }

    @Test
    fun `toData recursively maps children`() {
        val child = PageNode(
            route = "prayers/vespers/psalm",
            type = "prayer",
            filename = "psalm.json",
            parent = "prayers/vespers",
            children = emptyList(),
            languages = emptyList(),
        )
        val parent = PageNode(
            route = "prayers/vespers",
            type = "section",
            filename = null,
            parent = "prayers",
            children = listOf(child),
            languages = emptyList(),
        )

        val dto = parent.toData()

        assertEquals(1, dto.children.size)
        assertEquals("prayers/vespers/psalm", dto.children[0].route)
    }

    // ─── Round-trip ───────────────────────────────────────────────────────────

    @Test
    fun `domain to data and back is lossless`() {
        val original = PageNodeDto(
            route = "a/b/c",
            type = "prayer",
            filename = "c.json",
            parent = "a/b",
            children = listOf(
                PageNodeDto(route = "a/b/c/d", type = "prayer", filename = "d.json", parent = "a/b/c"),
            ),
            languages = listOf("en"),
        )

        val roundTripped = original.toPageNodeDomain().toData()

        assertEquals(original, roundTripped)
    }
}
