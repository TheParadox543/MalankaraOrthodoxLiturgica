package com.paradox543.malankaraorthodoxliturgica.view

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.paradox543.malankaraorthodoxliturgica.BuildConfig
import com.paradox543.malankaraorthodoxliturgica.navigation.TopNavBar
import androidx.core.net.toUri
import com.paradox543.malankaraorthodoxliturgica.R

@Composable
fun AboutScreen(navController: NavController) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopNavBar(
                title = "About",
                navController = navController,
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App name & version
            Text(
                text = "Malankara Orthodox Liturgica",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Version ${BuildConfig.VERSION_NAME}",
                style = MaterialTheme.typography.bodyMedium
            )

            // Description
            Text(
                text = "A prayer companion app designed for the members of the Malankara Orthodox Syrian Church. Offline-first, multi-language (English, Malayalam, Manglish)."
            )

            HorizontalDivider()

            Text(
                "📜 Credits & Contributors",
                style = MaterialTheme.typography.titleLarge
            )
            
            Text(
                text = """
                    - Samuel Alex Koshy – Development, Implementation, UI Design, and Text Translations
                    - Shriganesh Keshrimal Purohit – Guidance, Structural Planning, and Development Insights
                    - Jerin M George – Assistance with Color Theme Fixes and Image Selection.
                    - Shaun John, Lisa Shibu George, Sabu John, Saira Susan Koshy, Sunitha Mathew, Nohan George & Anoop Alex Koshy – Additional Text Translations and Preparation
                    """.trimIndent(),
                style = MaterialTheme.typography.bodyMedium
            )


            HorizontalDivider()

            // Contact Info
            Text("Contact", style = MaterialTheme.typography.titleLarge)
            Text("Developer: Samuel Alex Koshy")
            TextButton(
                onClick = { /* launch email intent */ }
            ) {
                Text("Email: samuel.alex.koshy@gmail.com")
                Icon(
                    painter = painterResource(R.drawable.link),
                    contentDescription = "External Link Icon",
                    modifier = Modifier.size(20.dp),
                )
            }

            HorizontalDivider()

            // Links
            Text("More", style = MaterialTheme.typography.titleLarge)
            TextButton(
                onClick = { openUrl(context, "https://theparadox543.github.io/MalankaraOrthodoxLiturgica/terms-and-conditions.html") }
            ) {
                Text("Terms of Service")
                Icon(
                    painter = painterResource(R.drawable.link),
                    contentDescription = "External Link Icon",
                    modifier = Modifier.size(20.dp),
                )
            }
            TextButton (
                onClick = { openUrl(context, "https://theparadox543.github.io/MalankaraOrthodoxLiturgica/privacy-policy.html") }
            ) {
                Text("Privacy Policy")
                Icon(
                    painter = painterResource(R.drawable.link),
                    contentDescription = "External Link Icon",
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

private fun openUrl(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
    context.startActivity(intent)
}
