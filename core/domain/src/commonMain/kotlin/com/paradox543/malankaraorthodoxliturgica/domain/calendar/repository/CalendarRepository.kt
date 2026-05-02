package com.paradox543.malankaraorthodoxliturgica.domain.calendar.repository

import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.CalendarDay
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.CalendarWeek
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.LiturgicalDay
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.LiturgicalEventDetails
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.LiturgicalWeek
import kotlinx.datetime.LocalDate

interface CalendarRepository {
    suspend fun getDay(day: LocalDate): LiturgicalDay?

    suspend fun getRange(
        start: LocalDate,
        end: LocalDate,
    ): List<LiturgicalDay>

    suspend fun getSeasonDays(season: String): List<LiturgicalDay>

    suspend fun getSeasonWeeks(season: String): List<LiturgicalWeek>

    suspend fun getMonthDays(
        year: Int,
        month: Int,
    ): List<LiturgicalDay>

    suspend fun getMonthWeeks(
        year: Int,
        month: Int,
    ): List<LiturgicalWeek>

    suspend fun getUpcomingDays(count: Int): List<LiturgicalDay>

    suspend fun checkMonthDataExists(
        month: Int,
        year: Int,
    ): Boolean

    suspend fun loadMonthData(
        month: Int? = null,
        year: Int? = null,
    ): List<CalendarWeek>

    suspend fun getUpcomingWeekEvents(): List<CalendarDay>

    suspend fun getUpcomingWeekEventItems(): List<LiturgicalEventDetails>
}