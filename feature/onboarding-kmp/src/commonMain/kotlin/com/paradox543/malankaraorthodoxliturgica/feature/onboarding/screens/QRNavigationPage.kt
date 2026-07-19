package com.paradox543.malankaraorthodoxliturgica.feature.onboarding.screens

fun qrNavigationPage() {}
// import androidx.compose.foundation.layout.Column
// import androidx.compose.foundation.layout.PaddingValues
// import androidx.compose.foundation.layout.Spacer
// import androidx.compose.foundation.layout.aspectRatio
// import androidx.compose.foundation.layout.fillMaxSize
// import androidx.compose.foundation.layout.fillMaxWidth
// import androidx.compose.foundation.layout.height
// import androidx.compose.foundation.layout.padding
// import androidx.compose.foundation.rememberScrollState
// import androidx.compose.foundation.verticalScroll
// import androidx.compose.runtime.Composable
// import androidx.compose.ui.Alignment
// import androidx.compose.ui.Modifier
// import androidx.compose.ui.unit.Dp
// import androidx.compose.ui.unit.dp
// import com.paradox543.malankaraorthodoxliturgica.feature.onboarding.components.OnboardingHeader
// import com.paradox543.malankaraorthodoxliturgica.feature.onboarding.components.OnboardingNavigationButtons
// import com.paradox543.malankaraorthodoxliturgica.feature.onboarding.components.VideoPlayer
// import com.paradox543.malankaraorthodoxliturgica.feature.onboarding.components.qrNavTutorialVideoRes
//
// @Composable
// fun QRNavigationPage(
//    contentPadding: PaddingValues,
//    columnPadding: Dp,
//    onNext: () -> Unit,
//    onBack: () -> Unit,
//    onSkip: () -> Unit,
// ) {
//    Column(
//        modifier =
//            Modifier
//                .fillMaxSize()
//                .padding(contentPadding)
//                .padding(columnPadding)
//                .verticalScroll(rememberScrollState()),
//        horizontalAlignment = Alignment.CenterHorizontally,
//    ) {
//        OnboardingHeader(
//            title = "QR Code Navigation",
//            description = "Easily find prayers and hymns by scanning QR codes in another device.",
//        )
//
//        Spacer(Modifier.height(32.dp))
//
//        VideoPlayer(
//            resourceId = qrNavTutorialVideoRes,
//            modifier =
//                Modifier
//                    .weight(1f)
//                    .fillMaxWidth(),
// //                    .aspectRatio(16f / 9f), // Adjust based on video aspect ratio
//        )
//
//        OnboardingNavigationButtons(
//            onNext = onNext,
//            onBack = onBack,
//            onSkip = onSkip,
//        )
//    }
// }
