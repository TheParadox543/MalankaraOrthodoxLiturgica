package com.paradox543.malankaraorthodoxliturgica.view

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.paradox543.malankaraorthodoxliturgica.R
import com.paradox543.malankaraorthodoxliturgica.data.model.PageNodeData
import com.paradox543.malankaraorthodoxliturgica.data.model.Screen
import com.paradox543.malankaraorthodoxliturgica.navigation.BottomNavBar
import com.paradox543.malankaraorthodoxliturgica.navigation.TopNavBar
import com.paradox543.malankaraorthodoxliturgica.viewmodel.NavViewModel
import com.paradox543.malankaraorthodoxliturgica.viewmodel.PrayerViewModel
import com.paradox543.malankaraorthodoxliturgica.viewmodel.SettingsViewModel

@Composable
fun PrayNowScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel,
    prayerViewModel: PrayerViewModel,
    navViewModel: NavViewModel
) {
    val translations by prayerViewModel.translations.collectAsState()
    val nodes = navViewModel.getAllPrayerNodes()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val title = translations["prayNow"]?: "Pray Now"
    Scaffold (
        topBar = { TopNavBar(title, navController) },
        bottomBar = { BottomNavBar(navController = navController) }
    ) { innerPadding ->
        Box {
            Image(
                painter = painterResource(R.drawable.praynow),
                "background Image",
                modifier = Modifier
                    .padding(innerPadding)
                    .requiredWidth(400.dp)
                    .fillMaxSize(),
                alignment = Alignment.TopStart,
                contentScale = ContentScale.Fit
            )
            if (screenWidth > 600.dp) {
                Row {
                    Spacer(Modifier.padding(horizontal = 160.dp))
                    if (nodes.isNotEmpty()) {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(240.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            items(nodes) { node ->
                                val routeParts = node.route.split("_")
                                val translatedParts = routeParts.joinToString(" ") { part ->
                                    translations[part] ?: part
                                }
                                PrayNowCard(
                                    node,
                                    navController,
                                    translatedParts,
                                    prayerViewModel
                                )
                            }
                        }
                    } else {
                        Column(
                            Modifier.padding(innerPadding),
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
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .padding(horizontal = 20.dp),
//                                .weight(0.6f),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            items(nodes) { node ->
                                val routeParts = node.route.split("_")
                                val translatedParts = routeParts.joinToString(" ") { part ->
                                    translations[part] ?: part
                                }
                                PrayNowCard(
                                    node,
                                    navController,
                                    translatedParts,
                                    prayerViewModel
                                )
                            }
                        }
                    } else {
                        Card(
                            Modifier
                                .padding(innerPadding)
                                .padding(horizontal = 20.dp)
                                .fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            ),
                        ) {
                            Text(
                                "No prayers found for this time.",
                                Modifier.fillMaxWidth().padding(8.dp)
                            )
                        }
                    }

                }
            }
        }
    }
}

@Composable
private fun PrayNowCard(
    node: PageNodeData,
    navController: NavController,
    translatedParts: String,
    prayerViewModel: PrayerViewModel
) {
    var errorState = remember { false }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                if (node.filename != null) {
                    prayerViewModel.logPrayNowItemSelection(translatedParts, node.route)
                    navController.navigate(Screen.Prayer.createRoute(node.route))
                } else {
                    Log.w("PrayNowScreen", "No file found")
                    errorState = true
                }
            },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            Modifier.requiredHeightIn(min=60.dp)
        ) {
            Text(
                translatedParts,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
            if (errorState) {
                Text("No file found", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}