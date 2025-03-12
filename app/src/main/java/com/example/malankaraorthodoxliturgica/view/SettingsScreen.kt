package com.example.malankaraorthodoxliturgica.view

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.malankaraorthodoxliturgica.model.PrayerRepository
import com.example.malankaraorthodoxliturgica.viewmodel.PrayerViewModel

import java.util.Locale

@Composable
fun SettingsScreen(navController: NavController, prayerViewModel: PrayerViewModel) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    val savedLanguage = sharedPreferences.getString("language", "en") ?: "en"
    var selectedLanguage by remember { mutableStateOf(savedLanguage) }

    val languages = listOf("English" to "en", "Malayalam" to "ml", "Manglish" to "mn")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Select Language", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        DropdownMenu(
            selectedLanguage,
            languages,
            onLanguageSelected = { newLang ->
                selectedLanguage = newLang
                sharedPreferences.edit().putString("language", newLang).apply()
                setAppLocale(context, newLang)
                restartActivity(context) // Restart to apply changes
            }

        )
    }
}

@Composable
fun DropdownMenu(
    selected: String,
    options: List<Pair<String, String>>,
    onLanguageSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Button(onClick = { expanded = true }) {
            Text(options.firstOrNull { it.second == selected }?.first ?: "Select Language")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { (name, code) ->
                DropdownMenuItem(text = { Text(name) }, onClick = {
                    onLanguageSelected(code)
                    expanded = false
                })
            }
        }
    }
}

fun setAppLocale(context: Context, languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)

    val config = Configuration()
    config.setLocale(locale)

    val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    sharedPreferences.edit().putString("language", languageCode).apply()
}

fun restartActivity(context: Context) {
    val intent = (context as Activity).intent
    context.finish()
    context.startActivity(intent)
}

//@Preview
//@Composable
//fun Preview(){
//    SettingsScreen(
//        navController = NavController(LocalContext.current),
//        prayerViewModel = PrayerViewModel(PrayerRepository())
//    )
//}