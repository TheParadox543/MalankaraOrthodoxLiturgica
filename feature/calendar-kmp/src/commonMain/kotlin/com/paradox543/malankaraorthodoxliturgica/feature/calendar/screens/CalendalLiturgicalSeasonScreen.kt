package com.paradox543.malankaraorthodoxliturgica.feature.calendar.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.composables.icons.materialicons.MaterialIcons
import com.composables.icons.materialicons.rounded.Arrow_back
import com.composables.icons.materialicons.rounded.Arrow_forward
import com.paradox543.malankaraorthodoxliturgica.core.ui.scaffold.ScaffoldUiState
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.LiturgicalDay
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.WeekItem
import com.paradox543.malankaraorthodoxliturgica.feature.calendar.model.CalendarMode
import com.paradox543.malankaraorthodoxliturgica.feature.calendar.model.CalendarUiState
import com.paradox543.malankaraorthodoxliturgica.feature.calendar.viewmodel.CalendarViewModel

@Composable
fun CalendarLiturgicalSeasonScreen(
    calendarViewModel: CalendarViewModel,
    contentPadding: PaddingValues = PaddingValues(),
    onScaffoldStateChanged: (ScaffoldUiState) -> Unit = {},
) {
    val state by calendarViewModel.state.collectAsState()
    val selectedMonth =
        state.selectedDay
            ?.date
            ?.month
            ?.let {
                WeekItem.HeaderLabel(
                    it.name
                        .lowercase()
                        .replaceFirstChar { it.uppercase() },
                )
            }
    val selectedWeek: WeekItem.LiturgicalWeek? =
        state.weeks
            .filterIsInstance<WeekItem.LiturgicalWeek>()
            .firstOrNull { week ->
                state.selectedDay?.let { selected ->
                    week.days.any { it.date == selected.date }
                } == true
            }

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
                .padding(contentPadding)
                .padding(horizontal = 4.dp)
                .background(MaterialTheme.colorScheme.surface),
        ) {
            CalendarHeader(
                mode = state.mode,
                onNext = { calendarViewModel.nextSeason() },
                onPrev = { calendarViewModel.previousSeason() },
            )
            WeekdayHeader()

            if (state.selectedDay == null) {
                BrowseMode(state, calendarViewModel)
            } else {
                InspectMode(state.selectedDay, selectedWeek, selectedMonth, calendarViewModel)
            }
        }
    }
}

@Composable
private fun BrowseMode(
    state: CalendarUiState,
    calendarViewModel: CalendarViewModel,
) {
    LazyColumn {
        state.weeks.forEach { weekItem ->
            when (weekItem) {
                is WeekItem.HeaderLabel -> {
                    stickyHeader {
                        MonthHeader(weekItem)
                    }
                }

                is WeekItem.LiturgicalWeek -> {
                    item {
                        WeekRow(
                            week = weekItem,
                            selectedDay = state.selectedDay,
                            onDayClick = calendarViewModel::selectDay,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InspectMode(
    selectedDay: LiturgicalDay?,
    selectedWeek: WeekItem.LiturgicalWeek?,
    selectedMonth: WeekItem.HeaderLabel?,
    calendarViewModel: CalendarViewModel,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        selectedMonth?.let { MonthHeader(selectedMonth) }

        selectedWeek?.let {
            WeekRow(
                week = selectedWeek,
                selectedDay = selectedDay,
                onDayClick = calendarViewModel::selectDay,
            )
        }

        HorizontalDivider()
        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
        ) {
            Text("Testing Day Details on click for ${selectedDay?.date}")
        }
//        Box(
//            modifier = Modifier.weight(1f)
//        ) {
//            DayDetails(
//                day = selectedDay,
//                onClose = onClose
//            )
//        }
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
                .background(MaterialTheme.colorScheme.surfaceDim)
                .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(onClick = onPrev) {
            Icon(MaterialIcons.Rounded.Arrow_back, contentDescription = "Back Arrow")
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
            Icon(MaterialIcons.Rounded.Arrow_forward, contentDescription = "Forward Arrow")
        }
    }
}

@Composable
fun WeekdayHeader() {
    val days = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    Row(
        modifier =
            Modifier
                .fillMaxWidth(),
//                .background(MaterialTheme.colorScheme.background),
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
private fun MonthHeader(weekItem: WeekItem.HeaderLabel) {
    Text(
        text = weekItem.name,
        style = MaterialTheme.typography.titleSmall,
        modifier =
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(8.dp),
    )
}

@Composable
fun WeekRow(
    week: WeekItem.LiturgicalWeek,
    selectedDay: LiturgicalDay?,
    onDayClick: (LiturgicalDay) -> Unit = {},
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        week.days.forEach { day ->
            DayCell(
                modifier = Modifier.weight(1f),
                day = day,
                isSelected = day.date == selectedDay?.date,
                onClick = { onDayClick(day) },
            )
        }
    }
}

@Composable
fun DayCell(
    modifier: Modifier = Modifier,
    day: LiturgicalDay,
    isSelected: Boolean = false,
    onClick: () -> Unit,
) {
    val isClickable = day.eventKeys.isNotEmpty()
    val isVisible = day.season != null
    val isToday = day.isToday

    val border =
        when {
            isSelected -> {
                BorderStroke(
                    2.dp,
                    MaterialTheme.colorScheme.primary,
                )
            }

            isToday -> {
                BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline,
                )
            }

            else -> {
                BorderStroke(0.dp, Color.Transparent)
            }
        }

    Box(
        modifier =
            modifier
                .aspectRatio(1f)
                .padding(vertical = 4.dp, horizontal = 2.dp)
                .border(border, shape = RectangleShape)
                .then(
                    if (isClickable) {
                        Modifier.clickable { onClick() }
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
                Modifier.align(Alignment.TopEnd).padding(end = 2.dp, top = 2.dp),
                style =
                    MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
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