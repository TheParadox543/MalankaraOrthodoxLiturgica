package com.paradox543.malankaraorthodoxliturgica.shared.prayer

import com.paradox543.malankaraorthodoxliturgica.data.prayer.mapping.toData
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElement
import kotlinx.serialization.json.Json

object PrayerExportService {
    private val json =
        Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
        }

    fun export(elements: List<PrayerElement>): String {
        val dtoList = elements.map { it.toData() }
        return json.encodeToString(dtoList)
    }
}