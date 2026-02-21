package com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerRoutes
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

class GetRecommendedPrayersUseCase {
    operator fun invoke(civilDate: LocalDateTime): List<String> {
        val list = mutableListOf<String>()
        val liturgicalDate = if (civilDate.hour >= 16) civilDate.plusDays(1) else civilDate

        // TODO: Change the season every year, or until calendar repo can pass this data.
        // Date range check: 15 February to 29 March (inclusive) for the current year, 2026.
        val year = liturgicalDate.year
        val today = liturgicalDate.toLocalDate()
        val greatLentStart = LocalDate.of(2026, Month.FEBRUARY, 15)
        val hosanna = LocalDate.of(2026, Month.MARCH, 29)
        val isGreatLentSeason = !today.isBefore(greatLentStart) && !today.isAfter(hosanna)

        val easterDate = LocalDate.of(2026, 4, 5)
        val sleebaDate = LocalDate.of(year, 9, 14)
        val isKyamthaSeason = !today.isBefore(easterDate) && !today.isAfter(sleebaDate)

        fun getKeyName(day: DayOfWeek): String = day.getDisplayName(TextStyle.FULL, Locale.ENGLISH).lowercase()

        fun addFor(option: String) {
            val civilKey = option.replace("day", getKeyName(civilDate.dayOfWeek))
            val liturgicalKey = option.replace("day", getKeyName(liturgicalDate.dayOfWeek))
            if (civilDate.hour < 7) list.add("${civilKey}_${PrayerRoutes.MATINS}")
            if (civilDate.hour in 4..9) list.add("${civilKey}_${PrayerRoutes.PRIME}")
            if (civilDate.hour in 5..12) list.add("${civilKey}_${PrayerRoutes.TERCE}")
            if (civilDate.hour in 11..15) list.add("${civilKey}_${PrayerRoutes.SEXT}")
            if (civilDate.hour in 11..18) list.add("${civilKey}_${PrayerRoutes.NONE}")

            if (civilDate.hour in 16..20) {
                list.add("${liturgicalKey}_${PrayerRoutes.VESPERS}")
                list.add("${liturgicalKey}_${PrayerRoutes.COMPLINE}")
            }
            if (civilDate.hour > 20) list.add("${liturgicalKey}_${PrayerRoutes.MATINS}")
        }

        if (isGreatLentSeason) {
            addFor("greatLent_day")
            addFor("greatLent_general")
            if (liturgicalDate.dayOfWeek == DayOfWeek.SATURDAY) {
                addFor("sheema_day")
            }
        } else {
            addFor("sheema_day")
        }
        if (isKyamthaSeason) {
            addFor("kyamtha")
        }

        if (civilDate.dayOfWeek == DayOfWeek.SUNDAY && civilDate.hour in 6..13) {
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
        // Add wedding prayers once great lent season is over.
        if (!isGreatLentSeason) {
            if ((liturgicalDate.dayOfWeek == DayOfWeek.SUNDAY || liturgicalDate.dayOfWeek == DayOfWeek.MONDAY) &&
                civilDate.hour in 10..12
            ) {
                list += listOf("wedding_ring", "wedding_crown")
            }
        }

        addFor("sleeba")

        return list.distinct()
    }
}