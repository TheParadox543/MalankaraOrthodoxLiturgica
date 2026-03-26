package com.paradox543.malankaraorthodoxliturgica.core.platform

import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.SoundMode

class AndroidSoundModeCapability(
    private val manager: SoundModeManager,
) : SoundModeCapability {
    override val isAvailable: Boolean = true

    override val hasPermission: Boolean
        get() = manager.checkDndPermission()

    override fun apply(mode: SoundMode) {
        manager.apply(mode)
    }

    override fun restoreIfNeeded() {
        manager.restoreIfNeeded()
    }
}