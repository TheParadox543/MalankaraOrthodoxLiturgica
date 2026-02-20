package com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PageNodeDomain
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.Test

class GetAdjacentSiblingRoutesUseCaseTest {
    private val useCase = GetAdjacentSiblingRoutesUseCase()

    private fun makeNode(route: String, parent: String?) = PageNodeDomain(route = route, parent = parent)

    @Test
    fun `returns prev and next for a middle sibling`() {
        val child1 = makeNode("prayers/morning", "prayers")
        val child2 = makeNode("prayers/evening", "prayers")
        val child3 = makeNode("prayers/night", "prayers")
        val root = PageNodeDomain(
            route = "prayers",
            parent = null,
            children = listOf(child1, child2, child3),
        )

        val (prev, next) = useCase(root, child2)
        assertEquals("prayers/morning", prev)
        assertEquals("prayers/night", next)
    }

    @Test
    fun `returns null prev for the first sibling`() {
        val child1 = makeNode("prayers/morning", "prayers")
        val child2 = makeNode("prayers/evening", "prayers")
        val root = PageNodeDomain(
            route = "prayers",
            parent = null,
            children = listOf(child1, child2),
        )

        val (prev, next) = useCase(root, child1)
        assertNull(prev)
        assertEquals("prayers/evening", next)
    }

    @Test
    fun `returns null next for the last sibling`() {
        val child1 = makeNode("prayers/morning", "prayers")
        val child2 = makeNode("prayers/evening", "prayers")
        val root = PageNodeDomain(
            route = "prayers",
            parent = null,
            children = listOf(child1, child2),
        )

        val (prev, next) = useCase(root, child2)
        assertEquals("prayers/morning", prev)
        assertNull(next)
    }

    @Test
    fun `returns null pair when parent route is not found in tree`() {
        val node = makeNode("prayers/morning", "nonexistent")
        val root = PageNodeDomain(route = "prayers", parent = null)

        val (prev, next) = useCase(root, node)
        assertNull(prev)
        assertNull(next)
    }

    @Test
    fun `returns null pair when node has no parent`() {
        val node = makeNode("prayers", null)
        val root = PageNodeDomain(route = "prayers", parent = null)

        val (prev, next) = useCase(root, node)
        assertNull(prev)
        assertNull(next)
    }

    @Test
    fun `returns null pair when node is not found in parent children`() {
        val child1 = makeNode("prayers/morning", "prayers")
        val root = PageNodeDomain(
            route = "prayers",
            parent = null,
            children = listOf(child1),
        )
        // Create a node with the same parent route but not in the children list
        val orphan = makeNode("prayers/orphan", "prayers")

        val (prev, next) = useCase(root, orphan)
        assertNull(prev)
        assertNull(next)
    }

    @Test
    fun `returns null both for single child`() {
        val child = makeNode("prayers/morning", "prayers")
        val root = PageNodeDomain(
            route = "prayers",
            parent = null,
            children = listOf(child),
        )

        val (prev, next) = useCase(root, child)
        assertNull(prev)
        assertNull(next)
    }
}
