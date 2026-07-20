package com.paradox543.malankaraorthodoxliturgica.feature.calendar.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.composables.icons.materialicons.MaterialIcons
import com.composables.icons.materialicons.rounded.Arrow_back
import com.composables.icons.materialicons.rounded.Arrow_forward
import com.paradox543.malankaraorthodoxliturgica.core.ui.scaffold.ScaffoldUiState
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.LiturgicalDay
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.LiturgicalEventDetails
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.SeasonName
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.WeekItem
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.feature.calendar.model.CalendarMode
import com.paradox543.malankaraorthodoxliturgica.feature.calendar.model.CalendarUiState
import com.paradox543.malankaraorthodoxliturgica.feature.calendar.viewmodel.CalendarViewModel
import kotlinx.datetime.LocalDate

@Composable
fun CalendarLiturgicalSeasonScreen(
    calendarViewModel: CalendarViewModel,
    contentPadding: PaddingValues = PaddingValues(),
    onPrayerNavigate: (String) -> Unit,
    onBibleNavigate: () -> Unit,
    onScaffoldStateChanged: (ScaffoldUiState) -> Unit = {},
) {
    val state by calendarViewModel.state.collectAsState()
    val selectedLanguage by calendarViewModel.selectedLanguage.collectAsState()
    val translations by calendarViewModel.translations.collectAsState()
    val selectedMonth =
        state.selectedDay
            ?.date
            ?.month
            ?.let {
                WeekItem.HeaderLabel(
                    it.name
                        .lowercase()
                        .replaceFirstChar { character ->
                            character.uppercase()
                        },
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

    LaunchedEffect(selectedLanguage, translations) {
        onScaffoldStateChanged(
            ScaffoldUiState.Standard(translations["calendar"] ?: "Calendar", showFab = false),
        )
    }

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
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 4.dp)
                .background(MaterialTheme.colorScheme.surface),
        ) {
            CalendarHeader(
                mode = state.mode,
                onNext = { calendarViewModel.nextSeason() },
                onPrev = { calendarViewModel.previousSeason() },
                hasNext = state.hasNextSeason,
                hasPrevious = state.hasPreviousSeason,
                selectedLanguage = selectedLanguage,
                translations = translations,
            )
            WeekdayHeader()

            if (state.selectedDay == null) {
                BrowseMode(state, calendarViewModel, translations)
            } else {
                InspectMode(
                    selectedDay = state.selectedDay,
                    selectedWeek = selectedWeek,
                    selectedMonth = selectedMonth,
                    selectedDayEvents = state.selectedDayEvents,
                    isSelectedDayEventsLoading = state.isSelectedDayEventsLoading,
                    selectedLanguage = selectedLanguage,
                    translations = translations,
                    calendarViewModel = calendarViewModel,
                    onPrayerNavigate = onPrayerNavigate,
                    onBibleNavigate = onBibleNavigate,
                )
            }
        }
    }
}

