package com.paradox543.malankaraorthodoxliturgica.domain.settings.model

enum class OnboardingStage(
    val value: Int,
) {
    WELCOME(0),
    SONG_WRAP(1),
    SOUND_MODE(2),
    COMPLETE(3),
    ;

    //    QR_NAVIGATION(99);

    companion object {
        fun fromInt(value: Int) = entries.find { it.value == value } ?: WELCOME

        const val MAX_STAGE = 3
    }
}
