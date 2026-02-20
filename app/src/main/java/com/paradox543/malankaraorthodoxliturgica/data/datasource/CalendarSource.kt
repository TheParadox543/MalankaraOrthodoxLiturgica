package com.paradox543.malankaraorthodoxliturgica.data.datasource

import com.paradox543.malankaraorthodoxliturgica.data.calendar.model.LiturgicalDataStore
import com.paradox543.malankaraorthodoxliturgica.data.core.datasource.AssetJsonReader
import com.paradox543.malankaraorthodoxliturgica.data.model.LiturgicalCalendarDates
import javax.inject.Inject

class CalendarSource @Inject constructor(
    private val reader: AssetJsonReader,
) {
    fun readLiturgicalData(): LiturgicalDataStore? =
//        withContext(Dispatchers.IO) {
        reader.loadJsonAsset<LiturgicalDataStore>("calendar/liturgical_data.json")
//        }

    fun readLiturgicalDates(): LiturgicalCalendarDates? =
//        withContext(Dispatchers.IO) {
        reader.loadJsonAsset<LiturgicalCalendarDates>("calendar/liturgical_calendar.json")
//        }
}