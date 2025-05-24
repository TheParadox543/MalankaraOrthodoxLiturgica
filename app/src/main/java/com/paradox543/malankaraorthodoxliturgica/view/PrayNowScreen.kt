package com.paradox543.malankaraorthodoxliturgica.view

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
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
    Scaffold (
        topBar = {
            TopNavBar(
                navController = navController,
                prayerViewModel = prayerViewModel,
                navViewModel = navViewModel
            )
        },
        bottomBar = { BottomNavBar(navController = navController) }
    ) { innerPadding ->
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
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                if (node.filename.isNotEmpty()) {
                                    val parentRoute = navViewModel.getParentRoute(node.route)
                                    val parentNode = navViewModel.findNode(
                                        navViewModel.rootNode,
                                        parentRoute ?: ""
                                    )
                                    val siblingIndex =
                                        navViewModel.getIndexOfSibling(node.route, parentRoute)
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
            }
        } else {
            Column (Modifier.padding(innerPadding)){
                Text("No prayers found for this time.")
            }
        }
    }
}