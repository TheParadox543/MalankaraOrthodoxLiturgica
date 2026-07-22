package com.paradox543.malankaraorthodoxliturgica.feature.onboarding.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingNavigationButtons(
    onNext: () -> Unit,
    onBack: () -> Unit,
    onSkip: () -> Unit,
    skipBack: Boolean = false,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (!skipBack) {
            TextButton(onClick = onBack) {
                Text("Back")
            }
        } else {
            Spacer(Modifier.weight(1f))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onSkip) {
                Text("Skip", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.width(8.dp))
            Button(onClick = onNext) {
                Text("Next")
            }
        }
    }
}