package com.paradox543.malankaraorthodoxliturgica.core.platform

import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.SoundMode

interface SoundModeCapability {
    val isAvailable: Boolean
    val hasPermission: Boolean

    fun apply(mode: SoundMode)

    fun restoreIfNeeded()
}