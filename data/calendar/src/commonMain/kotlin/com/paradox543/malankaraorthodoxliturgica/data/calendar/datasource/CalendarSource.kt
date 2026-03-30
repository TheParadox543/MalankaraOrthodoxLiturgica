package com.paradox543.malankaraorthodoxliturgica.data.calendar.datasource

import com.paradox543.malankaraorthodoxliturgica.data.calendar.model.LiturgicalDataStore
import com.paradox543.malankaraorthodoxliturgica.data.core.datasource.AssetJsonReader
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.LiturgicalCalendarDates

class CalendarSource(
    private val reader: AssetJsonReader,
) {
    fun readLiturgicalData(): LiturgicalDataStore = reader.loadJsonAsset("calendar/liturgical_data.json")

    fun readLiturgicalDates(): LiturgicalCalendarDates = reader.loadJsonAsset("calendar/liturgical_calendar.json")
}