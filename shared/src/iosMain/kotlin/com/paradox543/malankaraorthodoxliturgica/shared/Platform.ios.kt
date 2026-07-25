package com.paradox543.malankaraorthodoxliturgica.shared

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeUIViewController
import com.paradox543.malankaraorthodoxliturgica.core.ui.scaffold.ScaffoldUiState
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PageNode
import com.paradox543.malankaraorthodoxliturgica.feature.calendar.viewmodel.CalendarViewModel
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.screens.HomeScreen
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.screens.IndexScreen
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.screens.PrayNowScreen
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.screens.PrayerScreen
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.screens.SectionScreen
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.viewmodel.PrayerNavViewModel
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.viewmodel.PrayerViewModel
import org.koin.mp.KoinPlatform.getKoin
import platform.UIKit.UIViewController

actual fun platformName(): String = "iOS"

/**
 * `ScaffoldUiState` is intentionally not exported to Swift (it would widen
 * the framework's public surface for a single enum). Every screen wrapper
 * flattens it to primitives here before it crosses the bridge. `showBack`
 * and `showSettings` are NOT included — on Android those are computed by
 * `NavGraph.kt` from route identity (`currentRoute != AppScreen.Home.route`
 * etc.), not from `ScaffoldUiState`, so Swift's `AppRouter` computes them the
 * same way locally.
 */
private fun ScaffoldUiState.toChromeState(): Pair<String, Boolean> =
    when (this) {
        is ScaffoldUiState.Standard -> title to showFab
        is ScaffoldUiState.PrayerReading -> title to showFab
        is ScaffoldUiState.BibleChapterReading -> title to showFab
        ScaffoldUiState.None -> "" to false
    }

@Composable
fun PrayerScreenWrapper(
    fileName: String,
    onPrayerButtonClick: (String, Boolean) -> Unit,
    onChromeStateChanged: (String, Boolean) -> Unit
) {
    val koin = getKoin()
    val prayerViewModel: PrayerViewModel = koin.get()
    val prayerNavViewModel: PrayerNavViewModel = koin.get()

    val node = remember(fileName) {
        PageNode(
            route = fileName.substringBeforeLast("."),
            filename = fileName,
            parent = null
        )
    }

    PrayerScreen(
        onPrayerButtonClick = onPrayerButtonClick,
        prayerViewModel = prayerViewModel,
        prayerNavViewModel = prayerNavViewModel,
        node = node,
        onQrDialogShow = { _, _ -> "" },
        routeProvider = { it },
        onScaffoldStateChanged = { state ->
            val (title, showFab) = state.toChromeState()
            onChromeStateChanged(title, showFab)
        }
    )
}

fun getPrayerViewController(
    fileName: String,
    onPrayerButtonClick: (String, Boolean) -> Unit,
    onChromeStateChanged: (String, Boolean) -> Unit
): UIViewController = ComposeUIViewController {
    PrayerScreenWrapper(fileName, onPrayerButtonClick, onChromeStateChanged)
}

@Composable
fun HomeScreenWrapper(
    onSectionNavigate: (String) -> Unit,
    onPrayerNavigate: (String) -> Unit,
    onSongNavigate: (String) -> Unit,
    onPrayNowNavigate: () -> Unit,
    onIndexNavigate: () -> Unit,
    onChromeStateChanged: (String, Boolean) -> Unit
) {
    val koin = getKoin()
    val prayerViewModel: PrayerViewModel = koin.get()
    val prayerNavViewModel: PrayerNavViewModel = koin.get()
    val calendarViewModel: CalendarViewModel = koin.get()

    val liturgicalDay by calendarViewModel.todayLiturgicalDay.collectAsState()
    val recommendedPrayers = prayerNavViewModel.getAllPrayerNodes()
    val topPrayer = recommendedPrayers.firstOrNull()

    HomeScreen(
        prayerViewModel = prayerViewModel,
        prayerNavViewModel = prayerNavViewModel,
        liturgicalDay = liturgicalDay,
        topRecommendedPrayer = topPrayer,
        contentPadding = PaddingValues(0.dp),
        onSectionNavigate = onSectionNavigate,
        onPrayerNavigate = onPrayerNavigate,
        onSongNavigate = onSongNavigate,
        onPrayNowNavigate = onPrayNowNavigate,
        onIndexNavigate = onIndexNavigate,
        onScaffoldStateChanged = { state ->
            val (title, showFab) = state.toChromeState()
            onChromeStateChanged(title, showFab)
        }
    )
}

