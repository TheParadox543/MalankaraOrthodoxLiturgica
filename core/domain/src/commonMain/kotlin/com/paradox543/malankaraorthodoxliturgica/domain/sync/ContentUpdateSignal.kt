package com.paradox543.malankaraorthodoxliturgica.domain.sync

import kotlinx.coroutines.flow.Flow

interface ContentUpdateSignal {
    /**
     * Flow that emits whenever a specific domain has been updated on disk.
     * Emits the domain name (e.g., "prayers", "calendar", "translations").
     */
    val onDomainUpdated: Flow<String>

    /**
     * Flow that emits whenever any content update has occurred.
     */
    val onAnyUpdate: Flow<Unit>
}
