package com.paradox543.malankaraorthodoxliturgica.domain.calendar.model

import kotlinx.datetime.LocalDate

typealias EventKey = String

// Structure for liturgical data
typealias CalendarData = Map<LocalDate, LiturgicalDay> // Maps LocalDate to LiturgicalDay