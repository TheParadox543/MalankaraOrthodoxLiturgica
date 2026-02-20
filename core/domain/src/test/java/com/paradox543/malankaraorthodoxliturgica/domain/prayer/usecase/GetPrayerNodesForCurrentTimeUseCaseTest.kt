package com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PageNodeDomain
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import java.time.LocalDateTime

class GetPrayerNodesForCurrentTimeUseCaseTest {
    private val getRecommendedPrayers = GetRecommendedPrayersUseCase()
    private val useCase = GetPrayerNodesForCurrentTimeUseCase(getRecommendedPrayers)

    @Test
    fun `returns matching nodes for recommended prayer routes`() {
        // Monday at 9 AM: sheema_monday prayers should be recommended
        val monday9am = LocalDateTime.of(2026, 2, 16, 9, 0) // Monday

        val keys = getRecommendedPrayers(monday9am)
        assertTrue(keys.isNotEmpty(), "Expected some recommended prayers at Monday 9am")

        // Build a tree containing at least one of the recommended routes
        val firstKey = keys.first()
        val matchingNode = PageNodeDomain(route = firstKey, parent = "root")
        val root = PageNodeDomain(
            route = "root",
            parent = null,
            children = listOf(matchingNode),
        )

        val result = useCase(root, monday9am)
        assertTrue(result.isNotEmpty(), "Expected at least one matching node")
        assertTrue(result.any { it.route == firstKey })
    }

    @Test
    fun `returns empty list when no routes match tree`() {
        val monday9am = LocalDateTime.of(2026, 2, 16, 9, 0)

        // Build a tree with routes that won't match any recommendations
        val root = PageNodeDomain(
            route = "root",
            parent = null,
            children = listOf(
                PageNodeDomain(route = "nonexistent/route", parent = "root"),
            ),
        )

        val result = useCase(root, monday9am)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `filters out null results from findByRoute`() {
        val monday9am = LocalDateTime.of(2026, 2, 16, 9, 0)
        val keys = getRecommendedPrayers(monday9am)

        // Only add one of the recommended routes to the tree
        val firstKey = keys.first()
        val matchingNode = PageNodeDomain(route = firstKey, parent = "root")
        val root = PageNodeDomain(
            route = "root",
            parent = null,
            children = listOf(matchingNode),
        )

        val result = useCase(root, monday9am)

        // Result should only contain the one matching node, not nulls
        assertFalse(result.size > keys.size)
        assertTrue(result.all { it.route == firstKey || keys.contains(it.route) })
    }
}
