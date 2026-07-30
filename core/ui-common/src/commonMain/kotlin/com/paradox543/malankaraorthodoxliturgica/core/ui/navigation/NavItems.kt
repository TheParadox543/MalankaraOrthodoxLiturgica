package com.paradox543.malankaraorthodoxliturgica.core.ui.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.composables.icons.materialicons.MaterialIcons
import com.composables.icons.materialicons.rounded.Calendar_month
import com.composables.icons.materialicons.rounded.Home
import com.composables.icons.materialicons.rounded.Settings
import com.paradox543.malankaraorthodoxliturgica.core.ui.Res
import com.paradox543.malankaraorthodoxliturgica.core.ui.bible
import com.paradox543.malankaraorthodoxliturgica.core.ui.clock
import org.jetbrains.compose.resources.painterResource

val iconSize = 24.dp

val navItems =
    listOf(
        NavigationItem("home", "Home") {
            Icon(MaterialIcons.Rounded.Home, "Home")
        },
        NavigationItem("prayNow", "Pray Now") {
            Icon(
                painterResource(Res.drawable.clock),
                "Clock",
                modifier = Modifier.size(iconSize),
            )
        },
//        NavigationItem(
//            "music",
//            "Music",
//        ) {
//            Icon(
//                painterResource(R.drawable.musical_note),
//                "Music",
//                modifier = Modifier.size(iconSize),
//            )
//        },
        NavigationItem("calendar", "Calendar") {
            Icon(
                MaterialIcons.Rounded.Calendar_month,
                "Calendar",
                Modifier.size(iconSize),
            )
        },
        NavigationItem("bible", "Bible") {
            Icon(
                painterResource(Res.drawable.bible),
                "Bible",
                modifier = Modifier.size(iconSize),
            )
        },
        NavigationItem("settings", "Settings", isRailOnly = true) {
            Icon(
                MaterialIcons.Rounded.Settings,
                "Settings",
                Modifier.size(iconSize),
            )
        },
    )