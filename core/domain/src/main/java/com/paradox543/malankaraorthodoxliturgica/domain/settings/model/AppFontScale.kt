package com.paradox543.malankaraorthodoxliturgica.domain.settings.model

import kotlin.math.abs

/**
 * AppFontScale represents the set of supported font-size scale options used throughout
 * the application UI.
 *
 * Each entry contains:
 * - [scaleFactor]: the multiplier applied to the app's default font size.
 * - [displayName]: a human-friendly label shown in UI controls (for example, in a settings screen).
 *
 * Convenience functions are provided to move between scale steps and to convert a raw
 * float scale to the closest enum value.
 *
 * Example:
 * val current = AppFontScale.fromScale(userSettings.fontScale)
 * val larger = current.next()
 */
enum class AppFontScale(
    val scaleFactor: Float,
    val displayName: String,
) {
    /** Very small font size (approximately 70% of default). */
    VerySmall(0.7f, "Very Small"),

    /** Small font size (approximately 80% of default). */
    Small(0.8f, "Small"),

    /** Default/medium font size (100%). */
    Medium(1.0f, "Medium"),

    /** Large font size (approximately 120% of default). */
    Large(1.2f, "Large"),

    /** Very large font size (approximately 140% of default). */
    VeryLarge(1.4f, "Very Large"),
    ;

    /**
     * Move up one scale step, or stay at max
     */
    fun next(): AppFontScale = entries.getOrElse(ordinal + 1) { this }

    /**
     * Move down one scale step, or stay at min.
     */
    fun prev(): AppFontScale = entries.getOrElse(ordinal - 1) { this }

    companion object {
        /**
         * Return the closest AppFontScale entry for a given float scale.
         * If the provided scale doesn't match exactly, the nearest defined scale is returned.
         */
        fun fromScale(scale: Float): AppFontScale = entries.minByOrNull { abs(it.scaleFactor - scale) } ?: Medium
    }
}