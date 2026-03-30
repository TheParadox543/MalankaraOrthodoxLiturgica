package com.paradox543.malankaraorthodoxliturgica.core.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.materialicons.MaterialIcons
import com.composables.icons.materialicons.rounded.Arrow_back
import com.composables.icons.materialicons.rounded.Settings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavBar(
    title: String = "malankara",
    showBack: Boolean,
    showSettings: Boolean,
    onBack: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    TopAppBar(
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.StartEllipsis,
                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 24.sp),
                    textAlign = TextAlign.Center,
                )
            }
        },
        navigationIcon = {
            if (showBack) {
                IconButton(onClick = onBack) {
                    Icon(
                        MaterialIcons.Rounded.Arrow_back,
                        "Previous Page",
                    )
                }
            } else {
                Spacer(modifier = Modifier.padding(24.dp))
            }
        },
        actions = {
            if (showSettings) {
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        MaterialIcons.Rounded.Settings,
                        "Settings",
                    )
                }
            } else {
                Spacer(modifier = Modifier.padding(24.dp))
            }
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
            ),
    )
}