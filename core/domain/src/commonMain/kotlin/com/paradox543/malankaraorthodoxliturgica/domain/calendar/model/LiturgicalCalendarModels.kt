package com.paradox543.malankaraorthodoxliturgica.domain.calendar.model

typealias EventKey = String

// Structure for liturgical_calendar.json
typealias DayEvents = List<EventKey> // List of EventKeys
typealias MonthEvents = Map<String, DayEvents> // Maps day (e.g., "1") to DayEvents
typealias YearEvents = Map<String, MonthEvents> // Maps month (e.g., "1") to MonthEvents
typealias LiturgicalCalendarDates = Map<String, YearEvents> // Maps year (e.g., "2024") to YearEvents
