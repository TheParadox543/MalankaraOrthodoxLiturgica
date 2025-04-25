package com.paradox543.malankaraorthodoxliturgica.navigation

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.paradox543.malankaraorthodoxliturgica.view.AboutAppScreen
import com.paradox543.malankaraorthodoxliturgica.view.DummyScreen
import com.paradox543.malankaraorthodoxliturgica.view.HomeScreen
import com.paradox543.malankaraorthodoxliturgica.view.PrayerScreen
import com.paradox543.malankaraorthodoxliturgica.view.SectionScreen
import com.paradox543.malankaraorthodoxliturgica.view.SettingsScreen
import com.paradox543.malankaraorthodoxliturgica.viewmodel.NavViewModel
import com.paradox543.malankaraorthodoxliturgica.viewmodel.PrayerViewModel

data class BottomNavItem(val route: String, val icon: ImageVector, val label: String)

val bottomNavItems = listOf(
    BottomNavItem("home", Icons.Default.Home, "Home"),
    BottomNavItem("settings", Icons.Default.Settings, "Settings")
)

@Composable
fun rememberScrollAwareVisibility(): Pair<MutableState<Boolean>, NestedScrollConnection> {
    val isVisible = remember { mutableStateOf(true) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
//                if (available.y > 0) {
//                    isVisible.value = true  // Scrolling UP → Show bars
//                } else
                if (available.y < 0) {
                    isVisible.value = false // Scrolling DOWN → Hide bars
                }
                return Offset.Zero
            }
        }
    }
    return isVisible to nestedScrollConnection
}

fun getPadding(padding: PaddingValues, currentRoute: String?): PaddingValues {
    return if (currentRoute != "prayerScreen") {
        padding
    } else {
        PaddingValues(0.dp)
    }
}

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun NavGraph(modifier: Modifier = Modifier) {
    val prayerViewModel: PrayerViewModel = hiltViewModel()
    val navViewModel: NavViewModel = hiltViewModel()
    val navController = rememberNavController()
    val (isVisible, nestedScrollConnection) = rememberScrollAwareVisibility()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val context = LocalContext.current
    val activity = context as? Activity

    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            if (destination.route != "prayerScreen") {
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
        }
        navController.addOnDestinationChangedListener(listener)

        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }
    Box(modifier = Modifier.fillMaxSize()){
        Scaffold(
            modifier = Modifier
                .nestedScroll(nestedScrollConnection)
                .pointerInput(Unit) {
                    detectTapGestures { isVisible.value = !isVisible.value }
                },
            containerColor = Color.Transparent,
            topBar = {
                if (currentRoute == "prayerScreen") {
                    AnimatedVisibility(
                        visible = isVisible.value,
                        modifier = Modifier.zIndex(1f)
                    ) {
                        TopNavBar(
                            navController = navController,
                            prayerViewModel = prayerViewModel,
                            navViewModel = navViewModel,
                            onActionClick = {navController.navigate("settings")}
                        )
                    }
                } else {
                    TopNavBar(navController, prayerViewModel, navViewModel)
                }
            },
            bottomBar = {
                if (currentRoute == "prayerScreen") {
                    AnimatedVisibility(
                        isVisible.value,
                        modifier = Modifier.zIndex(1f)
                    ) {
                        BottomNavBar(navController, prayerViewModel, navViewModel)
                    }
                } else {
                    BottomNavBar(navController, prayerViewModel, navViewModel)
                }
            }
        ) { innerPadding ->
            val padding = getPadding(innerPadding, currentRoute)
            NavHost(
                navController, startDestination = "home",
                Modifier.padding(padding)
            ) {
                composable("home") {
                    prayerViewModel.setSectionNavigation(false)
                    HomeScreen(navController, prayerViewModel, navViewModel)
                }
                composable("section/{route}") { backStackEntry ->
                    prayerViewModel.setSectionNavigation(false)
                    val route = backStackEntry.arguments?.getString("route") ?: ""
                    val node = navViewModel.findNode(navViewModel.rootNode, route)
                    Log.d("NavGraph", "route: $route, node: ${node?.route?:"no route"}")
                    if (node != null) {
                        prayerViewModel.setTopBarKeys(listOf(route))
                        SectionScreen(navController, prayerViewModel, navViewModel, node.children)
                    }
                }
                composable("prayerScreen") { //backStackEntry ->
//                    val route = backStackEntry.arguments?.getString("route") ?: ""
//                    val node = navViewModel.findNode(navViewModel.rootNode, route)
//                    Log.d("NavGraph prayerScreen", "route: $route, node: ${node?.route?:"no route"}")
                    prayerViewModel.setSectionNavigation(true)
                    PrayerScreen(navController, prayerViewModel, navViewModel)
                }
                composable("settings") {
                    prayerViewModel.setSectionNavigation(false)
                    SettingsScreen(navController, prayerViewModel)
                }
                composable("aboutApp"){
                    AboutAppScreen(navController)
                }
                composable("dummy") {
                    DummyScreen()
                }
            }
        }
    }
}
