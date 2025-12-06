package com.paradox543.malankaraorthodoxliturgica.ui.screens

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.paradox543.malankaraorthodoxliturgica.data.model.Screen
import com.paradox543.malankaraorthodoxliturgica.domain.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.model.CalendarDay
import com.paradox543.malankaraorthodoxliturgica.domain.model.CalendarWeek
import com.paradox543.malankaraorthodoxliturgica.domain.model.LiturgicalEventDetails
import com.paradox543.malankaraorthodoxliturgica.navigation.TopNavBar
import com.paradox543.malankaraorthodoxliturgica.ui.components.BottomNavBar
import com.paradox543.malankaraorthodoxliturgica.ui.viewmodel.BibleViewModel
import com.paradox543.malankaraorthodoxliturgica.ui.viewmodel.CalendarViewModel
import com.paradox543.malankaraorthodoxliturgica.ui.viewmodel.PrayerViewModel
import com.paradox543.malankaraorthodoxliturgica.ui.viewmodel.SettingsViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    navController: NavController,
    bibleViewModel: BibleViewModel,
    calendarViewModel: CalendarViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel(),
) {
    // Collect the StateFlows from the ViewModel
    val monthCalendarData by calendarViewModel.monthCalendarData.collectAsState()
    val currentCalendarViewDate by calendarViewModel.currentCalendarViewDate.collectAsState()
    val hasPrevMonth by calendarViewModel.hasPreviousMonth.collectAsState()
    val hasNextMonth by calendarViewModel.hasNextMonth.collectAsState()
    val selectedDate by calendarViewModel.selectedDate.collectAsState()
    val displayEvents by calendarViewModel.selectedDayViewData.collectAsState()
    val isLoading by calendarViewModel.isLoading.collectAsState()
    val error by calendarViewModel.error.collectAsState()
    val selectedLanguage by settingsViewModel.selectedLanguage.collectAsState()

    Scaffold(
        topBar = { TopNavBar("Calendar", navController) },
        bottomBar = { BottomNavBar(navController) },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 8.dp)
                    .fillMaxSize(),
        ) {
            if (isLoading) {
                // Show a loading indicator
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            } else if (error != null || monthCalendarData.isEmpty()) {
                // Show an error message
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Error: $error",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp),
                        )
                        // Add a retry button if appropriate
                        Button(onClick = {
                            calendarViewModel.loadMonth(
                                currentCalendarViewDate.monthValue,
                                currentCalendarViewDate.year,
                            )
                        }) {
                            Text("Retry")
                        }
                    }
                }
            } else {
                // Calendar content
                val configuration = LocalConfiguration.current
                val screenWidth = configuration.screenWidthDp.dp
                if (screenWidth <= 600.dp) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                    ) {
                        CalendarMainView(
                            calendarViewModel,
                            currentCalendarViewDate,
                            hasPrevMonth,
                            hasNextMonth,
                            monthCalendarData,
                            selectedDate,
                        )
                    }
                    if (displayEvents.isNotEmpty()) {
                        val scrollState = rememberScrollState()
                        HorizontalDivider(
                            thickness = 8.dp,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        DisplayEvents(
                            scrollState,
                            displayEvents,
                            selectedLanguage,
                            navController,
                            calendarViewModel,
                            bibleViewModel,
                        )
                    }
                } else {
                    Row(Modifier.padding(8.dp)) {
                        CalendarMainView(
                            calendarViewModel,
                            currentCalendarViewDate,
                            hasPrevMonth,
                            hasNextMonth,
                            monthCalendarData,
                            selectedDate,
                        )
                        if (displayEvents.isNotEmpty()) {
                            val scrollState = rememberScrollState()
                            VerticalDivider(
                                thickness = 8.dp,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            DisplayEvents(
                                scrollState,
                                displayEvents,
                                selectedLanguage,
                                navController,
                                calendarViewModel,
                                bibleViewModel,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarMainView(
    calendarViewModel: CalendarViewModel,
    currentCalendarViewDate: LocalDate,
    hasPrevMonth: Boolean,
    hasNextMonth: Boolean,
    monthCalendarData: List<CalendarWeek>,
    selectedDate: LocalDate?,
) {
    Column(
        Modifier
            .widthIn(max = 400.dp)
            .verticalScroll(rememberScrollState())
            .border(4.dp, MaterialTheme.colorScheme.outline)
            .padding(4.dp),
    ) {
        MonthNavigation(
            calendarViewModel,
            currentCalendarViewDate,
            hasPrevMonth,
            hasNextMonth,
        )
        DayOfWeekHeaders()
        CalendarGrid(
            monthCalendarData,
            currentCalendarViewDate,
            calendarViewModel,
            selectedDate,
        )
    }
}

@Composable
private fun MonthNavigation(
    calendarViewModel: CalendarViewModel,
    currentCalendarViewDate: LocalDate,
    hasPrevMonth: Boolean,
    hasNextMonth: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween, // Space out elements
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = calendarViewModel::goToPreviousMonth, // Connect to ViewModel
            modifier = Modifier.weight(0.15f), // Give it some weight for spacing
            enabled = hasPrevMonth,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Previous Month",
            )
        }
        Text(
            text = "${
                currentCalendarViewDate.month.getDisplayName(
                    TextStyle.FULL,
                    Locale.getDefault(),
                )
            } ${currentCalendarViewDate.year}",
            modifier = Modifier.weight(0.7f), // Give more weight to the month/year text
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
        )
        IconButton(
            onClick = calendarViewModel::goToNextMonth, // Connect to ViewModel
            modifier = Modifier.weight(0.15f), // Give it some weight for spacing
            enabled = hasNextMonth,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Next Month", // Corrected content description
            )
        }
    }
}

