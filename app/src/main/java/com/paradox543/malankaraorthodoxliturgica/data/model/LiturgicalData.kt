package com.paradox543.malankaraorthodoxliturgica.data.model

import kotlinx.serialization.Serializable

// Represents localized text for titles, notes, etc.
@Serializable
data class LiturgicalText(
    val en: String, // English
    val ml: String? = null // Malayalam, or make non-nullable if always present
)

// Represents the full details of a liturgical event
@Serializable
data class LiturgicalEventDetails(
    val type: String, // e.g., "Feast", "Commemoration", "Anniversary", "Ordinary"
    val title: LiturgicalText,
    val readings: List<String>? = null, // List of reading keys (e.g., "ROM_1_1-7")
    val notes: LiturgicalText? = null,   // Optional notes about the event
    val color: String? = null,           // Liturgical color (e.g., "Green", "Red", "White")
    val fasting_level: String? = null    // Fasting rule (e.g., "No Fast", "Normal Fast", "Strict Fast")
)

// Type aliases for the structure of liturgical_calendar.json
// Example: { "2025": { "1": { "1": ["FEAST_CIRCUMCISION", "COMM_ST_BASIL"] } } }

// Represents a list of event keys for a specific day (e.g., ["FEAST_CIRCUMCISION"])
typealias DayKeysForDate = List<String>

// Represents a map of day (String) to its list of DayKeysForDate (nullable list for absent days)
typealias MonthKeys = Map<String, DayKeysForDate?>

// Represents a map of month (String) to its MonthKeys
typealias YearKeys = Map<String, MonthKeys>

