package com.paradox543.malankaraorthodoxliturgica

import com.paradox543.malankaraorthodoxliturgica.data.bible.mapping.toData
import com.paradox543.malankaraorthodoxliturgica.data.bible.mapping.toDomain
import com.paradox543.malankaraorthodoxliturgica.data.prayer.model.PageNodeDto
import com.paradox543.malankaraorthodoxliturgica.shared.domain.model.PageNodeDomain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PageNodeMapperTest {
    @Test
    fun `simple node maps to domain and back`() {
        val data =
            PageNodeDto(
                route = "home",
                type = "section",
                filename = "index.html",
                parent = null,
                children = emptyList(),
                languages = listOf("en", "ml"),
            )

        val domain = data.toDomain()
        assertEquals(PageNodeDomain::class, domain::class)
        assertEquals("home", domain.route)
        assertEquals("index.html", domain.filename)
        assertEquals(null, domain.parent)
        assertEquals(0, domain.children.size)
        assertEquals(listOf("en", "ml"), domain.languages)

        val roundtrip = domain.toData()
        assertEquals(data, roundtrip)
    }

    @Test
    fun `nested children map correctly`() {
        val grandChild =
            PageNodeDto(
                route = "home/section/item",
                filename = "item.html",
                parent = "home/section",
                children = emptyList(),
                languages = listOf(),
            )
        val child =
            PageNodeDto(route = "home/section", filename = null, parent = "home", children = listOf(grandChild), languages = listOf("en"))
        val root =
            PageNodeDto(route = "home", filename = "index.html", parent = null, children = listOf(child), languages = listOf("en", "ml"))

        val domain = root.toDomain()
        // root checks
        assertEquals("home", domain.route)
        assertEquals(1, domain.children.size)
        val domainChild = domain.children[0]
        assertEquals("home/section", domainChild.route)
        assertEquals(1, domainChild.children.size)
        val domainGrandChild = domainChild.children[0]
        assertEquals("home/section/item", domainGrandChild.route)
        assertEquals("item.html", domainGrandChild.filename)

        val back = domain.toData()
        assertEquals(root, back)
    }

    @Test
    fun `empty lists and null filename handled`() {
        val node = PageNodeDto(route = "empty", filename = null, parent = null, children = emptyList(), languages = emptyList())
        val domain = node.toDomain()
        assertNull(domain.filename)
        assertTrue(domain.children.isEmpty())
        assertTrue(domain.languages.isEmpty())

        val back = domain.toData()
        assertEquals(node, back)
    }
}
