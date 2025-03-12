package com.example.malankaraorthodoxliturgica

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.malankaraorthodoxliturgica.model.PrayerRepository
import com.example.malankaraorthodoxliturgica.view.navigation.NavGraph
import com.example.malankaraorthodoxliturgica.ui.theme.MalankaraOrthodoxLiturgicaTheme
import com.example.malankaraorthodoxliturgica.view.setAppLocale
import com.example.malankaraorthodoxliturgica.viewmodel.PrayerViewModel
import com.example.malankaraorthodoxliturgica.viewmodel.PrayerViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val savedLanguage = sharedPreferences.getString("language", "en") ?: "en"
        setAppLocale(this, savedLanguage) // Apply locale before UI inflation

        super.onCreate(savedInstanceState)

        val prayerRepository = PrayerRepository()
        val prayerViewModelFactory = PrayerViewModelFactory(prayerRepository)
        val prayerViewModel = ViewModelProvider(this, prayerViewModelFactory)[PrayerViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            MalankaraOrthodoxLiturgicaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {innerPadding ->
                    NavGraph(prayerViewModel, Modifier.padding(innerPadding))
                }
            }
        }
    }
}