package com.paradox543.malankaraorthodoxliturgica.data.calendar.datasource

import com.paradox543.malankaraorthodoxliturgica.data.calendar.model.LiturgicalDataStore
import com.paradox543.malankaraorthodoxliturgica.data.core.datasource.ResourceTextReader
import com.paradox543.malankaraorthodoxliturgica.data.core.exceptions.AssetParsingException
import com.paradox543.malankaraorthodoxliturgica.data.core.exceptions.AssetReadException
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.LiturgicalCalendarDates
import kotlinx.serialization.json.Json

class CalendarSource(
    private val reader: ResourceTextReader,
    private val json: Json,
) {
    suspend fun readLiturgicalData(): LiturgicalDataStore {
        val jsonString = try {
            reader.readText("calendar/liturgical_data.json")
        } catch (t: Throwable) {
            throw AssetReadException("Failed to read asset at path: calendar/liturgical_data.json", t)
        }

        return try {
            json.decodeFromString<LiturgicalDataStore>(jsonString)
        } catch (t: Throwable) {
            throw AssetParsingException("Failed to parse asset at path: calendar/liturgical_data.json", t)
        }
    }

    suspend fun readLiturgicalDates(): LiturgicalCalendarDates {
        val jsonString = try {
            reader.readText("calendar/liturgical_calendar.json")
        } catch (t: Throwable) {
            throw AssetReadException("Failed to read asset at path: calendar/liturgical_calendar.json", t)
        }

        return try {
            json.decodeFromString<LiturgicalCalendarDates>(jsonString)
        } catch (t: Throwable) {
            throw AssetParsingException("Failed to parse asset at path: calendar/liturgical_calendar.json", t)
        }
    }
}