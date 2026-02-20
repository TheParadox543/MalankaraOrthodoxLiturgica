package com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerRoutes
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

class GetRecommendedPrayersUseCase {
    operator fun invoke(now: LocalDateTime = LocalDateTime.now()): List<String> {
        val list = mutableListOf<String>()

        val hour = now.hour
        var dayIndex = now.dayOfWeek.value - 1
        if (hour >= 18) dayIndex++
        if (dayIndex > 6) dayIndex = 0

        val day = DayOfWeek.of(dayIndex + 1)

        // Date range check: 15 February .. 29 March (inclusive) for the current year
        val year = now.year
        val today = now.toLocalDate()
        val greatLentStart = LocalDate.of(2026, Month.FEBRUARY, 15)
        val hosanna = LocalDate.of(2026, Month.MARCH, 29)
        val isGreatLentSeason = !today.isBefore(greatLentStart) && !today.isAfter(hosanna)

        val easterDate = LocalDate.of(2026, 4, 5)
        val sleebaDate = LocalDate.of(year, 9, 14)
        val isKyamthaSeason = !today.isBefore(easterDate) && !today.isAfter(sleebaDate)

        fun addFor(option: String) {
            if (hour in 18..21) list.add("${option}_${PrayerRoutes.VESPERS}")
            if (hour >= 18) list.add("${option}_${PrayerRoutes.COMPLINE}")
            if (hour !in 7..<20) list.add("${option}_${PrayerRoutes.MATINS}")
            if (hour in 5..11) list.add("${option}_${PrayerRoutes.PRIME}")
            if (hour in 5..17) {
                list.add("${option}_${PrayerRoutes.TERCE}")
                list.add("${option}_${PrayerRoutes.SEXT}")
            }
            if (hour in 11..17) list.add("${option}_${PrayerRoutes.NONE}")
        }

        if (day != DayOfWeek.SUNDAY) {
            val name = day.getDisplayName(TextStyle.FULL, Locale.ENGLISH).lowercase()
            if (isGreatLentSeason && day != DayOfWeek.SATURDAY) {
                addFor("greatLent_$name")
                addFor("greatLent_general")
            } else {
                addFor("sheema_$name")
            }
        } else if (isKyamthaSeason) {
            addFor("kyamtha")
        }

        if (day == DayOfWeek.SUNDAY && hour in 6..13) {
            list +=
                listOf(
                    "${PrayerRoutes.QURBANA}_${PrayerRoutes.PREPARATION}",
                    "${PrayerRoutes.QURBANA}_${PrayerRoutes.PARTONE}",
                    "${PrayerRoutes.QURBANA}_${PrayerRoutes.CHAPTERONE}",
                    "${PrayerRoutes.QURBANA}_${PrayerRoutes.CHAPTERTWO}",
                    "${PrayerRoutes.QURBANA}_${PrayerRoutes.CHAPTERTHREE}",
                    "${PrayerRoutes.QURBANA}_${PrayerRoutes.CHAPTERFOUR}",
                    "${PrayerRoutes.QURBANA}_${PrayerRoutes.CHAPTERFIVE}",
                )
        }

//        if ((day == DayOfWeek.SUNDAY || day == DayOfWeek.MONDAY) && hour in 10..12) {
//            list += listOf("wedding_ring", "wedding_crown")
//        }

        addFor("sleeba")

        return list.distinct()
    }
}