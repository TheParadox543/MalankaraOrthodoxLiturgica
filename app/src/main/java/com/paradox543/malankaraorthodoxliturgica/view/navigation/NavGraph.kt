package com.paradox543.malankaraorthodoxliturgica.view.navigation

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.paradox543.malankaraorthodoxliturgica.R
import com.paradox543.malankaraorthodoxliturgica.view.AboutAppScreen
import com.paradox543.malankaraorthodoxliturgica.view.CategoryListScreen
import com.paradox543.malankaraorthodoxliturgica.view.DummyScreen
import com.paradox543.malankaraorthodoxliturgica.view.GreatLentDayScreen
import com.paradox543.malankaraorthodoxliturgica.view.GreatLentScreen
import com.paradox543.malankaraorthodoxliturgica.view.HomeScreen
import com.paradox543.malankaraorthodoxliturgica.view.PrayerScreen
import com.paradox543.malankaraorthodoxliturgica.view.QurbanaScreen
import com.paradox543.malankaraorthodoxliturgica.view.WeddingScreen
import com.paradox543.malankaraorthodoxliturgica.view.SettingsScreen
import com.paradox543.malankaraorthodoxliturgica.view.SleebaScreen
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavBar(
    navController: NavController,
    prayerViewModel: PrayerViewModel,
    onActionClick: (() -> Unit)? = null // Optional Action button
) {
    val topBarNames by prayerViewModel.topBarNames.collectAsState()
    val selectedLanguage by prayerViewModel.selectedLanguage.collectAsState()
    var translations by remember { mutableStateOf(prayerViewModel.translations) }
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val isVisible = rememberScrollAwareVisibility()

    LaunchedEffect(selectedLanguage) {
        translations = prayerViewModel.loadTranslations()
    }

    val title = if (currentRoute == "settings") {
        translations["malankara"] ?: "error"
    } else {
        topBarNames.joinToString(separator = " ") { key ->
            translations[key] ?: "error"
        }
    }

    TopAppBar(
        title = { Text(title) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Blue,
            titleContentColor = Color.White
        ),
        navigationIcon = {
            if (topBarNames != listOf("malankara") && currentRoute != "settings") {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous Page",
                    )
                }
            }
        },
        actions = {
            if (onActionClick != null) {
                IconButton(onClick = onActionClick) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Action Button",
                        tint = Color.White
                    )
                }
            }
        }
    )
}

@Composable
fun BottomNavBar(navController: NavController, prayerViewModel: PrayerViewModel) {
    val sectionNavigation by prayerViewModel.sectionNavigation.collectAsState()
    val isVisible = rememberScrollAwareVisibility() // Track scroll visibility

    if (sectionNavigation) {
        SequentialNavBar(navController, prayerViewModel)
    } else {
        DefaultBottomNavBar(navController)
    }
}

@Composable
fun DefaultBottomNavBar(navController: NavController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute == "settings"){
                        navController.navigateUp()
                    } else {
                        navController.navigate(item.route)
                    }
                }
            )
        }
    }
}

@Composable
fun SequentialNavBar(navController: NavController, prayerViewModel: PrayerViewModel) {
    val topBarNames by prayerViewModel.topBarNames.collectAsState()
    val currentIndex = prayerViewModel.sectionNames.indexOf(topBarNames.last())
    val sectionSize = prayerViewModel.sectionNames.size - 1

    NavigationBar {
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Previous",
                )
            },
            label = { Text("Previous") },
            selected = false,
            enabled = currentIndex > 0,
            onClick = {
                prayerViewModel.getPreviousPrayer()
            }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next",
                )
            },
            label = { Text("Next") },
            selected = false,
            enabled = currentIndex < sectionSize,
            onClick = {
                prayerViewModel.getNextPrayer()
            }
        )
    }
}

fun getPadding(padding: PaddingValues, currentRoute: String?): PaddingValues {
    return if (currentRoute != "prayerScreen") {
        padding
    } else {
        PaddingValues(0.dp)
    }
}

@Composable
fun NavGraph(prayerViewModel: PrayerViewModel, modifier: Modifier = Modifier) {
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
//        if (currentRoute != "prayerScreen") {
//            Image(
//                painter = painterResource(id = R.drawable.background),
//                contentDescription = "Background",
//                contentScale = ContentScale.FillWidth,
//                modifier = Modifier.fillMaxSize()
//            )
//        }
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(
//                brush = (painterResource(R.drawable.background_image))
//            )
//    ) {
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
                            onActionClick = {navController.navigate("settings")}
                        )
                    }
                } else {
                    TopNavBar(navController, prayerViewModel)
                }
            },
            bottomBar = {
                if (currentRoute == "prayerScreen") {
                    AnimatedVisibility(
                        isVisible.value,
                        modifier = Modifier.zIndex(1f)
                    ) {
                        BottomNavBar(navController, prayerViewModel)
                    }
                } else {
                    BottomNavBar(navController, prayerViewModel)
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
                    HomeScreen(navController, prayerViewModel)
                }
                composable("prayer_list/{category}") { navBackStackEntry ->
                    val category = navBackStackEntry.arguments?.getString("category") ?: ""
                    prayerViewModel.setSectionNavigation(false)
                    CategoryListScreen(navController, prayerViewModel, category)
                }
                composable("great_lent_main") {
                    prayerViewModel.setSectionNavigation(false)
                    GreatLentScreen(navController, prayerViewModel)
                }
                composable("great_lent_day/{day}") { navBackStackEntry ->
                    val day = navBackStackEntry.arguments?.getString("day") ?: ""
                    prayerViewModel.setSectionNavigation(false)
                    GreatLentDayScreen(navController, prayerViewModel, day)
                }
                composable("sleeba"){
                    prayerViewModel.setSectionNavigation(false)
                    SleebaScreen(navController, prayerViewModel)
                }
                composable("prayerScreen") {
                    prayerViewModel.setSectionNavigation(true)
                    PrayerScreen(navController, prayerViewModel)
                }
                composable("qurbana") {
                    prayerViewModel.setSectionNavigation(false)
                    QurbanaScreen(navController, prayerViewModel)
                }
                composable("wedding") {
                    prayerViewModel.setSectionNavigation(false)
                    WeddingScreen(navController, prayerViewModel)
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
