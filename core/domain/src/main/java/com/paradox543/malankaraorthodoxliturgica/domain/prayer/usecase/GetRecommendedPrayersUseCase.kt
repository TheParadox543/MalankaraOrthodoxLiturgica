package com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerRoutes
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

class GetRecommendedPrayersUseCase {
    @OptIn(ExperimentalTime::class)
    operator fun invoke(civilDate: LocalDateTime): List<String> {
        val timeZone = TimeZone.currentSystemDefault()
        val list = mutableListOf<String>()
        val civilDateInstant = civilDate.toInstant(timeZone)

        val liturgicalDateInstant =
            if (civilDate.hour >= 16) {
                civilDateInstant.plus(1, DateTimeUnit.DAY, timeZone)
            } else {
                civilDateInstant
            }
        val liturgicalDate: LocalDateTime = liturgicalDateInstant.toLocalDateTime(timeZone)

        // TODO: Change the season every year, or until calendar repo can pass this data.
        // Date range check: 15 February to 29 March (inclusive) for the current year, 2026.
        val year = liturgicalDate.year
        val today = liturgicalDate.date
        val greatLentStart = LocalDate(2026, Month.FEBRUARY, 15)
        val hosanna = LocalDate(2026, Month.MARCH, 29)
        val isGreatLentSeason = today >= greatLentStart && today <= hosanna

        val easterDate = LocalDate(2026, 4, 5)
        val sleebaDate = LocalDate(year, 9, 14)
        val isKyamthaSeason = today >= easterDate && today <= sleebaDate

        fun getKeyName(day: DayOfWeek): String = day.name.lowercase()

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