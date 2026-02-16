package com.paradox543.malankaraorthodoxliturgica.ui.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.paradox543.malankaraorthodoxliturgica.R

val iconSize = 24.dp
val bottomNavItems =
    listOf(
        BottomNavItem("home", "Home") {
            Icon(Icons.Default.Home, "Home")
        },
        BottomNavItem("prayNow", "Pray Now") {
            Icon(
                painterResource(R.drawable.clock),
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
                painterResource(R.drawable.calendar),
                "Calendar",
                Modifier.size(iconSize),
            )
        },
        BottomNavItem("bible", "Bible") {
            Icon(
                painterResource(R.drawable.bible),
                "Bible",
                modifier = Modifier.size(iconSize),
            )
        },
    )