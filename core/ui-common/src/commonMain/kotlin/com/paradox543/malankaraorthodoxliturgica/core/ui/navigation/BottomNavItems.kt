package com.paradox543.malankaraorthodoxliturgica.core.ui.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.composables.icons.materialicons.MaterialIcons
import com.composables.icons.materialicons.rounded.Calendar_month
import com.composables.icons.materialicons.rounded.Home
import com.paradox543.malankaraorthodoxliturgica.core.ui.Res
import com.paradox543.malankaraorthodoxliturgica.core.ui.bible
import com.paradox543.malankaraorthodoxliturgica.core.ui.clock
import org.jetbrains.compose.resources.painterResource

val iconSize = 24.dp

val bottomNavItems =
    listOf(
        BottomNavItem("home", "Home") {
            Icon(MaterialIcons.Rounded.Home, "Home")
        },
        BottomNavItem("prayNow", "Pray Now") {
            Icon(
                painterResource(Res.drawable.clock),
                "Clock",
                modifier = Modifier.size(iconSize),
            )
        },
//        BottomNavItem(
//            "music",
//            "Music",
//        ) {
//            Icon(
//                painterResource(R.drawable.musical_note),
//                "Music",
//                modifier = Modifier.size(iconSize),
//            )
//        },
        BottomNavItem("calendar", "Calendar") {
            Icon(
                MaterialIcons.Rounded.Calendar_month,
                "Calendar",
                Modifier.size(iconSize),
            )
        },
        BottomNavItem("bible", "Bible") {
            Icon(
                painterResource(Res.drawable.bible),
                "Bible",
                modifier = Modifier.size(iconSize),
            )
        },
    )