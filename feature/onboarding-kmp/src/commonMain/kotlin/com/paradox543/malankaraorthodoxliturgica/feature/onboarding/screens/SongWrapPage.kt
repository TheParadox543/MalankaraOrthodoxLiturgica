package com.paradox543.malankaraorthodoxliturgica.feature.onboarding.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.paradox543.malankaraorthodoxliturgica.core.ui.components.Song
import com.paradox543.malankaraorthodoxliturgica.feature.onboarding.components.OnboardingHeader
import com.paradox543.malankaraorthodoxliturgica.feature.onboarding.components.OnboardingNavigationButtons
import com.paradox543.malankaraorthodoxliturgica.feature.onboarding.viewmodel.OnboardingViewModel

@Composable
fun SongWrapPage(
    onboardingViewModel: OnboardingViewModel,
    contentPadding: PaddingValues,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onSkip: () -> Unit,
) {
    val songScrollState by onboardingViewModel.songScrollState.collectAsState()
    val sampleText = "♪ Praise the Lord with all your heart and with all your soul, for His mercy endures forever.\nAlleluia!"

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OnboardingHeader(
            title = "Song Text Wrapping",
            description =
                """Choose how song lyrics are displayed.
                    |Wrapped text allows long lines to
                    | continue onto the next line when needed.
                    |Original formatting preserves
                    | the manually inserted line breaks from the hymn.
                """.trimMargin(),
        )

        Spacer(Modifier.height(12.dp))

        Text(
            "Preview:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start),
        )
        Spacer(Modifier.height(8.dp))

        Text(
            "Wrapped",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.align(Alignment.Start),
        )
        Song(text = sampleText, isHorizontal = false)

        Spacer(Modifier.height(16.dp))

        Text(
            "Original",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.align(Alignment.Start),
        )
        Song(text = sampleText, isHorizontal = true)

        Spacer(Modifier.height(24.dp))

        Text(
            text =
                """On wider devices such as tablets or large phones, this setting may produce
                    | little or no visible difference because there is sufficient screen width.
                    |  It is most useful on smaller phones.
                """.trimMargin(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(24.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable { onboardingViewModel.setSongScrollState(false) },
            ) {
                RadioButton(
                    selected = !songScrollState,
                    onClick = { onboardingViewModel.setSongScrollState(false) },
                )
                Text("Wrap song text")
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable { onboardingViewModel.setSongScrollState(true) },
            ) {
                RadioButton(
                    selected = songScrollState,
                    onClick = { onboardingViewModel.setSongScrollState(true) },
                )
                Text("Preserve original line formatting")
            }
        }

        Spacer(Modifier.weight(1f))
        OnboardingNavigationButtons(onNext, onBack, onSkip)
    }
}