@Composable
private fun DayOfWeekHeaders() {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        val daysOfWeek =
            listOf(
                DayOfWeek.SUNDAY,
                DayOfWeek.MONDAY,
                DayOfWeek.TUESDAY,
                DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY,
                DayOfWeek.FRIDAY,
                DayOfWeek.SATURDAY,
            )
        daysOfWeek.forEach { dayOfWeek ->
            Text(
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Composable
private fun CalendarGrid(
    monthCalendarData: List<CalendarWeek>,
    currentCalendarViewDate: LocalDate,
    calendarViewModel: CalendarViewModel,
    selectedDate: LocalDate?,
) {
    Column {
        monthCalendarData.forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                week.days.forEach { day ->
                    // Determine if the day belongs to the current month being viewed
                    DayItem(currentCalendarViewDate, day, calendarViewModel, selectedDate)
                }
            }
        }
    }
}

@Composable
private fun RowScope.DayItem(
    currentCalendarViewDate: LocalDate,
    day: CalendarDay,
    calendarViewModel: CalendarViewModel,
    selectedDate: LocalDate?,
) {
    val isCurrentMonth = day.date.monthValue == currentCalendarViewDate.monthValue
    val isToday = day.date == LocalDate.now()
    val hasEvents = day.events.isNotEmpty()
    val isSelected = day.date == selectedDate

    // --- Define colors based on multiple states ---
    val containerColor =
        when {
            isSelected -> MaterialTheme.colorScheme.primary

            // Filled color for selected day
            else -> Color.Transparent // Default transparent background
        }

    val contentColor =
        when {
            isSelected -> MaterialTheme.colorScheme.onPrimary

            // High-contrast text for selected day
            isToday -> MaterialTheme.colorScheme.primary

            // Special color for today's date
            isCurrentMonth -> MaterialTheme.colorScheme.onSurface

            else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) // Dim for other months
        }

    // Border only shows if there are events AND the day is not selected
    val borderColor =
        if (hasEvents && !isSelected) {
            if (day.events.firstOrNull()?.type == "feast") {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.secondary
            }
        } else {
            Color.Transparent // No border if selected or no events
        }

    Button(
        onClick = {
            calendarViewModel.setDayEvents(day.events, day.date)
            if (!isCurrentMonth) {
                calendarViewModel.loadMonth(day.date.monthValue, day.date.year)
            }
        },
        modifier =
            Modifier
                .weight(1f) // Distribute equally
                .aspectRatio(1f) // Make buttons square
                .padding(2.dp)
                .then(
                    if (hasEvents) {
                        Modifier.border(4.dp, borderColor, CircleShape)
                    } else {
                        Modifier
                    },
                ),
        enabled = hasEvents, // Only enable buttons if there are events
        colors =
            ButtonDefaults.buttonColors(
                containerColor = containerColor,
                contentColor = contentColor,
                disabledContainerColor = Color.Transparent, // Keep disabled days transparent
                disabledContentColor = contentColor,
//            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            ),
        contentPadding = PaddingValues(0.dp), // Remove default padding for better centering
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun DisplayEvents(
    scrollState: ScrollState,
    displayEvents: List<LiturgicalEventDetails>,
    selectedLanguage: AppLanguage,
    navController: NavController,
    calendarViewModel: CalendarViewModel,
    bibleViewModel: BibleViewModel,
) {
    Column(
        Modifier
            .verticalScroll(scrollState)
            .padding(12.dp),
    ) {
        displayEvents.forEach { event ->
            DisplayEvent(
                event,
                selectedLanguage,
                navController,
                calendarViewModel = calendarViewModel,
                bibleViewModel = bibleViewModel,
            )
        }
    }
}

@Composable
fun DisplayEvent(
    event: LiturgicalEventDetails,
    selectedLanguage: AppLanguage,
    navController: NavController,
    modifier: Modifier = Modifier,
    calendarViewModel: CalendarViewModel,
    bibleViewModel: BibleViewModel,
    prayerViewModel: PrayerViewModel = hiltViewModel(),
) {
    val translations by prayerViewModel.translations.collectAsState()
    val textTitle = calendarViewModel.getFormattedDateTitle(event, selectedLanguage)
    Card(
        modifier
            .padding(8.dp)
            .fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            ),
    ) {
        Column(Modifier.padding(8.dp)) {
            Text(textTitle, style = MaterialTheme.typography.titleLarge)
            if (event.bibleReadings != null) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    if (event.bibleReadings.vespersGospel != null) {
                        val text =
                            bibleViewModel.formatGospelEntry(
                                event.bibleReadings.vespersGospel,
                                selectedLanguage,
                            )
                        Text(
                            translations["vespers"] ?: "Vespers",
                            Modifier.padding(start = 4.dp),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Row {
                            Spacer(Modifier.padding(8.dp))
                            TextButton(
                                onClick = {
                                    bibleViewModel.setSelectedBibleReference(event.bibleReadings.vespersGospel)
                                    navController.navigate(Screen.BibleReader.route)
                                },
                            ) {
                                Text(
                                    text,
                                    style = MaterialTheme.typography.bodyLarge,
                                    textDecoration = TextDecoration.Underline,
                                )
                                Icon(
                                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = "Go to Bible Reading",
                                )
                            }
                        }
                    }
                    if (event.bibleReadings.matinsGospel != null) {
                        val text =
                            bibleViewModel.formatGospelEntry(
                                event.bibleReadings.matinsGospel,
                                selectedLanguage,
                            )
                        Text(
                            translations["matins"] ?: "Matins",
                            Modifier.padding(start = 4.dp),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Row {
                            Spacer(Modifier.padding(8.dp))
                            TextButton(
                                onClick = {
                                    bibleViewModel.setSelectedBibleReference(event.bibleReadings.matinsGospel)
                                    navController.navigate(Screen.BibleReader.route)
                                },
                            ) {
                                Text(
                                    text,
                                    style = MaterialTheme.typography.bodyLarge,
                                    textDecoration = TextDecoration.Underline,
                                )
                                Icon(
                                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = "Go to Bible Reading",
                                )
                            }
                        }
                    }
                    if (event.bibleReadings.primeGospel != null) {
                        val text =
                            bibleViewModel.formatGospelEntry(
                                event.bibleReadings.primeGospel,
                                selectedLanguage,
                            )
                        Text(
                            translations["prime"] ?: "Prime",
                            Modifier.padding(start = 4.dp),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Row {
                            Spacer(Modifier.padding(8.dp))
                            TextButton(
                                onClick = {
                                    bibleViewModel.setSelectedBibleReference(event.bibleReadings.primeGospel)
                                    navController.navigate(Screen.BibleReader.route)
                                },
                            ) {
                                Text(
                                    text,
                                    style = MaterialTheme.typography.bodyLarge,
                                    textDecoration = TextDecoration.Underline,
                                )
                                Icon(
                                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = "Go to Bible Reading",
                                )
                            }
                        }
                    }
                    if (event.bibleReadings.oldTestament != null) {
                        Text(
                            translations["oldTestament"] ?: "Before Holy Qurbana",
                            Modifier.padding(start = 4.dp),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        event.bibleReadings.oldTestament.forEach { entry ->
                            val text = bibleViewModel.formatBibleReadingEntry(entry, selectedLanguage)
                            Row {
                                Spacer(Modifier.padding(8.dp))
                                TextButton(
                                    onClick = {
                                        bibleViewModel.setSelectedBibleReference(listOf(entry))
                                        navController.navigate(Screen.BibleReader.route)
                                    },
                                ) {
                                    Text(
                                        text,
                                        style = MaterialTheme.typography.bodyLarge,
                                        textDecoration = TextDecoration.Underline,
                                    )
                                    Icon(
                                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = "Go to Bible Reading",
                                    )
                                }
                            }
                        }
                    }
                    if (event.bibleReadings.gospel != null) {
                        Text(
                            translations["qurbana"] ?: "Holy Qurbana",
                            Modifier.padding(start = 4.dp),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        event.bibleReadings.generalEpistle?.forEach { entry ->
                            val text = bibleViewModel.formatBibleReadingEntry(entry, selectedLanguage)
                            Row {
                                Spacer(Modifier.padding(8.dp))
                                TextButton(
                                    onClick = {
                                        bibleViewModel.setSelectedBibleReference(listOf(entry))
                                        navController.navigate(Screen.BibleReader.route)
                                    },
                                ) {
                                    Text(
                                        text,
                                        style = MaterialTheme.typography.bodyLarge,
                                        textDecoration = TextDecoration.Underline,
                                    )
                                    Icon(
                                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = "Go to Bible Reading",
                                    )
                                }
                            }
                        }
                        event.bibleReadings.paulEpistle?.forEach {  entry ->
                            val text = bibleViewModel.formatBibleReadingEntry(entry, selectedLanguage)
                            Row {
                                Spacer(Modifier.padding(8.dp))
                                TextButton(
                                    onClick = {
                                        bibleViewModel.setSelectedBibleReference(listOf(entry))
                                        navController.navigate(Screen.BibleReader.route)
                                    },
                                ) {
                                    Text(
                                        text,
                                        style = MaterialTheme.typography.bodyLarge,
                                        textDecoration = TextDecoration.Underline,
                                    )
                                    Icon(
                                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = "Go to Bible Reading",
                                    )
                                }
                            }
                        }
                        val text =
                            bibleViewModel.formatGospelEntry(
                                event.bibleReadings.gospel,
                                selectedLanguage,
                            )
                        Row {
                            Spacer(Modifier.padding(8.dp))
                            TextButton(
                                onClick = {
                                    bibleViewModel.setSelectedBibleReference(event.bibleReadings.gospel)
                                    navController.navigate(Screen.BibleReader.route)
                                },
                            ) {
                                Text(
                                    text,
                                    style = MaterialTheme.typography.bodyLarge,
                                    textDecoration = TextDecoration.Underline,
                                )
                                Icon(
                                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = "Go to Bible Reading",
                                )
                            }
                        }
                        if (event.specialSongsKey != null) {
                            val key = event.specialSongsKey.removeSuffix("Songs")
                            TextButton(
                                onClick = {
                                    navController.navigate(Screen.Prayer.createRoute("qurbanaSongs_$key"))
                                },
                            ) {
                                Text(
                                    translations["specialSongs"] ?: key,
                                    Modifier.padding(start = 8.dp),
                                    style = MaterialTheme.typography.bodyLarge,
                                    textDecoration = TextDecoration.Underline,
                                )
                                Icon(
                                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = "Go to Bible Reading",
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}