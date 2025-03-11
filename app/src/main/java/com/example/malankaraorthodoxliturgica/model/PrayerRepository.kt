package com.example.malankaraorthodoxliturgica.model

class PrayerRepository {
    fun getCategories(): List<String> = listOf("Daily Prayers", "Sacramental Prayers")

    fun getPrayers(category: String): List<String> {
        return when (category) {
            "Daily Prayers" -> listOf("Sleeba", "Kyamtha", "Nineveh Lent", "Great Lent")
            "Sacramental Prayers" -> listOf("Qurbana", "Baptism", "Wedding", "Funeral")
//            "Feast Day Prayers" -> listOf("Christmas", "Easter", "Ascension")
            else -> emptyList()
        }
    }

    fun getGreatLentDays(): List<String> = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")

    fun getDayPrayers(): List<String> = listOf("Sandhya", "Soothara", "Rathri", "Prabatham", "3rd Hour", "6th Hour", "9th Hour")

    fun getQurbanaSections(): List<String> = listOf(
        "Preparation",
        "Part One",
        "Part Two Chapter One",
        "Part Two Chapter Two",
        "Part Two Chapter Three",
        "Conclusion"
    )
}
