package com.paradox543.malankaraorthodoxliturgica.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PageNode
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.screens.PrayerScreen
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
