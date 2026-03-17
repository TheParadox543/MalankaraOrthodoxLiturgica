package com.paradox543.malankaraorthodoxliturgica.core.platform

/**
 * Abstraction over the system share sheet and associated analytics.
 *
 * The concrete implementation lives in :app.
 */
interface ShareService {
    /**
     * Launches an Android share intent to share the app's Play Store link.
     *
     * @param shareSubject   Optional subject line (for email clients, etc)
     * @param shareMessage  An optional custom message prepended to the Play Store link.
     */
    fun shareAppLink(
        shareSubject: String = "",
        shareMessage: String = "",
    )
}
