package com.paradox543.malankaraorthodoxliturgica.core.platform

import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.SoundMode

class IOSSoundModeCapability : SoundModeCapability {
    override val isAvailable = false
    override val hasPermission = false

    override fun apply(mode: SoundMode) = Unit

    override fun restoreIfNeeded() = Unit
}