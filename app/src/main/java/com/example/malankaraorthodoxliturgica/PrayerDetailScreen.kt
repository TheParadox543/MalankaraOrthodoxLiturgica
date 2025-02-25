package com.example.malankaraorthodoxliturgica

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.json.JSONObject

data class Prayer(
    val title_en: String,
    val title_ml: String,
    val content_en: String,
    val content_ml: String
)

fun loadPrayers(context: Context, category: String): List<Prayer> {
    val json = context.assets.open("prayers.json").bufferedReader().use { it.readText() }
    val jsonObject = JSONObject(json)
    val jsonArray = jsonObject.getJSONArray(category)

    val prayers = mutableListOf<Prayer>()
    for (i in 0 until jsonArray.length()) {
        val obj = jsonArray.getJSONObject(i)
        prayers.add(
            Prayer(
                title_en = obj.getString("title_en"),
                title_ml = obj.getString("title_ml"),
                content_en = obj.getString("content_en"),
                content_ml = obj.getString("content_ml")
            )
        )
    }
    return prayers
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerDetailScreen(navController: NavController, context: Context, category: String, language: String) {
    val prayers = remember { mutableStateOf(loadPrayers(context, category)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(category) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(prayers.value) { prayer ->
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (language == "ml") prayer.title_ml else prayer.title_en,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (language == "ml") prayer.content_ml else prayer.content_en,
                        fontSize = 16.sp
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
}
