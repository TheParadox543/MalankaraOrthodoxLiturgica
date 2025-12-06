package com.paradox543.malankaraorthodoxliturgica.domain.repository

import com.paradox543.malankaraorthodoxliturgica.data.model.LiturgicalEventDetailsData
import com.paradox543.malankaraorthodoxliturgica.domain.model.CalendarDay
import com.paradox543.malankaraorthodoxliturgica.domain.model.CalendarWeek
import com.paradox543.malankaraorthodoxliturgica.domain.model.LiturgicalEventDetails
import java.time.LocalDate

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