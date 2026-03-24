package com.paradox543.malankaraorthodoxliturgica.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.paradox543.malankaraorthodoxliturgica.designsystem.theme.Background
import com.paradox543.malankaraorthodoxliturgica.designsystem.theme.BackgroundDark
import com.paradox543.malankaraorthodoxliturgica.designsystem.theme.OnBackground
import com.paradox543.malankaraorthodoxliturgica.designsystem.theme.OnBackgroundDark
import com.paradox543.malankaraorthodoxliturgica.designsystem.theme.OnPrimary
import com.paradox543.malankaraorthodoxliturgica.designsystem.theme.OnPrimaryContainer
import com.paradox543.malankaraorthodoxliturgica.designsystem.theme.OnPrimaryContainerDark
import com.paradox543.malankaraorthodoxliturgica.designsystem.theme.OnPrimaryDark
import com.paradox543.malankaraorthodoxliturgica.designsystem.theme.OnSecondary
import com.paradox543.malankaraorthodoxliturgica.designsystem.theme.OnSecondaryContainer
import com.paradox543.malankaraorthodoxliturgica.designsystem.theme.OnSecondaryContainerDark
import com.paradox543.malankaraorthodoxliturgica.designsystem.theme.OnSecondaryDark
import com.paradox543.malankaraorthodoxliturgica.designsystem.theme.Primary
import com.paradox543.malankaraorthodoxliturgica.designsystem.theme.PrimaryContainer
import com.paradox543.malankaraorthodoxliturgica.designsystem.theme.PrimaryContainerDark
import com.paradox543.malankaraorthodoxliturgica.designsystem.theme.PrimaryDark
import com.paradox543.malankaraorthodoxliturgica.designsystem.theme.Secondary
import com.paradox543.malankaraorthodoxliturgica.designsystem.theme.SecondaryContainer
import com.paradox543.malankaraorthodoxliturgica.designsystem.theme.SecondaryContainerDark
import com.paradox543.malankaraorthodoxliturgica.designsystem.theme.SecondaryDark
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppFontScale
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage

private val DarkColorScheme =
    darkColorScheme(
        primary = PrimaryDark,
        onPrimary = OnPrimaryDark,
        primaryContainer = PrimaryContainerDark,
        onPrimaryContainer = OnPrimaryContainerDark,
        secondary = SecondaryDark,
        onSecondary = OnSecondaryDark,
        secondaryContainer = SecondaryContainerDark,
        onSecondaryContainer = OnSecondaryContainerDark,
        background = BackgroundDark,
        onBackground = OnBackgroundDark,
    )

private val LightColorScheme =
    lightColorScheme(
        primary = Primary,
        onPrimary = OnPrimary,
        primaryContainer = PrimaryContainer,
        onPrimaryContainer = OnPrimaryContainer,
        secondary = Secondary,
        onSecondary = OnSecondary,
        secondaryContainer = SecondaryContainer,
        onSecondaryContainer = OnSecondaryContainer,
        background = Background,
        onBackground = OnBackground,
    /* Other default colors to override
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F),
     */
    )

@Composable
fun MalankaraOrthodoxLiturgicaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    language: AppLanguage = AppLanguage.MALAYALAM,
    textScale: AppFontScale = AppFontScale.Medium,
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }
    val typography = rememberAppTypography(language, textScale.scaleFactor)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content,
    )
}