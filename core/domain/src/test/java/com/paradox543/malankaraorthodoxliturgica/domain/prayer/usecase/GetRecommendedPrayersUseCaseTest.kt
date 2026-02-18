package com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerRoutes
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class GetRecommendedPrayersUseCaseTest {
    private val useCase = GetRecommendedPrayersUseCase()

    @Test
    fun `returns Qurbana prayers on Sunday morning`() {
        // Sunday at 9 AM
        val sundayMorning = LocalDateTime.of(2026, 2, 15, 9, 0)
        val result = useCase(sundayMorning)

        assertTrue(result.any { it.contains(PrayerRoutes.QURBANA) })
    }

    @Test
    fun `returns Sheema prayers on a regular weekday`() {
        // Monday at 9 AM (not in Great Lent season)
        val monday = LocalDateTime.of(2025, 6, 16, 9, 0)
        val result = useCase(monday)

        assertTrue(result.any { it.startsWith("sheema_monday") })
    }

    @Test
    fun `returns Great Lent prayers during lent season on weekdays`() {
        // Wednesday during Great Lent (Feb 18, 2026 is a Wednesday)
        val lentWednesday = LocalDateTime.of(2026, 2, 18, 9, 0)
        val result = useCase(lentWednesday)

        assertTrue(result.any { it.startsWith("greatLent_") })
    }

    @Test
    fun `returns Sheema prayers on Saturday during Great Lent`() {
        // Saturday during Great Lent (Feb 21, 2026 is a Saturday)
        val lentSaturday = LocalDateTime.of(2026, 2, 21, 9, 0)
        val result = useCase(lentSaturday)

        // Saturday is excluded from Great Lent, should use sheema
        assertTrue(result.any { it.startsWith("sheema_saturday") })
    }

    @Test
    fun `returns Kyamtha prayers on Sunday during Easter season`() {
        // Sunday during Kyamtha season (April 12, 2026 is a Sunday after Easter)
        val kyamthaSunday = LocalDateTime.of(2026, 4, 12, 9, 0)
        val result = useCase(kyamthaSunday)

        assertTrue(result.any { it.startsWith("kyamtha") })
    }

    @Test
    fun `always includes Sleeba prayers`() {
        val monday9am = LocalDateTime.of(2025, 6, 16, 9, 0)
        val result = useCase(monday9am)

        assertTrue(result.any { it.startsWith("sleeba_") })
    }

    @Test
    fun `returns distinct results with no duplicates`() {
        val monday9am = LocalDateTime.of(2025, 6, 16, 9, 0)
        val result = useCase(monday9am)

        assertEquals(result.size, result.distinct().size)
    }

    @Test
    fun `includes Vespers and Compline in evening hours`() {
        // Monday at 7 PM (hour=19)
        val mondayEvening = LocalDateTime.of(2025, 6, 16, 19, 0)
        val result = useCase(mondayEvening)

        assertTrue(result.any { it.endsWith(PrayerRoutes.VESPERS) })
        assertTrue(result.any { it.endsWith(PrayerRoutes.COMPLINE) })
    }

    @Test
    fun `includes Matins in early morning hours`() {
        // Monday at 5 AM (hour=5)
        val mondayEarlyMorning = LocalDateTime.of(2025, 6, 16, 5, 0)
        val result = useCase(mondayEarlyMorning)

        assertTrue(result.any { it.endsWith(PrayerRoutes.MATINS) })
    }

    @Test
    fun `adjusts day index after 6 PM so next day prayers are included`() {
        // Sunday at 7 PM (hour=19): dayIndex should shift to Monday
        val sundayEvening = LocalDateTime.of(2025, 6, 15, 19, 0)
        val result = useCase(sundayEvening)

        // After 6 PM on Sunday, should recommend Monday prayers
        assertTrue(result.any { it.startsWith("sheema_monday") || it.startsWith("sleeba_monday") })
    }

    @Test
    fun `does not include Qurbana prayers outside Sunday morning hours`() {
        // Sunday at 3 PM (hour=15)
        val sundayAfternoon = LocalDateTime.of(2026, 2, 15, 15, 0)
        val result = useCase(sundayAfternoon)

        assertFalse(result.any { it.contains(PrayerRoutes.QURBANA) })
    }

    private fun assertEquals(expected: Int, actual: Int) {
        org.junit.Assert.assertEquals(expected, actual)
    }
}
