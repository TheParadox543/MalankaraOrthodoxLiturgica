package com.paradox543.malankaraorthodoxliturgica.domain.fakes

import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.CalendarDay
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.CalendarWeek
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.LiturgicalDay
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.LiturgicalEventDetails
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.SeasonName
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.WeekItem
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.repository.CalendarRepository
import kotlinx.datetime.LocalDate

/**
 * Test fake for [CalendarRepository].
 */
class FakeCalendarRepository(
    private val weeks: List<CalendarWeek> = emptyList(),
    private val upcomingDays: List<CalendarDay> = emptyList(),
    private val upcomingEventItems: List<LiturgicalEventDetails> = emptyList(),
    private val eventsMap: Map<String, LiturgicalEventDetails> = emptyMap(),
) : CalendarRepository {
    override suspend fun getDay(day: LocalDate): LiturgicalDay? {
        TODO("Not yet implemented")
    }

    override suspend fun getRange(
        start: LocalDate,
        end: LocalDate,
    ): List<LiturgicalDay> {
        TODO("Not yet implemented")
    }

    override suspend fun getSeasonDays(
        liturgicalYear: String,
        seasonName: SeasonName,
    ): List<LiturgicalDay> {
        TODO("Not yet implemented")
    }

    override suspend fun getSeasonWeeks(
        liturgicalYear: String,
        seasonName: SeasonName,
    ): List<WeekItem> = emptyList()

    override suspend fun getMonthDays(
        year: Int,
        month: Int,
    ): List<LiturgicalDay> = emptyList()

    override suspend fun getMonthWeeks(
        year: Int,
        month: Int,
    ): List<WeekItem> = emptyList()

    override suspend fun getUpcomingDays(count: Int): List<LiturgicalDay> = emptyList()

    override suspend fun checkMonthDataExists(
        month: Int,
        year: Int,
    ): Boolean = true

    override suspend fun loadMonthData(
        month: Int?,
        year: Int?,
    ): List<CalendarWeek> = weeks

    override suspend fun getUpcomingWeekEvents(): List<CalendarDay> = upcomingDays

    override suspend fun getUpcomingWeekEventItems(): List<LiturgicalEventDetails> = upcomingEventItems

    override suspend fun getEvents(eventKeys: List<String>): List<LiturgicalEventDetails> =
        eventKeys.mapNotNull { eventsMap[it] }

    override suspend fun hasLiturgicalYear(liturgicalYear: String): Boolean = true
}
