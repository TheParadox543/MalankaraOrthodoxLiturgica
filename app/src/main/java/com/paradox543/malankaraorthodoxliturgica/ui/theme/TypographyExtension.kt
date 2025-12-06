package com.paradox543.malankaraorthodoxliturgica.ui.theme

import androidx.compose.material3.Typography

fun Typography.scaledTypography(scale: Float): Typography =
    Typography(
        displayLarge = displayLarge.copy(fontSize = displayLarge.fontSize * scale),
        displayMedium = displayMedium.copy(fontSize = displayMedium.fontSize * scale),
        displaySmall = displaySmall.copy(fontSize = displaySmall.fontSize * scale),
        headlineLarge = headlineLarge.copy(fontSize = headlineLarge.fontSize * scale),
        headlineMedium = headlineMedium.copy(fontSize = headlineMedium.fontSize * scale),
        headlineSmall = headlineSmall.copy(fontSize = headlineSmall.fontSize * scale),
        titleLarge = titleLarge.copy(fontSize = titleLarge.fontSize * scale),
        titleMedium = titleMedium.copy(fontSize = titleMedium.fontSize * scale),
        titleSmall = titleSmall.copy(fontSize = titleSmall.fontSize * scale),
        bodyLarge = bodyLarge.copy(fontSize = bodyLarge.fontSize * scale),
        bodyMedium = bodyMedium.copy(fontSize = bodyMedium.fontSize * scale),
        bodySmall = bodySmall.copy(fontSize = bodySmall.fontSize * scale),
        labelLarge = labelLarge.copy(fontSize = labelLarge.fontSize * scale),
        labelMedium = labelMedium.copy(fontSize = labelMedium.fontSize * scale),
        labelSmall = labelSmall.copy(fontSize = labelSmall.fontSize * scale),
    )
