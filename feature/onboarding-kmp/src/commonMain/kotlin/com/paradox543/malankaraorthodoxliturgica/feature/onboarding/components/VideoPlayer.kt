package com.paradox543.malankaraorthodoxliturgica.feature.onboarding.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun VideoPlayer(
    resourceId: Int,
    modifier: Modifier = Modifier
)
