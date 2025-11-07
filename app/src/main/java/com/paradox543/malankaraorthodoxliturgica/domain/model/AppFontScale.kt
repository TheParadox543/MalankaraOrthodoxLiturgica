package com.paradox543.malankaraorthodoxliturgica.domain.model

import kotlin.math.abs

enum class AppFontScale(
    val scaleFactor: Float,
    val displayName: String,
) {
    VerySmall(0.7f, "Very Small"),
    Small(0.8f, "Small"),
    Medium(1.0f, "Medium"),
    Large(1.2f, "Large"),
    VeryLarge(1.4f, "Very Large"),
    ;

    // Move up one scale step, or stay at max
    fun next(): AppFontScale = entries.getOrElse(ordinal + 1) { this }

    // Move down one scale step, or stay at min
    fun prev(): AppFontScale = entries.getOrElse(ordinal - 1) { this }

    companion object {
        fun fromScale(scale: Float): AppFontScale = entries.minByOrNull { abs(it.scaleFactor - scale) } ?: Medium
    }
}