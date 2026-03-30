package com.paradox543.malankaraorthodoxliturgica.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.paradox543.malankaraorthodoxliturgica.designsystem.CinzelDecorative_Regular
import com.paradox543.malankaraorthodoxliturgica.designsystem.CormorantGaramond_VariableFont_wght
import com.paradox543.malankaraorthodoxliturgica.designsystem.NotoSerifMalayalam_VariableFont_wght
import com.paradox543.malankaraorthodoxliturgica.designsystem.Res
import com.paradox543.malankaraorthodoxliturgica.designsystem.arima_variablefont_wght
import com.paradox543.malankaraorthodoxliturgica.designsystem.notoSansMalayalam_VariableFont
import org.jetbrains.compose.resources.Font

@Composable
fun englishTypography(): Typography {
    val cinzelDecorative =
        FontFamily(
            Font(Res.font.CinzelDecorative_Regular),
        )

    val cormorantGaramond =
        FontFamily(
            Font(Res.font.CormorantGaramond_VariableFont_wght),
        )
    return Typography(
        displayLarge =
            TextStyle(
                fontFamily = cinzelDecorative,
                fontWeight = FontWeight.Bold,
                fontSize = 57.sp,
                lineHeight = 64.sp,
                letterSpacing = (-0.25).sp,
            ),
        displayMedium =
            TextStyle(
                fontFamily = cinzelDecorative,
                fontWeight = FontWeight.Bold,
                fontSize = 45.sp,
                lineHeight = 52.sp,
            ),
        displaySmall =
            TextStyle(
                fontFamily = cinzelDecorative,
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp,
                lineHeight = 44.sp,
            ),
        headlineLarge =
            TextStyle(
                fontFamily = cinzelDecorative,
                fontWeight = FontWeight.SemiBold,
                fontSize = 32.sp,
                lineHeight = 40.sp,
            ),
        headlineMedium =
            TextStyle(
                fontFamily = cinzelDecorative,
                fontWeight = FontWeight.SemiBold,
                fontSize = 28.sp,
                lineHeight = 36.sp,
            ),
        headlineSmall =
            TextStyle(
                fontFamily = cinzelDecorative,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                lineHeight = 32.sp,
            ),
        titleLarge =
            TextStyle(
                fontFamily = cormorantGaramond,
                fontWeight = FontWeight.Medium,
                fontSize = 22.sp,
                lineHeight = 28.sp,
            ),
        titleMedium =
            TextStyle(
                fontFamily = cormorantGaramond,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.15.sp,
            ),
        titleSmall =
            TextStyle(
                fontFamily = cormorantGaramond,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.1.sp,
            ),
        bodyLarge =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.5.sp,
            ),
        bodyMedium =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.25.sp,
            ),
        bodySmall =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.4.sp,
            ),
        labelLarge =
            TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.1.sp,
            ),
        labelMedium =
            TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.5.sp,
            ),
        labelSmall =
            TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.5.sp,
            ),
    )
}

@Composable
fun malayalamTypography(): Typography {
    val arimaMalayalam = FontFamily(Font(Res.font.arima_variablefont_wght))
    val notoSerifMalayalam =
        FontFamily(Font(Res.font.NotoSerifMalayalam_VariableFont_wght))

    val notoSansMalayalam =
        FontFamily(Font(Res.font.notoSansMalayalam_VariableFont))
    return Typography(
        displayLarge =
            TextStyle(
                fontFamily = arimaMalayalam,
                fontWeight = FontWeight.Bold,
                fontSize = 57.sp,
                lineHeight = 64.sp,
                letterSpacing = (-0.25).sp,
            ),
        displayMedium =
            TextStyle(
                fontFamily = arimaMalayalam,
                fontWeight = FontWeight.Bold,
                fontSize = 45.sp,
                lineHeight = 52.sp,
            ),
        displaySmall =
            TextStyle(
                fontFamily = arimaMalayalam,
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp,
                lineHeight = 44.sp,
            ),
        headlineLarge =
            TextStyle(
                fontFamily = arimaMalayalam,
                fontWeight = FontWeight.SemiBold,
                fontSize = 32.sp,
                lineHeight = 40.sp,
            ),
        headlineMedium =
            TextStyle(
                fontFamily = arimaMalayalam,
                fontWeight = FontWeight.SemiBold,
                fontSize = 28.sp,
                lineHeight = 36.sp,
            ),
        headlineSmall =
            TextStyle(
                fontFamily = arimaMalayalam,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                lineHeight = 32.sp,
            ),
        titleLarge =
            TextStyle(
                fontFamily = notoSerifMalayalam,
                fontWeight = FontWeight.Medium,
                fontSize = 22.sp,
                lineHeight = 28.sp,
            ),
        titleMedium =
            TextStyle(
                fontFamily = notoSerifMalayalam,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.15.sp,
            ),
        titleSmall =
            TextStyle(
                fontFamily = notoSerifMalayalam,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.1.sp,
            ),
        bodyLarge =
            TextStyle(
                fontFamily = notoSansMalayalam,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.5.sp,
            ),
        bodyMedium =
            TextStyle(
                fontFamily = notoSansMalayalam,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.25.sp,
            ),
        bodySmall =
            TextStyle(
                fontFamily = notoSansMalayalam,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.4.sp,
            ),
        labelLarge =
            TextStyle(
                fontFamily = notoSansMalayalam,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.1.sp,
            ),
        labelMedium =
            TextStyle(
                fontFamily = notoSansMalayalam,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.5.sp,
            ),
        labelSmall =
            TextStyle(
                fontFamily = notoSansMalayalam,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.5.sp,
            ),
    )
}