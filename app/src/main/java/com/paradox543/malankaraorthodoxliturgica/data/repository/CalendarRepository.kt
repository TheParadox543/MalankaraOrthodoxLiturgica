package com.paradox543.malankaraorthodoxliturgica.data.repository

//import android.content.Context
//import android.util.Log
//import com.paradox543.malankaraorthodoxliturgica.data.model.DateKeysInMonth
//import com.paradox543.malankaraorthodoxliturgica.data.model.LiturgicalEventDetails
//import com.paradox543.malankaraorthodoxliturgica.data.model.MonthKeysInYear
//import com.paradox543.malankaraorthodoxliturgica.data.model.YearKeys
//import dagger.hilt.android.qualifiers.ApplicationContext
//import kotlinx.serialization.json.Json
//import javax.inject.Inject
//
//class CalendarRepository @Inject constructor(
//    @ApplicationContext private val context: Context,
//    private val json: Json
//)
//{
//    private val cachedCalendarKeys: YearKeys by lazy {
//        loadJsonAsset<YearKeys>("calendar/liturgical_calendar.json") ?: emptyMap()
//    }
//
//    private val cachedEventDetails: Map<String, LiturgicalEventDetails> by lazy {
//        loadJsonAsset<Map<String, LiturgicalEventDetails>>("calendar/liturgical_data.json") ?: emptyMap()
//    }
//
//    private inline fun <reified T> loadJsonAsset(fileName: String): T? {
//        return try {
//            context.assets.open(fileName).use { inputStream ->
//                val jsonString = inputStream.bufferedReader().use { it.readText() }
//                json.decodeFromString<T>(jsonString)
//            }
//        } catch (e: Exception) {
//            Log.e("CalendarRepository", "Error loading or parsing $fileName: ${e.message}", e)
//            null
//        }
//    }
//
//    /**
//     * Retrieves the map of day keys for a specific year and month.
//     * This map indicates which event keys are associated with each day.
//     *
//     * @param year The year (e.g., 2025).
//     * @param month The month (1-indexed, e.g., 5 for May).
//     * @return A Map<String, DayKeysForDate?> where key is day (String) and value is list of event keys,
//     * or null if no data for the specified month/year.
//     */
//    suspend fun getDateKeys(year: Int, month: Int): DateKeysInMonth? {
//        // Access cached data directly
//        return cachedCalendarKeys["$year"]?.get("$month")
//    }
//
////    suspend fun getLiturgicalDataForDate(year: Int, month: Int, day: Int): List<LiturgicalEventDetails> {
////        val yearData: MonthKeys? = cachedCalendarKeys["$year"]
////        val monthData: Map<String, DayKeysForDate?>? = yearData?.get("$month")
////        val dayKeys: DayKeysForDate? = monthData?.get("$day")
////
////        val eventsForDay = mutableListOf<LiturgicalEventDetails>()
////
////        if (!dayKeys.isNullOrEmpty()) {
////            for (key in dayKeys) {
////                cachedEventDetails[key]?.let { details ->
////                    eventsForDay.add(details)
////                } ?: run {
////                    Log.w("CalendarRepository", "Missing event details for key: '$key' on $year-$month-$day. Using default unknown.")
////                    eventsForDay.add(defaultUnknownEventDetails)
////                }
////            }
////        } else {
////            eventsForDay.add(defaultOrdinaryDayDetails)
////        }
////        return events
////    }
//
//    suspend fun loadCalendarKeysMap(): YearKeys = cachedCalendarKeys
//    suspend fun loadEventDetailsMap(): Map<String, LiturgicalEventDetails> = cachedEventDetails
//}