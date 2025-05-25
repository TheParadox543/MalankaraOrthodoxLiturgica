package com.paradox543.malankaraorthodoxliturgica.view

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.paradox543.malankaraorthodoxliturgica.R
import com.paradox543.malankaraorthodoxliturgica.model.PageNode
import com.paradox543.malankaraorthodoxliturgica.navigation.BottomNavBar
import com.paradox543.malankaraorthodoxliturgica.navigation.TopNavBar
import com.paradox543.malankaraorthodoxliturgica.viewmodel.NavViewModel
import com.paradox543.malankaraorthodoxliturgica.viewmodel.PrayerViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PrayNowScreen(
    navController: NavController,
    prayerViewModel: PrayerViewModel,
    navViewModel: NavViewModel
) {
    val translations by prayerViewModel.translations.collectAsState()
    val selectedFontSize by prayerViewModel.selectedFontSize.collectAsState()
    prayerViewModel.setTopBarKeys("malankara")
    val nodes = navViewModel.getAllPrayerNodes()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    Scaffold (
        topBar = {
            TopNavBar(
                navController = navController,
                prayerViewModel = prayerViewModel
            )
        },
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
                contentScale = ContentScale.Crop
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
                                PrayNowCard(
                                    node,
                                    navViewModel,
                                    navController,
                                    translations,
                                    selectedFontSize
                                )
                            }
                        }
                    } else {
                        Column(Modifier.padding(innerPadding)) {
                            Text("No prayers found for this time.")
                        }
                    }
                }
            } else {
                Column {
                    Spacer(Modifier.weight(0.4f))
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(240.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(horizontal = 20.dp)
                            .weight(0.6f),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        items(nodes) { node ->
                            PrayNowCard(
                                node,
                                navViewModel,
                                navController,
                                translations,
                                selectedFontSize
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
    node: PageNode,
    navViewModel: NavViewModel,
    navController: NavController,
    translations: Map<String, String>,
    selectedFontSize: TextUnit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                if (node.filename.isNotEmpty()) {
                    val parentRoute =
                        navViewModel.getParentRoute(node.route)
                    val parentNode = navViewModel.findNode(
                        navViewModel.rootNode,
                        parentRoute ?: ""
                    )
                    val siblingIndex =
                        navViewModel.getIndexOfSibling(
                            node.route,
                            parentRoute
                        )
                    if (parentNode != null && siblingIndex != null) {
                        navViewModel.setSiblingNodes(parentNode.children)
                        navViewModel.setCurrentSiblingIndex(siblingIndex)
                        navController.navigate("prayerScreen/${node.route}")
                    }
                } else {
                    Log.d("PrayNowScreen", "No file found")
                }
            },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        val routeParts = node.route.split("_")
        val translatedParts = routeParts.map { part ->
            translations[part] ?: part
        }
        Text(
            translatedParts.joinToString(" "),
            fontSize = selectedFontSize,
            modifier = Modifier.padding(16.dp)
        )
    }
}