package com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerRoutes
import java.time.DayOfWeek
import java.time.LocalDateTime
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

        if (day != DayOfWeek.SATURDAY) {
            val name = day.getDisplayName(TextStyle.FULL, Locale.ENGLISH).lowercase()
            addFor("sheema_$name")
        } else {
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

        if ((day == DayOfWeek.SUNDAY || day == DayOfWeek.MONDAY) && hour in 10..12) {
            list += listOf("wedding_ring", "wedding_crown")
        }

        addFor("sleeba")

        return list.distinct()
    }
}