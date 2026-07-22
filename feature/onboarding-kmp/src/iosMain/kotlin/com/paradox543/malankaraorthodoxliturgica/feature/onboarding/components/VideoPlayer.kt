package com.paradox543.malankaraorthodoxliturgica.feature.onboarding.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
actual fun VideoPlayer(
    resourceId: Int,
    modifier: Modifier
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text("Video Player Placeholder (iOS)")
    }
}
