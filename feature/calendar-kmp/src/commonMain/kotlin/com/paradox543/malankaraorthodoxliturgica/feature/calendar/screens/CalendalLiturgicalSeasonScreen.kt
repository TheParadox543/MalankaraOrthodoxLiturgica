package com.paradox543.malankaraorthodoxliturgica.feature.calendar.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.composables.icons.materialicons.MaterialIcons
import com.composables.icons.materialicons.rounded.Arrow_back
import com.composables.icons.materialicons.rounded.Arrow_forward
import com.paradox543.malankaraorthodoxliturgica.core.ui.scaffold.ScaffoldUiState
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.LiturgicalDay
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.LiturgicalWeek
import com.paradox543.malankaraorthodoxliturgica.feature.calendar.model.CalendarMode
import com.paradox543.malankaraorthodoxliturgica.feature.calendar.viewmodel.CalendarViewModel

@Composable
fun CalendarLiturgicalSeasonScreen(
    calendarViewModel: CalendarViewModel,
    contentPadding: PaddingValues = PaddingValues(),
    onScaffoldStateChanged: (ScaffoldUiState) -> Unit = {},
) {
    val state by calendarViewModel.state.collectAsState()

    LaunchedEffect(Unit) { onScaffoldStateChanged(ScaffoldUiState.Standard("Calendar")) }

    if (state.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    } else if (state.error != null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error: ${state.error}")
        }
        return
    } else {
        Column(
            Modifier
                .padding(contentPadding),
        ) {
            CalendarHeader(
                mode = state.mode,
                onNext = { calendarViewModel.nextSeason() },
                onPrev = { calendarViewModel.previousSeason() },
            )
            WeekdayHeader()
            LazyColumn {
                items(state.weeks) { week ->
                    WeekRow(
                        week = week,
                        onDayClick = { day ->
//                    calendarViewModel.onDayClicked(day)
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun CalendarHeader(
    mode: CalendarMode,
    onNext: () -> Unit,
    onPrev: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(onClick = onPrev) {
            Icon(MaterialIcons.Rounded.Arrow_back, contentDescription = null)
        }

        Text(
            text =
                when (mode) {
                    is CalendarMode.Season -> mode.name.replaceFirstChar { it.uppercase() } + " ${mode.liturgicalYear}"
                    is CalendarMode.Month -> "${mode.month} ${mode.year}"
                },
            style = MaterialTheme.typography.titleLarge,
        )

        IconButton(onClick = onNext) {
            Icon(MaterialIcons.Rounded.Arrow_forward, contentDescription = null)
        }
    }
}

@Composable
fun WeekdayHeader() {
    val days = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background),
    ) {
        days.forEach { day ->
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}

@Composable
fun WeekRow(
    week: LiturgicalWeek,
    onDayClick: (LiturgicalDay) -> Unit = {},
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        week.days.forEach { day ->
            DayCell(
                day = day,
                modifier = Modifier.weight(1f),
                onClick = { onDayClick(day) },
            )
        }
    }
}

@Composable
fun DayCell(
    day: LiturgicalDay,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val isClickable = day.eventKeys.isNotEmpty()
    val isVisible = day.season != null

    Box(
        modifier =
            modifier
                .aspectRatio(1f)
                .padding(8.dp)
                .then(
                    if (isClickable) {
                        Modifier.clickable { onClick() }
//                            .border(
//                                width = 1.dp,
//                                color = MaterialTheme.colorScheme.primary,
//                                shape = CircleShape,
//                            )
                    } else {
                        Modifier
                    },
                ),
        contentAlignment = Alignment.Center,
    ) {
        if (!isVisible) return@Box

        // Tune
        day.tune?.let {
            Text(
                it.toString(),
                Modifier.align(Alignment.TopEnd),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Date
            Text(
                text = day.date.day.toString(),
                style = MaterialTheme.typography.bodyLarge,
                color =
                    if (day.lent != null) {
                        Color.Blue
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
            )

            Box(
                Modifier.height(6.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (isClickable) {
                    Box(
                        Modifier
//                            .padding(top = 4.dp)
                            .height(6.dp)
                            .width(6.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape,
                            ),
                    )
                }
            }
        }
    }
}