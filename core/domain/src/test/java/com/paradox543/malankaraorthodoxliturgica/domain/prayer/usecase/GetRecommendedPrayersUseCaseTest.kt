package com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerRoutes
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GetRecommendedPrayersUseCaseTest {
    private val useCase = GetRecommendedPrayersUseCase()

    data class HourCase(
        val hour: Int,
        val minute: Int = 0,
        val expected: List<String>,
    )

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
        // Sunday during Kyamtha season (April 12, 2026, is a Sunday after Easter)
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

    @Test
    fun `great lent weekday full 24 hour mapping`() {
        val date = LocalDate.of(2026, 2, 20) // inside great lent
        val cases =
            listOf(
                HourCase(0, expected = listOf("greatLent_friday_matins")),
                HourCase(1, expected = listOf("greatLent_friday_matins")),
                HourCase(2, expected = listOf("greatLent_friday_matins")),
                HourCase(3, expected = listOf("greatLent_friday_matins")),
                HourCase(4, expected = listOf("greatLent_friday_matins", "greatLent_friday_prime")),
                HourCase(5, expected = listOf("greatLent_friday_matins", "greatLent_friday_prime", "greatLent_friday_terce")),
                HourCase(6, expected = listOf("greatLent_friday_matins", "greatLent_friday_prime", "greatLent_friday_terce")),
                HourCase(7, expected = listOf("greatLent_friday_prime", "greatLent_friday_terce")),
                HourCase(8, expected = listOf("greatLent_friday_prime", "greatLent_friday_terce")),
                HourCase(9, expected = listOf("greatLent_friday_prime", "greatLent_friday_terce")),
                HourCase(10, expected = listOf("greatLent_friday_terce")),
                HourCase(11, expected = listOf("greatLent_friday_terce", "greatLent_friday_sext", "greatLent_friday_none")),
                HourCase(12, expected = listOf("greatLent_friday_terce", "greatLent_friday_sext", "greatLent_friday_none")),
                HourCase(13, expected = listOf("greatLent_friday_sext", "greatLent_friday_none")),
                HourCase(14, expected = listOf("greatLent_friday_sext", "greatLent_friday_none")),
                HourCase(15, expected = listOf("greatLent_friday_sext", "greatLent_friday_none")),
                HourCase(16, expected = listOf("greatLent_friday_none", "sheema_saturday_vespers", "sheema_saturday_compline")),
                HourCase(17, expected = listOf("greatLent_friday_none", "sheema_saturday_vespers", "sheema_saturday_compline")),
                HourCase(18, expected = listOf("greatLent_friday_none", "sheema_saturday_vespers", "sheema_saturday_compline")),
                HourCase(19, expected = listOf("sheema_saturday_vespers", "sheema_saturday_compline")),
                HourCase(20, expected = listOf("sheema_saturday_vespers", "sheema_saturday_compline")),
                HourCase(21, expected = listOf("sheema_saturday_matins")),
                HourCase(22, expected = listOf("sheema_saturday_matins")),
                HourCase(23, expected = listOf("sheema_saturday_matins")),
            )

        for (case in cases) {
            val time = date.atTime(case.hour, case.minute)
            val result = useCase(time)

            for (expected in case.expected) {
                assertTrue(
                    result.any { it.endsWith(expected) },
                    "Failed at ${case.hour}:${case.minute}. Could not find $expected",
                )
            }

//            assertEquals(
//                case.expected,
//                result,
//                "Failed at ${case.hour}:${case.minute}",
//            )
        }
    }
}
