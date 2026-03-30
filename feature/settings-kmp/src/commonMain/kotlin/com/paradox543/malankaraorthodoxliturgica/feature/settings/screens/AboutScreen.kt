package com.paradox543.malankaraorthodoxliturgica.feature.settings.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.paradox543.malankaraorthodoxliturgica.core.ui.scaffold.ScaffoldUiState
import com.paradox543.malankaraorthodoxliturgica.feature.settings.Res
import com.paradox543.malankaraorthodoxliturgica.feature.settings.link
import org.jetbrains.compose.resources.painterResource

@Composable
fun AboutScreen(
    contentPadding: PaddingValues = PaddingValues(),
    appVersion: String,
    onDeveloperContact: () -> Unit,
    onExternalLinkClick: (String) -> Unit,
    onScaffoldStateChanged: (ScaffoldUiState) -> Unit = {},
) {
    LaunchedEffect(Unit) {
        onScaffoldStateChanged(
            ScaffoldUiState.Standard("About", showBottomBar = false, showFab = false),
        )
    }

    Column(
        modifier =
            Modifier
                .padding(contentPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // App name & version
        Text(
            text = "Malankara Orthodox Liturgica",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "Version $appVersion",
            style = MaterialTheme.typography.bodyMedium,
        )

        // Description
        Text(
            text =
                """
                    |A prayer companion app designed for the members of the Malankara Orthodox 
                    |Syrian Church. Offline-first, multi-language (English, Malayalam, Manglish).
                """.trimMargin(),
        )

        HorizontalDivider()

        Text(
            "📜 Credits & Contributors",
            style = MaterialTheme.typography.titleLarge,
        )

        Text(
            text =
                """
                - Samuel Alex Koshy – Development, Implementation, UI Design, and Text Translations
                - Shriganesh Keshrimal Purohit – Guidance, Structural Planning, and Development Insights.
                - Jerin M George – Assistance with Color Theme Fixes and Image Selection.
                - Shaun John, Lisa Shibu George, Sabu John, Saira Susan Koshy, Sunitha Mathew, Nohan George & Anoop Alex Koshy – Additional Text Translations and Preparation.
                - Prasad Joseph Cheeran - Audio files for Ekkara Songs.
                """.trimIndent(),
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        // Contact Info
        Text("Contact", style = MaterialTheme.typography.titleLarge)
        Text("Developer: Samuel Alex Koshy")
        TextButton(
            onClick = { onDeveloperContact() },
        ) {
            Text("Email: samuel.alex.koshy@gmail.com")
            Icon(
                painter = painterResource(Res.drawable.link),
                contentDescription = "External Link Icon",
                modifier = Modifier.size(20.dp),
            )
        }

        HorizontalDivider()

        // Links
        Text("More", style = MaterialTheme.typography.titleLarge)
        TextButton(
            onClick = { onExternalLinkClick("https://theparadox543.github.io/MalankaraOrthodoxLiturgica/terms-and-conditions.html") },
        ) {
            Text("Terms of Service")
            Icon(
                painter = painterResource(Res.drawable.link),
                contentDescription = "External Link Icon",
                modifier = Modifier.size(20.dp),
            )
        }
        TextButton(
            onClick = { onExternalLinkClick("https://theparadox543.github.io/MalankaraOrthodoxLiturgica/privacy-policy.html") },
        ) {
            Text("Privacy Policy")
            Icon(
                painter = painterResource(Res.drawable.link),
                contentDescription = "External Link Icon",
                modifier = Modifier.size(20.dp),
            )
        }
    }
}