@Composable
private fun BrowseMode(
    state: CalendarUiState,
    calendarViewModel: CalendarViewModel,
    translations: Map<String, String>,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(state.weeks) {
        val todayWeekIndex = state.weeks.indexOfFirst { weekItem ->
            weekItem is WeekItem.LiturgicalWeek && weekItem.days.any { it.isToday }
        }

        if (todayWeekIndex != -1) {
            val isVisible = listState.layoutInfo.visibleItemsInfo.any { it.index == todayWeekIndex }
            if (!isVisible) {
                listState.scrollToItem(todayWeekIndex)
            }
        }
    }

    Column {
        LazyColumn(
            modifier = Modifier.weight(1f),
            state = listState,
        ) {
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
        CalendarLegend(translations)
    }
}

@Composable
private fun CalendarLegend(translations: Map<String, String>) {
    val dummyDate = LocalDate(2026, 1, 1)
    val normalDay = LiturgicalDay.empty(dummyDate).copy(seasonName = SeasonName.DUMMY)
    val fastingDay = LiturgicalDay.empty(dummyDate).copy(lent = 1, seasonName = SeasonName.DUMMY)
    val commemorationDay = LiturgicalDay.empty(dummyDate).copy(eventKeys = listOf("event"), seasonName = SeasonName.DUMMY)

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        HorizontalDivider(Modifier.padding(bottom = 4.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            LegendItem(
                modifier = Modifier.weight(1f),
                day = normalDay,
                showDate = false,
                label = translations["calendar_normalDay"] ?: "Normal Day",
            )
            LegendItem(
                modifier = Modifier.weight(1f),
                day = fastingDay,
                showDate = false,
                label = translations["calendar_fasting"] ?: "Fasting",
            )
            LegendItem(
                modifier = Modifier.weight(1.5f),
                day = commemorationDay,
                showDate = true,
                label = translations["calendar_commemorations"] ?: "Commemorations",
            )
        }
    }
}

@Composable
private fun LegendItem(
    modifier: Modifier = Modifier,
    day: LiturgicalDay,
    isSelected: Boolean = false,
    showDate: Boolean = true,
    label: String,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(modifier = Modifier.width(32.dp)) {
            DayCell(
                day = day,
                isSelected = isSelected,
                showDate = showDate,
                onClick = {},
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun InspectMode(
    selectedDay: LiturgicalDay?,
    selectedWeek: WeekItem.LiturgicalWeek?,
    selectedMonth: WeekItem.HeaderLabel?,
    selectedDayEvents: List<LiturgicalEventDetails>,
    isSelectedDayEventsLoading: Boolean,
    selectedLanguage: AppLanguage,
    translations: Map<String, String>,
    calendarViewModel: CalendarViewModel,
    onPrayerNavigate: (String) -> Unit,
    onBibleNavigate: () -> Unit,
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
        selectedDay?.let {
            Box(
                modifier = Modifier.weight(1f),
            ) {
                DayDetails(
                    day = selectedDay,
                    events = selectedDayEvents,
                    isEventsLoading = isSelectedDayEventsLoading,
                    selectedLanguage = selectedLanguage,
                    translations = translations,
                    calendarViewModel = calendarViewModel,
                    onPrayerNavigate = onPrayerNavigate,
                    onBibleNavigate = onBibleNavigate,
                )
            }
        }
    }
}

@Composable
fun CalendarHeader(
    mode: CalendarMode,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    hasNext: Boolean,
    hasPrevious: Boolean,
    selectedLanguage: AppLanguage,
    translations: Map<String, String>,
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
        IconButton(
            onClick = onPrev,
            modifier = Modifier.width(36.dp),
            enabled = hasPrevious,
        ) {
            Icon(MaterialIcons.Rounded.Arrow_back, contentDescription = "Back Arrow")
        }

        Text(
            text =
                when (mode) {
                    is CalendarMode.Season -> "${formatSeasonTitle(mode.name, selectedLanguage, translations)} ${mode.liturgicalYear}"
                    is CalendarMode.Month -> "${mode.month} ${mode.year}"
                },
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
        )

        IconButton(
            onClick = onNext,
            modifier = Modifier.width(36.dp),
            enabled = hasNext,
        ) {
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
    showDate: Boolean = true,
    onClick: () -> Unit,
) {
    val isClickable = day.eventKeys.isNotEmpty()
    val isVisible = day.seasonName != null
    val isToday = day.isToday
    val lentDayColor = lentDayColor()
    val cellShape = RoundedCornerShape(8.dp)

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
                .background(
                    if (isSelected) {
                        MaterialTheme.colorScheme.primaryContainer.copy(0.5f)
                    } else if (isClickable) {
                        MaterialTheme.colorScheme.secondaryContainer.copy(0.5f)
                    } else {
                        Color.Transparent
                    },
                    shape = cellShape,
                ).border(border, shape = cellShape)
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
            if (showDate) {
                Text(
                    text = day.date.day.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color =
                        if (day.lent != null) {
                            lentDayColor
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                )
            } else {
                Box(
                    modifier =
                        Modifier
                            .size(10.dp)
                            .background(
                                color =
                                    if (day.lent != null) {
                                        lentDayColor
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    },
                                shape = CircleShape,
                            ),
                )
            }
        }
    }
}

@Composable
fun DayDetails(
    day: LiturgicalDay,
    events: List<LiturgicalEventDetails>,
    isEventsLoading: Boolean,
    selectedLanguage: AppLanguage,
    translations: Map<String, String>,
    calendarViewModel: CalendarViewModel,
    onPrayerNavigate: (String) -> Unit,
    onBibleNavigate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(12.dp),
    ) {
        // Date
        Text(
            text = day.date.toString(),
            style = MaterialTheme.typography.titleLarge,
        )

        Spacer(Modifier.height(4.dp))

        // Metadata row
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            day.tune?.let {
                Text(
                    text = "${translations["tune"]}: $it",
                    style = MaterialTheme.typography.labelMedium,
                )
            }

//            day.season?.let {
//                Text(
//                    text = formatSeasonTitle(it, selectedLanguage, translations),
//                    style = MaterialTheme.typography.labelMedium,
//                )
//            }
        }

        Spacer(Modifier.height(12.dp))

        HorizontalDivider()

        Spacer(Modifier.height(12.dp))

        if (isEventsLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else if (day.eventKeys.isEmpty()) {
            Text(
                text = "No commemorations",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(events) { event ->
                    DisplayEvent(
                        event = event,
                        selectedLanguage = selectedLanguage,
                        calendarViewModel = calendarViewModel,
                        onPrayerNavigate = onPrayerNavigate,
                        onBibleNavigate = onBibleNavigate,
                    )
                }
            }
        }
    }
}

private fun formatSeasonTitle(
    season: SeasonName,
    selectedLanguage: AppLanguage,
    translations: Map<String, String>,
): String {
    val seasonName = translations[seasonTranslationKey(season)] ?: season.toDisplayName()
    return when (selectedLanguage) {
        AppLanguage.MALAYALAM -> {
            "${seasonName}കാലം"
        }

        AppLanguage.ENGLISH, AppLanguage.MANGLISH, AppLanguage.INDIC -> {
            "Season of $seasonName"
        }
    }
}

private fun seasonTranslationKey(season: SeasonName): String =
    when (season) {
        SeasonName.TRANSFIGURATION -> "transfigurationSeason"
        else -> season.toString().lowercase()
    }

private fun SeasonName.toDisplayName(): String =
    toString()
        .lowercase()
        .replace(Regex("(?<=[a-z])(?=[A-Z])"), " ")
        .replaceFirstChar(Char::uppercase)

@Composable
private fun lentDayColor(): Color =
    if (isSystemInDarkTheme()) {
        Color(0xFF90CAF9)
    } else {
        Color(0xFF1565C0)
    }
