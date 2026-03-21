package com.paradox543.malankaraorthodoxliturgica.domain.calendar.repository

import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.CalendarDay
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.CalendarWeek
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.LiturgicalEventDetails

interface CalendarRepository {
    fun checkMonthDataExists(
        month: Int,
        year: Int,
    ): Boolean

    fun loadMonthData(
        month: Int? = null,
        year: Int? = null,
    ): List<CalendarWeek>

    fun getUpcomingWeekEvents(): List<CalendarDay>

    fun getUpcomingWeekEventItems(): List<LiturgicalEventDetails>
}