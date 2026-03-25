package com.paradox543.malankaraorthodoxliturgica.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import com.paradox543.malankaraorthodoxliturgica.designsystem.theme.englishTypography
import com.paradox543.malankaraorthodoxliturgica.designsystem.theme.malayalamTypography
import com.paradox543.malankaraorthodoxliturgica.designsystem.theme.scaledTypography
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage

@Composable
fun rememberAppTypography(
    language: AppLanguage,
    scaleFactor: Float,
): Typography {
    // Pick base typography based on language
    val baseTypography =
        when (language) {
            AppLanguage.MALAYALAM -> malayalamTypography()
            AppLanguage.ENGLISH -> englishTypography()
            AppLanguage.MANGLISH -> englishTypography()
            AppLanguage.INDIC -> englishTypography()
        }

    // Apply scale factor
    return baseTypography.scaledTypography(scaleFactor)
}