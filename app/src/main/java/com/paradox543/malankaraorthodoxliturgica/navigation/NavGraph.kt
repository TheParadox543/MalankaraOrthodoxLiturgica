package com.paradox543.malankaraorthodoxliturgica.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.paradox543.malankaraorthodoxliturgica.R
import com.paradox543.malankaraorthodoxliturgica.view.BibleScreen
import com.paradox543.malankaraorthodoxliturgica.view.HomeScreen
import com.paradox543.malankaraorthodoxliturgica.view.PrayNowScreen
import com.paradox543.malankaraorthodoxliturgica.view.PrayerScreen
import com.paradox543.malankaraorthodoxliturgica.view.SectionScreen
import com.paradox543.malankaraorthodoxliturgica.view.SettingsScreen
import com.paradox543.malankaraorthodoxliturgica.viewmodel.NavViewModel
import com.paradox543.malankaraorthodoxliturgica.viewmodel.PrayerViewModel

@Composable
fun rememberScrollAwareVisibility(): Pair<MutableState<Boolean>, NestedScrollConnection> {
    val isVisible = remember { mutableStateOf(true) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y > 20) {
                    isVisible.value = true  // Scrolling UP → Show bars
                } else if (available.y < 0) {
                    isVisible.value = false // Scrolling DOWN → Hide bars
                }
                return Offset.Zero
            }
        }
    }
    return isVisible to nestedScrollConnection
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(modifier: Modifier = Modifier) {
    val prayerViewModel: PrayerViewModel = hiltViewModel()
    val navViewModel: NavViewModel = hiltViewModel()
    val navController = rememberNavController()
    NavHost(navController, startDestination = "home") {
        composable("home") {
            HomeScreen(navController, prayerViewModel, navViewModel)
        }
        composable("section/{route}") { backStackEntry ->
            val route = backStackEntry.arguments?.getString("route") ?: ""
            val node = navViewModel.findNode(navViewModel.rootNode, route)
            if (node != null) {
                SectionScreen(navController, prayerViewModel, navViewModel, node)
            }
        }
        composable("prayerScreen/{route}") { backStackEntry ->
            val route = backStackEntry.arguments?.getString("route") ?: ""
//            prayerViewModel.setTopBarKeys(route)
            PrayerScreen(navController, prayerViewModel, navViewModel)
        }
        composable("prayNow") {
            PrayNowScreen(navController, prayerViewModel, navViewModel)
        }
        composable("bible") {
            BibleScreen(navController, prayerViewModel, navViewModel)
        }
        composable("settings") {
            SettingsScreen(navController, prayerViewModel, navViewModel)
        }
    }
}
