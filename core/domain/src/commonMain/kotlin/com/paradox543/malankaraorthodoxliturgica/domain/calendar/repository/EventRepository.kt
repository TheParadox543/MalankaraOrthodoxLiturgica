package com.paradox543.malankaraorthodoxliturgica.domain.calendar.repository

import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.LiturgicalEventDetails

interface EventRepository {
    fun getEvent(key: String): LiturgicalEventDetails
}