package com.paradox543.malankaraorthodoxliturgica.core.platform

/**
 * Abstraction over the app's analytics backend (e.g. Firebase Analytics).
 *
 * Feature modules depend on this interface; the concrete implementation lives in :app.
 */
interface AnalyticsService {
    /** Log a prayer selected from the Pray Now screen. */
    fun logPrayNowItemSelection(
        prayerName: String,
        prayerId: String,
    )

    /** Log an application error with a description and where it occurred. */
    fun logError(
        description: String,
        location: String,
    )

    /** Log that the user shared the app. */
    fun logShareEvent()

    /**
     * Log a screen view event.
     *
     * [routePattern] is the raw navigation route pattern (may contain argument placeholders),
     * and [arguments] is the resolved argument bundle for that destination.
     */
    fun logScreenVisited(
        routePattern: String,
        arguments: Map<String, String?> = emptyMap(),
    )
}