fun getHomeViewController(
    onSectionNavigate: (String) -> Unit,
    onPrayerNavigate: (String) -> Unit,
    onSongNavigate: (String) -> Unit,
    onPrayNowNavigate: () -> Unit,
    onIndexNavigate: () -> Unit,
    onChromeStateChanged: (String, Boolean) -> Unit
): UIViewController = ComposeUIViewController {
    HomeScreenWrapper(
        onSectionNavigate,
        onPrayerNavigate,
        onSongNavigate,
        onPrayNowNavigate,
        onIndexNavigate,
        onChromeStateChanged
    )
}

@Composable
fun SectionScreenWrapper(
    route: String,
    onSectionNavigate: (String) -> Unit,
    onPrayerNavigate: (String) -> Unit,
    onSongNavigate: (String) -> Unit,
    onIndexNavigate: () -> Unit,
    onChromeStateChanged: (String, Boolean) -> Unit
) {
    val koin = getKoin()
    val prayerViewModel: PrayerViewModel = koin.get()
    val prayerNavViewModel: PrayerNavViewModel = koin.get()
    val calendarViewModel: CalendarViewModel = koin.get()

    val rootNode by prayerNavViewModel.rootNode.collectAsState()
    val node = rootNode.findByRoute(route) ?: PageNode(route = route, parent = null)
    val liturgicalDay by calendarViewModel.todayLiturgicalDay.collectAsState()

    SectionScreen(
        prayerViewModel = prayerViewModel,
        prayerNavViewModel = prayerNavViewModel,
        node = node,
        contentPadding = PaddingValues(0.dp),
        onScaffoldStateChanged = { state ->
            val (title, showFab) = state.toChromeState()
            onChromeStateChanged(title, showFab)
        },
        onSectionNavigate = onSectionNavigate,
        onPrayerNavigate = onPrayerNavigate,
        onSongNavigate = onSongNavigate,
        onIndexNavigate = onIndexNavigate,
        liturgicalDay = liturgicalDay
    )
}

fun getSectionViewController(
    route: String,
    onSectionNavigate: (String) -> Unit,
    onPrayerNavigate: (String) -> Unit,
    onSongNavigate: (String) -> Unit,
    onIndexNavigate: () -> Unit,
    onChromeStateChanged: (String, Boolean) -> Unit
): UIViewController = ComposeUIViewController {
    SectionScreenWrapper(
        route,
        onSectionNavigate,
        onPrayerNavigate,
        onSongNavigate,
        onIndexNavigate,
        onChromeStateChanged
    )
}

@Composable
fun IndexScreenWrapper(
    onPrayerNavigate: (String) -> Unit,
    onChromeStateChanged: (String, Boolean) -> Unit
) {
    val koin = getKoin()
    val prayerViewModel: PrayerViewModel = koin.get()
    val prayerNavViewModel: PrayerNavViewModel = koin.get()

    IndexScreen(
        prayerViewModel = prayerViewModel,
        prayerNavViewModel = prayerNavViewModel,
        contentPadding = PaddingValues(0.dp),
        onPrayerNavigate = onPrayerNavigate,
        onScaffoldStateChanged = { state ->
            val (title, showFab) = state.toChromeState()
            onChromeStateChanged(title, showFab)
        }
    )
}

fun getIndexViewController(
    onPrayerNavigate: (String) -> Unit,
    onChromeStateChanged: (String, Boolean) -> Unit
): UIViewController = ComposeUIViewController {
    IndexScreenWrapper(onPrayerNavigate, onChromeStateChanged)
}

@Composable
fun PrayNowScreenWrapper(
    onPrayerNavigate: (String) -> Unit,
    onChromeStateChanged: (String, Boolean) -> Unit
) {
    val koin = getKoin()
    val prayerViewModel: PrayerViewModel = koin.get()
    val prayerNavViewModel: PrayerNavViewModel = koin.get()

    PrayNowScreen(
        onCardClick = onPrayerNavigate,
        prayerViewModel = prayerViewModel,
        prayerNavViewModel = prayerNavViewModel,
        contentPadding = PaddingValues(0.dp),
        onScaffoldStateChanged = { state ->
            val (title, showFab) = state.toChromeState()
            onChromeStateChanged(title, showFab)
        }
    )
}

fun getPrayNowViewController(
    onPrayerNavigate: (String) -> Unit,
    onChromeStateChanged: (String, Boolean) -> Unit
): UIViewController = ComposeUIViewController {
    PrayNowScreenWrapper(onPrayerNavigate, onChromeStateChanged)
}
