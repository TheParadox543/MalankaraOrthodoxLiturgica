package com.paradox543.malankaraorthodoxliturgica.domain.settings.model

enum class OnboardingStage(val value: Int) {
    WELCOME(0),
    SONG_WRAP(1),
    QR_NAVIGATION(2),
    SOUND_MODE(3),
    COMPLETE(4);

    companion object {
        fun fromInt(value: Int) = entries.find { it.value == value } ?: WELCOME
        const val MAX_STAGE = 4
    }
}
