package com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PageNodeDomain
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetAdjacentSiblingRoutesUseCaseTest {
    private val useCase = GetAdjacentSiblingRoutesUseCase()

    private fun makeNode(
        route: String,
        parent: String?,
        children: List<PageNodeDomain> = emptyList(),
    ) = PageNodeDomain(route = route, parent = parent, children = children)

    @Test
    fun `returns prev and next for a middle sibling`() {
        val child1 = makeNode("prayers/morning", "prayers")
        val child2 = makeNode("prayers/evening", "prayers")
        val child3 = makeNode("prayers/night", "prayers")
        val root =
            PageNodeDomain(
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
        val root =
            PageNodeDomain(
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
        val root =
            PageNodeDomain(
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
        val root =
            PageNodeDomain(
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
        val root =
            PageNodeDomain(
                route = "prayers",
                parent = null,
                children = listOf(child),
            )

        val (prev, next) = useCase(root, child)
        assertNull(prev)
        assertNull(next)
    }

    @Test
    fun `returns null if sibling is itself a parent`() {
        val qurbanaOne = makeNode("qurbana_One", "qurbana")
        val qurbanaTwo = makeNode("qurbana_Two", "qurbana")
        val qurbana = makeNode("qurbana", "root", listOf(qurbanaOne, qurbanaTwo))
        val baptism = makeNode("baptism", "root")
        val weddingOne = makeNode("wedding_One", "wedding")
        val weddingTwo = makeNode("wedding_Two", "wedding")
        val wedding = makeNode("wedding", "root", listOf(weddingOne, weddingTwo))
        val root =
            PageNodeDomain(
                route = "root",
                parent = null,
                children = listOf(qurbana, baptism, wedding),
            )
        val (prev, next) = useCase(root, baptism)
        assertNull(prev)
        assertNull(next)
    }

    @Test
    fun `return null if parent is feasts`() {
        val child1 = makeNode("christmas", "feasts")
        val child2 = makeNode("epiphany", "feasts")
        val child3 = makeNode("palmSundayService", "feasts")
        val feasts =
            PageNodeDomain(
                "feasts",
                parent = null,
                children = listOf(child1, child2, child3),
            )
        val (prev, next) = useCase(feasts, child2)
        assertNull(prev)
        assertNull(next)
    }

    @Test
    fun `return null if parent is sacraments`() {
        val child1 = makeNode("prayer_One", "sacraments")
        val child2 = makeNode("prayer_Two", "sacraments")
        val child3 = makeNode("prayer_Three", "sacraments")
        val sacraments =
            PageNodeDomain(
                "sacraments",
                parent = null,
                children = listOf(child1, child2, child3),
            )
        val (prev, next) = useCase(sacraments, child2)
        assertNull(prev)
        assertNull(next)
    }
}
