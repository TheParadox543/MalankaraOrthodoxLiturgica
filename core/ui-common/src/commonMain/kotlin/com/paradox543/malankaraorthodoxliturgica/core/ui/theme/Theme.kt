package com.paradox543.malankaraorthodoxliturgica.core.ui.theme

import androidx.compose.runtime.Composable
import com.paradox543.malankaraorthodoxliturgica.designsystem.theme.AppTheme
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppFontScale
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage

@Composable
fun MalankaraOrthodoxLiturgicaTheme(
    language: AppLanguage = AppLanguage.MALAYALAM,
    textScale: AppFontScale = AppFontScale.Medium,
    content: @Composable () -> Unit,
) {
    val typography = rememberAppTypography(language, textScale.scaleFactor)

    AppTheme(
        typography = typography,
        content = content,
    )
}