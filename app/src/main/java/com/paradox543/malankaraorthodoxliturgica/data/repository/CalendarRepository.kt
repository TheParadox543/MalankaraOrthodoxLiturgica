package com.paradox543.malankaraorthodoxliturgica.data.repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CalendarRepository @Inject constructor(
    @ApplicationContext private val context: Context
)
{
    fun loadCalendarKeys(){
        // TODO: Read calendar events from calendar_events.json
    }

    fun loadEventDetails() {
        // TODO: Read event details from event_details.json
    }


}