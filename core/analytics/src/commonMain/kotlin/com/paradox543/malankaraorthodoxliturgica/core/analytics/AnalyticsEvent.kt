package com.paradox543.malankaraorthodoxliturgica.core.analytics

sealed class AnalyticsEvent {
    abstract val name: String
    abstract val params: Map<String, Any?>?

    data class PrayNowItemSelected(
        val prayerName: String,
        val prayerId: String,
    ) : AnalyticsEvent() {
        override val name: String
            get() = "select_content"
        override val params: Map<String, Any?>
            get() =
                mapOf(
                    "item_id" to prayerId,
                    "item_name" to prayerName,
                    "content_type" to "prayNow",
                )
    }

    data class Error(
        val description: String,
        val location: String,
    ) : AnalyticsEvent() {
        override val name: String
            get() = "app_error"
        override val params: Map<String, Any?>
            get() =
                mapOf(
                    "error_description" to description,
                    "error_location" to location,
                )
    }

    data object ShareApp : AnalyticsEvent() {
        override val name: String
            get() = "share"
        override val params: Map<String, Any?>
            get() =
                mapOf(
                    "content_type" to "share_app",
                    "item_id" to "app_link",
                    "method" to "text/plain",
                )
    }

    data class LanguageSelected(
        val language: String,
    ) : AnalyticsEvent() {
        override val name: String
            get() = "language_selected"
        override val params: Map<String, Any?>
            get() = mapOf("language" to language)
    }

    data class QrNavigationSuccess(
        val destinationRoute: String,
    ) : AnalyticsEvent() {
        override val name: String
            get() = "qr_navigation_success"
        override val params: Map<String, Any?>
            get() =
                mapOf(
                    "destination_route" to destinationRoute,
                    "destination_type" to destinationRoute.substringBefore('/'),
                )
    }

    data class ScreenVisited(
        val routePattern: String,
        val arguments: Map<String, String?>,
    ) : AnalyticsEvent() {
        override val name: String
            get() = "screen_view"
        override val params: Map<String, Any?>
            get() {
                val resolvedScreenName =
                    """\{([^}]+)\}""".toRegex().replace(routePattern) { match ->
                        val argName = match.groupValues[1]
                        arguments[argName] ?: match.value
                    }

                val screenClass =
                    when {
                        routePattern.startsWith("section/") -> "SectionScreen"
                        routePattern.startsWith("prayer/") -> "PrayerScreen"
                        routePattern.startsWith("song/") -> "SongScreen"
                        routePattern == "calendar" -> "CalendarScreen"
                        routePattern == "qrScanner" -> "QrScannerScreen"
                        routePattern.startsWith("bible") -> "BibleScreen"
                        else -> "StaticScreen"
                    }
                return mapOf(
                    "screen_name" to resolvedScreenName,
                    "screen_class" to screenClass,
                )
            }
    }

    data object TutorialStarted : AnalyticsEvent() {
        override val name: String
            get() = "tutorial_begin"
        override val params: Map<String, Any?>?
            get() = null
    }

    data object TutorialCompleted : AnalyticsEvent() {
        override val name: String
            get() = "tutorial_complete"
        override val params: Map<String, Any?>?
            get() = null
    }
}