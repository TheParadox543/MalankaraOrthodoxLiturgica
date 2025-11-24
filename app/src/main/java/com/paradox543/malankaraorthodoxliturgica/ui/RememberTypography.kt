package com.paradox543.malankaraorthodoxliturgica.ui

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import com.paradox543.malankaraorthodoxliturgica.shared.domain.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.ui.theme.EnglishTypography
import com.paradox543.malankaraorthodoxliturgica.ui.theme.MalayalamTypography

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