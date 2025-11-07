package com.paradox543.malankaraorthodoxliturgica.domain.repository

import com.paradox543.malankaraorthodoxliturgica.data.model.CalendarDay
import com.paradox543.malankaraorthodoxliturgica.data.model.CalendarWeek
import com.paradox543.malankaraorthodoxliturgica.data.model.LiturgicalEventDetails
import java.time.LocalDate

interface CalendarRepository {
    fun getEventsForDate(date: LocalDate): List<LiturgicalEventDetails>

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