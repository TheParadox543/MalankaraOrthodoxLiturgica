package com.paradox543.malankaraorthodoxliturgica.domain.calendar.repository

import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.CalendarDay
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.CalendarWeek
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.LiturgicalEventDetails

interface CalendarRepository {
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