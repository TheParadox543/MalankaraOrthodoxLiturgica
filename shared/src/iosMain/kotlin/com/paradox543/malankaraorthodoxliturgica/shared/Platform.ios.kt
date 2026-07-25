package com.paradox543.malankaraorthodoxliturgica.shared

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeUIViewController
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

@Composable
fun PrayerScreenWrapper(
    fileName: String,
    onPrayerButtonClick: (String, Boolean) -> Unit
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
        onScaffoldStateChanged = { }
    )
}

fun getPrayerViewController(
    fileName: String,
    onPrayerButtonClick: (String, Boolean) -> Unit
): UIViewController = ComposeUIViewController {
    PrayerScreenWrapper(fileName, onPrayerButtonClick)
}

@Composable
fun HomeScreenWrapper(
    onSectionNavigate: (String) -> Unit,
    onPrayerNavigate: (String) -> Unit,
    onSongNavigate: (String) -> Unit,
    onPrayNowNavigate: () -> Unit,
    onIndexNavigate: () -> Unit
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
        onScaffoldStateChanged = { }
    )
}

fun getHomeViewController(
    onSectionNavigate: (String) -> Unit,
    onPrayerNavigate: (String) -> Unit,
    onSongNavigate: (String) -> Unit,
    onPrayNowNavigate: () -> Unit,
    onIndexNavigate: () -> Unit
): UIViewController = ComposeUIViewController {
    HomeScreenWrapper(
        onSectionNavigate,
        onPrayerNavigate,
        onSongNavigate,
        onPrayNowNavigate,
        onIndexNavigate
    )
}

@Composable
fun SectionScreenWrapper(
    route: String,
    onSectionNavigate: (String) -> Unit,
    onPrayerNavigate: (String) -> Unit,
    onSongNavigate: (String) -> Unit,
    onIndexNavigate: () -> Unit
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
        onScaffoldStateChanged = { },
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
    onIndexNavigate: () -> Unit
): UIViewController = ComposeUIViewController {
    SectionScreenWrapper(
        route,
        onSectionNavigate,
        onPrayerNavigate,
        onSongNavigate,
        onIndexNavigate
    )
}

@Composable
fun IndexScreenWrapper(
    onPrayerNavigate: (String) -> Unit
) {
    val koin = getKoin()
    val prayerViewModel: PrayerViewModel = koin.get()
    val prayerNavViewModel: PrayerNavViewModel = koin.get()

    IndexScreen(
        prayerViewModel = prayerViewModel,
        prayerNavViewModel = prayerNavViewModel,
        contentPadding = PaddingValues(0.dp),
        onPrayerNavigate = onPrayerNavigate,
        onScaffoldStateChanged = { }
    )
}

fun getIndexViewController(
    onPrayerNavigate: (String) -> Unit
): UIViewController = ComposeUIViewController {
    IndexScreenWrapper(onPrayerNavigate)
}

@Composable
fun PrayNowScreenWrapper(
    onPrayerNavigate: (String) -> Unit
) {
    val koin = getKoin()
    val prayerViewModel: PrayerViewModel = koin.get()
    val prayerNavViewModel: PrayerNavViewModel = koin.get()

    PrayNowScreen(
        onCardClick = onPrayerNavigate,
        prayerViewModel = prayerViewModel,
        prayerNavViewModel = prayerNavViewModel,
        contentPadding = PaddingValues(0.dp),
        onScaffoldStateChanged = { }
    )
}

fun getPrayNowViewController(
    onPrayerNavigate: (String) -> Unit
): UIViewController = ComposeUIViewController {
    PrayNowScreenWrapper(onPrayerNavigate)
}
