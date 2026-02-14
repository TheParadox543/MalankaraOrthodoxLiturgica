package com.paradox543.malankaraorthodoxliturgica.fakes

import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.CalendarDay
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.CalendarWeek
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.LiturgicalEventDetails
import com.paradox543.malankaraorthodoxliturgica.domain.repository.CalendarRepository

/**
 * Test fake for [CalendarRepository].
 */
class FakeCalendarRepository(
    private val weeks: List<CalendarWeek> = emptyList(),
    private val upcomingDays: List<CalendarDay> = emptyList(),
    private val upcomingEventItems: List<LiturgicalEventDetails> = emptyList(),
) : CalendarRepository {
    override fun checkMonthDataExists(
        month: Int,
        year: Int,
    ): Boolean = true

    override fun loadMonthData(
        month: Int?,
        year: Int?,
    ): List<CalendarWeek> = weeks

    override fun getUpcomingWeekEvents(): List<CalendarDay> = upcomingDays

    override fun getUpcomingWeekEventItems(): List<LiturgicalEventDetails> = upcomingEventItems
}
