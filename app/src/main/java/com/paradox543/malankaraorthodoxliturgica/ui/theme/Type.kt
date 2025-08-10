package com.paradox543.malankaraorthodoxliturgica.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.paradox543.malankaraorthodoxliturgica.R

// Set of Material typography styles to start with

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val ArimaMalayalam = FontFamily(
    Font(
        googleFont = GoogleFont("Arima"),
        fontProvider = provider
    )
)

val NotoSerifMalayalam = FontFamily(
    Font(
        googleFont = GoogleFont("Noto Serif Malayalam"),
        fontProvider = provider
    )
)

val NotoSansMalayalam = FontFamily(
    Font(
        googleFont = GoogleFont("Noto Sans Malayalam"),
        fontProvider = provider
    )
)

val CinzelDecorative = FontFamily(
    Font(
        googleFont = GoogleFont("Cinzel Decorative"),
        fontProvider = provider
    )
)

val CormorantGaramond = FontFamily(
    Font(
        googleFont = GoogleFont("Cormorant Garamond"),
        fontProvider = provider
    )
)

val NotoSans = FontFamily(
    Font(
        googleFont = GoogleFont("Noto Sans"),
        fontProvider = provider
    )
)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = CinzelDecorative,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = CormorantGaramond,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    )
    /* Other default text styles to override
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)