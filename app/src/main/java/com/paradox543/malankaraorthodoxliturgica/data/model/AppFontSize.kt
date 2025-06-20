package com.paradox543.malankaraorthodoxliturgica.data.model

import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

enum class AppFontSize(val fontSize: TextUnit, val intValue: Int, val displayName: String) {
    VerySmall(8.sp, 8, "Very Small"),
    Small(12.sp, 12, "Small"),
    Medium(16.sp, 16, "Medium"),
    Large(20.sp, 20, "Large"),
    VeryLarge(24.sp, 24, "Very Large");

    // Helper to get the next size in the enum list, or stay at current if already at max
    fun next(): AppFontSize = entries.getOrElse(ordinal + 1) { this }

    // Helper to get the previous size in the enum list, or stay at current if already at min
    fun prev(): AppFontSize = entries.getOrElse(ordinal - 1) { this }

    companion object {
        fun fromInt(intValue: Int): AppFontSize {
            return entries.find { it.intValue == intValue } ?: Medium // Default to MEDIUM if not found
        }
    }
}