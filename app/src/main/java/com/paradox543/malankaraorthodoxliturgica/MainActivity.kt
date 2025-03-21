package com.paradox543.malankaraorthodoxliturgica

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.paradox543.malankaraorthodoxliturgica.model.DataStoreManager
import com.paradox543.malankaraorthodoxliturgica.model.PrayerRepository
import com.paradox543.malankaraorthodoxliturgica.ui.theme.MalankaraOrthodoxLiturgicaTheme
import com.paradox543.malankaraorthodoxliturgica.view.navigation.NavGraph
import com.paradox543.malankaraorthodoxliturgica.viewmodel.PrayerViewModel
import com.paradox543.malankaraorthodoxliturgica.viewmodel.PrayerViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create repository instance
        val repository = PrayerRepository(applicationContext)

        // Create datastore instance
        val dataStoreManager = DataStoreManager(applicationContext)

        // Create ViewModel using ViewModelProvider
        val prayerViewModel = ViewModelProvider(this, PrayerViewModelFactory(repository, dataStoreManager))[PrayerViewModel::class.java]
        enableEdgeToEdge()
        setContent {
            MalankaraOrthodoxLiturgicaTheme {
                Scaffold (modifier = Modifier.fillMaxSize()) {innerPadding ->
                    NavGraph(prayerViewModel, Modifier.padding(innerPadding))
                }
            }
        }
    }
}