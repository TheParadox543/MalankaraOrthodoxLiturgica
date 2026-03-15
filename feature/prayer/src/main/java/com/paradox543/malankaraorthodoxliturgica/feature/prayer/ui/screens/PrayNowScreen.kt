package com.paradox543.malankaraorthodoxliturgica.feature.prayer.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.paradox543.malankaraorthodoxliturgica.core.ui.ScaffoldUiState
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PageNode
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.R
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.ui.viewmodel.PrayerNavViewModel
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.ui.viewmodel.PrayerViewModel

@Composable
fun PrayNowScreen(
    onCardClick: (String) -> Unit,
    prayerViewModel: PrayerViewModel,
    prayerNavViewModel: PrayerNavViewModel,
    contentPadding: PaddingValues,
    onScaffoldStateChanged: (ScaffoldUiState) -> Unit,
) {
    val translations by prayerViewModel.translations.collectAsState()
    val nodes = prayerNavViewModel.getAllPrayerNodes()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val title = translations["prayNow"] ?: "Pray Now"

    LaunchedEffect(title) { onScaffoldStateChanged(ScaffoldUiState.Standard(title)) }

    Box {
        Image(
            painter = painterResource(R.drawable.praynow),
            "background Image",
            modifier =
                Modifier
                    .padding(contentPadding)
                    .requiredWidth(400.dp)
                    .fillMaxSize(),
            alignment = Alignment.TopStart,
            contentScale = ContentScale.Fit,
        )
        if (screenWidth > 600.dp) {
            Row {
                Spacer(Modifier.padding(horizontal = 160.dp))
                if (nodes.isNotEmpty()) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(240.dp),
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(contentPadding)
                                .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        items(nodes) { node ->
                            val routeParts = node.route.split("_")
                            val translatedParts =
                                routeParts.joinToString(" ") { part ->
                                    translations[part] ?: part
                                }
                            PrayNowCard(
                                node,
                                onCardClick,
                                translatedParts,
                                prayerViewModel,
                            )
                        }
                    }
                } else {
                    Column(
                        Modifier.padding(contentPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("No prayers found for this time.")
                    }
                }
            }
        } else {
            Column(
                Modifier.fillMaxSize().padding(top = 32.dp),
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                if (nodes.isNotEmpty()) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(240.dp),
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(contentPadding)
                                .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        items(nodes) { node ->
                            val routeParts = node.route.split("_")
                            val translatedParts =
                                routeParts.joinToString(" ") { part ->
                                    translations[part] ?: part
                                }
                            PrayNowCard(
                                node,
                                onCardClick,
                                translatedParts,
                                prayerViewModel,
                            )
                        }
                    }
                } else {
                    Card(
                        Modifier
                            .padding(contentPadding)
                            .padding(horizontal = 20.dp)
                            .fillMaxWidth(),
                        colors =
                            CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            ),
                    ) {
                        Text(
                            "No prayers found for this time.",
                            Modifier.fillMaxWidth().padding(8.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PrayNowCard(
    node: PageNode,
    onCardClick: (String) -> Unit,
    translatedParts: String,
    prayerViewModel: PrayerViewModel,
) {
    var errorState = remember { false }
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable {
                    if (node.filename != null) {
                        prayerViewModel.onPrayerSelected(translatedParts, node.route)
                        onCardClick(node.route)
                    } else {
                        Log.w("PrayNowScreen", "No file found")
                        errorState = true
                    }
                },
        shape = RoundedCornerShape(8.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            ),
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Column(
            Modifier.requiredHeightIn(min = 60.dp),
        ) {
            Text(
                translatedParts,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp),
            )
            if (errorState) {
                Text("No file found", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
