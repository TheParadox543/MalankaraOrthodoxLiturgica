package com.paradox543.malankaraorthodoxliturgica.core.analytics

/**
 * Abstraction over the app's analytics backend (e.g. Firebase Analytics).
 *
 * Feature modules depend on this interface; the concrete implementation lives in :app.
 */
interface AnalyticsService {
    /** Log events that happen within the app.
     *
     * Use [AnalyticsEvent] to differentiate different events and pass required parameters.
     * */
    fun logEvent(event: AnalyticsEvent)
}