package com.paradox543.malankaraorthodoxliturgica

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.paradox543.malankaraorthodoxliturgica.navigation.NavGraph
import com.paradox543.malankaraorthodoxliturgica.ui.theme.MalankaraOrthodoxLiturgicaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MalankaraOrthodoxLiturgicaTheme {
                NavGraph(Modifier.fillMaxSize())
            }
        }
    }
}