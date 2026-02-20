package com.paradox543.malankaraorthodoxliturgica.domain.prayer.model

import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.Test

class PageNodeDomainTest {
    @Test
    fun `findByRoute finds node when route matches current node`() {
        val node = PageNodeDomain(route = "prayers/morning", parent = null)
        val result = node.findByRoute("prayers/morning")
        assertNotNull(result)
        assertEquals("prayers/morning", result?.route)
    }

    @Test
    fun `findByRoute finds node in immediate children`() {
        val child = PageNodeDomain(route = "prayers/morning/opening", parent = "prayers/morning")
        val parent = PageNodeDomain(
            route = "prayers/morning",
            parent = null,
            children = listOf(child),
        )

        val result = parent.findByRoute("prayers/morning/opening")
        assertNotNull(result)
        assertEquals("prayers/morning/opening", result?.route)
    }

    @Test
    fun `findByRoute finds node in nested children`() {
        val grandchild = PageNodeDomain(route = "prayers/morning/opening/psalm", parent = "prayers/morning/opening")
        val child = PageNodeDomain(
            route = "prayers/morning/opening",
            parent = "prayers/morning",
            children = listOf(grandchild),
        )
        val parent = PageNodeDomain(
            route = "prayers/morning",
            parent = null,
            children = listOf(child),
        )

        val result = parent.findByRoute("prayers/morning/opening/psalm")
        assertNotNull(result)
        assertEquals("prayers/morning/opening/psalm", result?.route)
    }

    @Test
    fun `findByRoute returns null when route doesn't exist`() {
        val node = PageNodeDomain(
            route = "prayers/morning",
            parent = null,
            children = listOf(
                PageNodeDomain(route = "prayers/morning/opening", parent = "prayers/morning"),
            ),
        )

        val result = node.findByRoute("prayers/evening")
        assertNull(result)
    }

    @Test
    fun `findByRoute handles empty children list`() {
        val node = PageNodeDomain(route = "prayers/morning", parent = null, children = emptyList())
        val result = node.findByRoute("prayers/morning/opening")
        assertNull(result)
    }

    @Test
    fun `findByRoute handles deeply nested tree structures`() {
        val level4 = PageNodeDomain(route = "a/b/c/d", parent = "a/b/c")
        val level3 = PageNodeDomain(route = "a/b/c", parent = "a/b", children = listOf(level4))
        val level2 = PageNodeDomain(route = "a/b", parent = "a", children = listOf(level3))
        val level1 = PageNodeDomain(route = "a", parent = null, children = listOf(level2))

        val result = level1.findByRoute("a/b/c/d")
        assertNotNull(result)
        assertEquals("a/b/c/d", result?.route)
    }

    @Test
    fun `findByRoute searches multiple children branches`() {
        val child1 = PageNodeDomain(route = "prayers/morning", parent = "prayers")
        val child2 = PageNodeDomain(route = "prayers/evening", parent = "prayers")
        val child3 = PageNodeDomain(route = "prayers/night", parent = "prayers")
        val parent = PageNodeDomain(
            route = "prayers",
            parent = null,
            children = listOf(child1, child2, child3),
        )

        val result1 = parent.findByRoute("prayers/morning")
        val result2 = parent.findByRoute("prayers/evening")
        val result3 = parent.findByRoute("prayers/night")

        assertNotNull(result1)
        assertEquals("prayers/morning", result1?.route)
        assertNotNull(result2)
        assertEquals("prayers/evening", result2?.route)
        assertNotNull(result3)
        assertEquals("prayers/night", result3?.route)
    }

    @Test
    fun `findByRoute returns first match in depth-first search`() {
        val grandchild = PageNodeDomain(route = "duplicate", parent = "prayers/morning")
        val child1 = PageNodeDomain(
            route = "prayers/morning",
            parent = "prayers",
            children = listOf(grandchild),
        )
        val child2 = PageNodeDomain(route = "duplicate", parent = "prayers")
        val parent = PageNodeDomain(
            route = "prayers",
            parent = null,
            children = listOf(child1, child2),
        )

        val result = parent.findByRoute("duplicate")
        assertNotNull(result)
        // Should find the one in the first child's subtree (depth-first)
        assertEquals("prayers/morning", result?.parent)
    }
}
