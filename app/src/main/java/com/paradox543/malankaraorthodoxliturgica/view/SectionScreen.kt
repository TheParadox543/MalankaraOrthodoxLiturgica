package com.paradox543.malankaraorthodoxliturgica.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.paradox543.malankaraorthodoxliturgica.R
import com.paradox543.malankaraorthodoxliturgica.model.PageNode
import com.paradox543.malankaraorthodoxliturgica.navigation.BottomNavBar
import com.paradox543.malankaraorthodoxliturgica.navigation.TopNavBar
import com.paradox543.malankaraorthodoxliturgica.viewmodel.NavViewModel
import com.paradox543.malankaraorthodoxliturgica.viewmodel.PrayerViewModel

@Composable
fun SectionScreen(
    navController: NavController,
    prayerViewModel: PrayerViewModel,
    navViewModel: NavViewModel,
    node: PageNode
) {
    val translations by prayerViewModel.translations.collectAsState()
    val selectedFontSize by prayerViewModel.selectedFontSize.collectAsState()
    val nodes = node.children
    prayerViewModel.setTopBarKeys(node.route)
    Scaffold (
        containerColor = Color.Transparent,
        topBar = {
            TopNavBar(
                navController = navController,
                prayerViewModel = prayerViewModel,
                navViewModel = navViewModel
            )
        },
        bottomBar = {BottomNavBar(navController = navController)}
    ){ innerPadding ->
        Box{
            Image(
                painter = painterResource(R.drawable.home),
                "icon",
                modifier = Modifier.fillMaxSize()
            )
            LazyVerticalGrid(
                columns = GridCells.Adaptive(240.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(top = 180.dp)
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                itemsIndexed(nodes) { index, node ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                if (node.children.isNotEmpty()) {
                                    navController.navigate("section/${node.route}")
                                } else if (node.filename.isNotEmpty()) {
                                    navViewModel.setSiblingNodes(nodes)
                                    navViewModel.setCurrentSiblingIndex(index)
                                    navController.navigate("prayerScreen/${node.route}")
                                }
                            },
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        val text = node.route.split("_").last()
                        Text(
                            text = translations[text] ?: text,
                            fontSize = selectedFontSize,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}
