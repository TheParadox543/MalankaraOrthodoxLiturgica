package com.paradox543.malankaraorthodoxliturgica.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.paradox543.malankaraorthodoxliturgica.model.PageNode
import com.paradox543.malankaraorthodoxliturgica.viewmodel.PrayerViewModel

@Composable
fun SectionScreen(
    navController: NavController,
    prayerViewModel: PrayerViewModel,
    nodes: List<PageNode>
) {
    val translations = prayerViewModel.loadTranslations()
    val selectedFontSize by prayerViewModel.selectedFontSize.collectAsState()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(nodes) { node ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable {
                        if (node.children.isNotEmpty()) {
                            navController.navigate("section/${node.route}")
                        } else if (node.filename.isNotEmpty()) {
                            prayerViewModel.setFilename(node.filename)
                            prayerViewModel.setTopBarKeys(listOf(node.route))
                            navController.navigate("prayerScreen")
                        }
                    },
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Text(
                    text = translations[node.route] ?: node.route,
                    fontSize = selectedFontSize,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
