package com.paradox543.malankaraorthodoxliturgica.data.datasource

import com.paradox543.malankaraorthodoxliturgica.data.core.datasource.AssetJsonReader
import com.paradox543.malankaraorthodoxliturgica.data.model.LiturgicalCalendarDates
import com.paradox543.malankaraorthodoxliturgica.data.model.LiturgicalDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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