package com.paradox543.malankaraorthodoxliturgica.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage

@Composable
fun rememberAppTypography(
    language: AppLanguage,
    scaleFactor: Float,
): Typography {
    // Pick base typography based on language
    val baseTypography =
        when (language) {
            AppLanguage.MALAYALAM -> MalayalamTypography
            AppLanguage.ENGLISH -> EnglishTypography
            AppLanguage.MANGLISH -> EnglishTypography
            AppLanguage.INDIC -> EnglishTypography
        }

    // Apply scale factor
    return baseTypography.scaledTypography(scaleFactor)
}