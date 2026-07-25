package com.paradox543.malankaraorthodoxliturgica.feature.onboarding.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingHeader(
    title: String,
    description: String,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineMedium,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(16.dp))
    Text(
        text = description,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
    )
}