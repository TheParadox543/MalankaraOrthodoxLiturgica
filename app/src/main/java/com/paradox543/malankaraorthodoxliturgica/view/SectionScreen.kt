package com.paradox543.malankaraorthodoxliturgica.view

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.paradox543.malankaraorthodoxliturgica.R
import com.paradox543.malankaraorthodoxliturgica.data.model.PageNode
import com.paradox543.malankaraorthodoxliturgica.navigation.BottomNavBar
import com.paradox543.malankaraorthodoxliturgica.navigation.TopNavBar
import com.paradox543.malankaraorthodoxliturgica.viewmodel.PrayerViewModel
import com.paradox543.malankaraorthodoxliturgica.viewmodel.SettingsViewModel

@Composable
fun SectionScreen(
    navController: NavController,
    prayerViewModel: PrayerViewModel,
    settingsViewModel: SettingsViewModel,
    node: PageNode
) {
    val translations by prayerViewModel.translations.collectAsState()
    val selectedFontSize by settingsViewModel.selectedFontScale.collectAsState()
    val nodes = node.children
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    var title = ""
    for (item in node.route.split("_")){
        title += translations[item] + " "
    }

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        // The review flow needs an Activity context.
        val activity = context as? Activity
        if (activity != null) {
            prayerViewModel.onSectionScreenOpened(activity)
        }
    }

    Scaffold (
        topBar = { TopNavBar(title, navController) },
        bottomBar = { BottomNavBar(navController = navController) }
    ){ innerPadding ->
        Box{
            if (screenWidth > 600.dp) {
                Row(
                    Modifier.padding(innerPadding)
                ) {
                    DisplayIconography("row")
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(240.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        items(nodes.size) { index ->
                            SectionCard(
                                nodes[index],
                                navController,
                                translations
                            )
                        }
                    }
                }
            }
            else {
                Column(
                    Modifier.padding(innerPadding)
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(240.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp)
                            .weight(0.6f),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        item {
                            DisplayIconography("column")
                        }
                        items(nodes.size) { index ->
                            SectionCard(
                                nodes[index],
                                navController,
                                translations
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DisplayIconography(orientation: String) {
    Image(
        painter = painterResource(R.drawable.transfigurationicon),
        contentDescription = "icon",
        modifier = if (orientation == "row") {
            Modifier.fillMaxHeight()
        } else {
            Modifier.fillMaxWidth()
        },
        alignment = Alignment.TopStart,
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun SectionCard(
    node: PageNode,
    navController: NavController,
    translations: Map<String, String>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable {
                if (node.children.isNotEmpty()) {
                    navController.navigate("section/${node.route}")
                } else if (node.filename != null) {
                    navController.navigate("prayerScreen/${node.route}")
                } else {
                    Log.w("SectionCard", "Invalid operation: Node has no children and no filename.")
                }
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        val text = node.route.split("_").last()
        Text(
            text = translations[text] ?: text,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(30.dp).fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}
