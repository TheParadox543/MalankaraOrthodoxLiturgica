package com.paradox543.malankaraorthodoxliturgica.data.repository

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.paradox543.malankaraorthodoxliturgica.data.model.CalendarDay
import com.paradox543.malankaraorthodoxliturgica.data.model.CalendarWeek
import com.paradox543.malankaraorthodoxliturgica.data.model.EventKey
import com.paradox543.malankaraorthodoxliturgica.data.model.LiturgicalCalendarDates
import com.paradox543.malankaraorthodoxliturgica.data.model.LiturgicalDataStore
import com.paradox543.malankaraorthodoxliturgica.data.model.LiturgicalEventDetails
import com.paradox543.malankaraorthodoxliturgica.data.model.MonthEvents
import com.paradox543.malankaraorthodoxliturgica.data.model.TitleStr
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.IOException
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

class LiturgicalCalendarRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json // Inject kotlinx.serialization.Json for parsing
) {
    // Lazy initialization ensures files are read only when first accessed
    private lateinit var liturgicalDates: LiturgicalCalendarDates
    private lateinit var liturgicalData: LiturgicalDataStore

    // Initial load, to be called in an appropriate scope (e.g., ViewModel init or App startup)
    suspend fun initialize() {
        if (!::liturgicalDates.isInitialized) { // Check if already initialized
            readLiturgicalDates()
        }
        if (!::liturgicalData.isInitialized) {
            readLiturgicalData()
        }
    }

    private suspend fun readLiturgicalDates() = withContext(Dispatchers.IO) {
        val filename = "calendar/liturgical_calendar.json"
        try {
            context.assets.open(filename).bufferedReader().use { reader ->
                val jsonString = reader.readText()
                liturgicalDates = json.decodeFromString<LiturgicalCalendarDates>(jsonString)
            }
        } catch (e: IOException) {
            System.err.println("File $filename not found or could not be read. Error: ${e.message}")
            throw e
        } catch (e: SerializationException) {
            System.err.println("Error decoding JSON from $filename. Please check file format. Error: ${e.message}")
            throw e
        }
    }

    private suspend fun readLiturgicalData() = withContext(Dispatchers.IO) {
        val filename = "calendar/liturgical_data.json"
        try {
            context.assets.open(filename).bufferedReader().use { reader ->
                val jsonString = reader.readText()
                liturgicalData = json.decodeFromString<LiturgicalDataStore>(jsonString)
            }
        } catch (e: IOException) {
            System.err.println("File $filename not found or could not be read. Error: ${e.message}")
            throw e
        } catch (e: SerializationException) {
            System.err.println("Error decoding JSON from $filename. Please check file format. Error: ${e.message}")
            throw e
        }
    }

    /**
     * Internal helper to get event keys for a specific date.
     */
    private fun getEventKeysForDate(day: LocalDate): List<EventKey> {
        // Ensure data is initialized before accessing
        if (!::liturgicalDates.isInitialized || !::liturgicalData.isInitialized) {
            throw IllegalStateException("LiturgicalCalendarRepository not initialized. Call initialize() first.")
        }

        return liturgicalDates[day.year.toString()]
            ?.get(day.monthValue.toString())
            ?.get(day.dayOfMonth.toString())
            ?: emptyList() // Return empty list if no events for the day
    }

    /**
     * Get detailed event information for a given date.
     * @param date The LocalDate object for which to retrieve events.
     * @return A map where keys are EventKeys and values are LiturgicalEventDetails.
     * @throws IllegalArgumentException if an event key found in liturgical_calendar.json
     * is not present in liturgical_data.json.
     */
    fun getEventsForDate(date: LocalDate): List<LiturgicalEventDetails> {
        val eventKeys = getEventKeysForDate(date)
        val eventDetails = mutableListOf<LiturgicalEventDetails>()

        for (key in eventKeys) {
            val details = liturgicalData[key]
            if (details != null) {
                eventDetails.add(details)
            } else {
                throw IllegalArgumentException("Could not find event key '$key' in liturgical_data.json.")
            }
        }
        return eventDetails
    }

    fun checkMonthDataExists(month: Int, year: Int): Boolean {
        // Ensure data is initialized
        if (!::liturgicalDates.isInitialized || !::liturgicalData.isInitialized) {
            throw IllegalStateException("LiturgicalCalendarRepository not initialized. Call initialize() first.")
        }
        return liturgicalDates[year.toString()]?.get(month.toString()) is MonthEvents
    }

    /**
     * Loads the calendar data for a specific month and year, structured by weeks.
     * Each week starts on Sunday.
     * @param month The month (1-12). Defaults to current month if null.
     * @param year The year. Defaults to current year if null.
     * @return A list of CalendarWeek objects, each containing 7 CalendarDay objects.
     */
    fun loadMonthData(month: Int? = null, year: Int? = null): List<CalendarWeek> {
        // Ensure data is initialized
        if (!::liturgicalDates.isInitialized || !::liturgicalData.isInitialized) {
            throw IllegalStateException("LiturgicalCalendarRepository not initialized. Call initialize() first.")
        }

        val targetYear = year ?: LocalDate.now().year
        val targetMonth = month ?: LocalDate.now().monthValue

        require(targetMonth in 1..12) { "Month must be between 1 and 12." }

        val firstDayOfMonth = LocalDate.of(targetYear, targetMonth, 1)
        val lastDayOfMonth = firstDayOfMonth.with(TemporalAdjusters.lastDayOfMonth())

        // Calculate the first day of the calendar grid (Sunday of the first week)
        var currentDay = firstDayOfMonth
        while (currentDay.dayOfWeek != DayOfWeek.SUNDAY) {
            currentDay = currentDay.minusDays(1)
        }

        val monthData = mutableListOf<CalendarWeek>()
        var weekDays = mutableListOf<CalendarDay>()

        // Iterate through days, forming weeks
        while (currentDay.isBefore(lastDayOfMonth.plusDays(1)) || currentDay.dayOfWeek != DayOfWeek.SUNDAY) {
            val events = getEventsForDate(currentDay)
            weekDays.add(CalendarDay(currentDay, events))

            if (weekDays.size == 7) {
                monthData.add(CalendarWeek(weekDays.toList()))
                weekDays = mutableListOf() // Start a new week
            }
            currentDay = currentDay.plusDays(1)
        }

        // Add any remaining days for the last week (if not a full week)
        if (weekDays.isNotEmpty()) {
            // Pad with empty days from next month if necessary to complete the last week,
            // though the logic above should ensure full weeks are added already.
            // If the loop finished on Saturday, weekDays would be empty.
            // If it finished mid-week (e.g. month ends on Wednesday), it will contain
            // days from the month and then days from the next month until Saturday.
            while (weekDays.size < 7) {
                weekDays.add(CalendarDay(currentDay, emptyList())) // Add placeholder for visual alignment
                currentDay = currentDay.plusDays(1)
            }
            monthData.add(CalendarWeek(weekDays.toList()))
        }


        return monthData
    }

    /**
     * Get events for the upcoming week starting from today.
     * @return A list of CalendarDay objects for the next 7 days, including their events.
     * Only days with events will have non-empty event maps.
     */
    fun getUpcomingWeekEvents(): List<CalendarDay> {
        // Ensure data is initialized
        if (!::liturgicalDates.isInitialized || !::liturgicalData.isInitialized) {
            throw IllegalStateException("LiturgicalCalendarRepository not initialized. Call initialize() first.")
        }

        val today = LocalDate.now()
        val weekEvents = mutableListOf<CalendarDay>()

        for (i in 0 until 7) {
            val day = today.plusDays(i.toLong())
            val eventDetails = getEventsForDate(day)
            // Python's code appended only if event_details != {}, but here we append
            // CalendarDay regardless for consistent list size, and the `events` map
            // will be empty if no events are found, similar to the month data.
            weekEvents.add(CalendarDay(day, eventDetails))
        }
        return weekEvents
    }
}