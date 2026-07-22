package com.paradox543.malankaraorthodoxliturgica.feature.onboarding.screens

import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.paradox543.malankaraorthodoxliturgica.core.ui.components.Song
import com.paradox543.malankaraorthodoxliturgica.feature.onboarding.components.OnboardingHeader
import com.paradox543.malankaraorthodoxliturgica.feature.onboarding.components.OnboardingNavigationButtons
import com.paradox543.malankaraorthodoxliturgica.feature.onboarding.viewmodel.OnboardingViewModel

@Composable
fun SongWrapPage(
    onboardingViewModel: OnboardingViewModel,
    contentPadding: PaddingValues,
    columnPadding: Dp,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onSkip: () -> Unit,
) {
    val songScrollState by onboardingViewModel.songScrollState.collectAsState()
    val sampleText = "♪ Praise the Lord with all your heart and with all your soul, for His mercy endures forever.\nHallelujah!"

    val wrappingEnabled = !songScrollState

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(columnPadding)
                .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OnboardingHeader(
            title = "Song Text Wrapping",
            description =
                """Choose how song lyrics are displayed.
                    |
                    |Wrapped text allows long lines to stay within the screen.
                    |
                    |Original formatting preserves the line as it is in printed books.
                """.trimMargin(),
        )

        Spacer(Modifier.height(12.dp))

        // Wrapped Preview
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { onboardingViewModel.setSongScrollState(false) })
                    .border(
                        width = if (wrappingEnabled) 2.dp else 0.dp,
                        color = if (wrappingEnabled) MaterialTheme.colorScheme.primary else androidx.compose.ui.graphics.Color.Transparent,
                        shape = RoundedCornerShape(12.dp),
                    ).padding(if (wrappingEnabled) 4.dp else 0.dp),
        ) {
            Text(
                "Wrapped: Songs fit within the screen.",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp),
            )
            Song(text = sampleText, isHorizontal = false)
        }

        Spacer(Modifier.height(24.dp))

        // Original Preview
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { onboardingViewModel.setSongScrollState(true) })
                    .border(
                        width = if (!wrappingEnabled) 2.dp else 0.dp,
                        color = if (!wrappingEnabled) MaterialTheme.colorScheme.primary else androidx.compose.ui.graphics.Color.Transparent,
                        shape = RoundedCornerShape(12.dp),
                    ).padding(if (!wrappingEnabled) 4.dp else 0.dp),
        ) {
            Text(
                "Original: Songs match printed books. Swipe sideways to see the remaining lyrics.",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp),
            )
            Song(text = sampleText, isHorizontal = true)
            Text(
                "Swipe right to see the remaining lyrics.",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.fillMaxWidth().padding(start = 4.dp, top = 4.dp),
                textAlign = TextAlign.End,
            )
        }

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

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Wrap song text",
                style = MaterialTheme.typography.bodyLarge,
            )
            Switch(
                checked = wrappingEnabled,
                onCheckedChange = { onboardingViewModel.setSongScrollState(!it) },
            )
        }

        Spacer(Modifier.weight(1f))
        OnboardingNavigationButtons(onNext, onBack, onSkip)
    }
}
