package com.paradox543.malankaraorthodoxliturgica.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.paradox543.malankaraorthodoxliturgica.data.model.CalendarDay
import com.paradox543.malankaraorthodoxliturgica.data.model.CalendarWeek
import com.paradox543.malankaraorthodoxliturgica.navigation.BottomNavBar
import com.paradox543.malankaraorthodoxliturgica.navigation.TopNavBar
import com.paradox543.malankaraorthodoxliturgica.viewmodel.CalendarViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    navController: NavController,
    calendarViewModel: CalendarViewModel = hiltViewModel(),
) {
    // Collect the StateFlows from the ViewModel
    val monthCalendarData by calendarViewModel.monthCalendarData.collectAsState()
    val currentCalendarViewDate by calendarViewModel.currentCalendarViewDate.collectAsState()
    val isLoading by calendarViewModel.isLoading.collectAsState()
    val error by calendarViewModel.error.collectAsState()

    Scaffold(
        topBar = { TopNavBar("Calendar", navController) },
        bottomBar = { BottomNavBar(navController) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (isLoading) {
                // Show a loading indicator
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (error != null) {
                // Show an error message
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Error: $error",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                        // Add a retry button if appropriate
                        Button(onClick = {
                            calendarViewModel.loadMonth(
                                currentCalendarViewDate.monthValue,
                                currentCalendarViewDate.year
                            )
                        }) {
                            Text("Retry")
                        }
                    }
                }
            } else {
                // Calendar content
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                ) {
                    MonthNavigation(calendarViewModel, currentCalendarViewDate)
                    DayOfWeekHeaders()
                    CalendarGrid(monthCalendarData, currentCalendarViewDate, calendarViewModel)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun MonthNavigation(
    calendarViewModel: CalendarViewModel,
    currentCalendarViewDate: LocalDate
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween, // Space out elements
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = calendarViewModel::goToPreviousMonth, // Connect to ViewModel
            modifier = Modifier.weight(0.15f) // Give it some weight for spacing
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Previous Month"
            )
        }
        Text(
            text = "${
                currentCalendarViewDate.month.getDisplayName(
                    TextStyle.FULL,
                    Locale.getDefault()
                )
            } ${currentCalendarViewDate.year}",
            modifier = Modifier.weight(0.7f), // Give more weight to the month/year text
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
        )
        IconButton(
            onClick = calendarViewModel::goToNextMonth, // Connect to ViewModel
            modifier = Modifier.weight(0.15f) // Give it some weight for spacing
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Next Month" // Corrected content description
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DayOfWeekHeaders() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        val daysOfWeek = listOf(
            DayOfWeek.SUNDAY,
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY
        )
        daysOfWeek.forEach { dayOfWeek ->
            Text(
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun CalendarGrid(
    monthCalendarData: List<CalendarWeek>,
    currentCalendarViewDate: LocalDate,
    calendarViewModel: CalendarViewModel,
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
    ) {
        monthCalendarData.forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                week.days.forEach { day ->
                    // Determine if the day belongs to the current month being viewed
                    DayItem(currentCalendarViewDate, day, calendarViewModel)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun RowScope.DayItem(
    currentCalendarViewDate: LocalDate,
    day: CalendarDay,
    calendarViewModel: CalendarViewModel,
) {
    val isCurrentMonth = day.date.monthValue == currentCalendarViewDate.monthValue
    val textColor = if (isCurrentMonth) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f) // Dim for outside days
    }
    val hasEvents = day.events.isNotEmpty()

    // Determine border color based on hasEvents and if it's the current month
    val borderColor =
        if (hasEvents) Color.Yellow else MaterialTheme.colorScheme.surface // Yellow if events, Gray if no events

    TextButton(
        onClick = {
            if (isCurrentMonth) {
                /* TODO: Handle day click, e.g., show events for this day */
            } else {
                calendarViewModel.loadMonth(day.date.monthValue, day.date.year)
            }
        },
        modifier = Modifier
            .weight(1f) // Distribute equally
            .aspectRatio(1f) // Make buttons square
            .padding(4.dp)
            .then (
                if (hasEvents) {
                    Modifier.border(2.dp, borderColor, CircleShape)
                } else {
                    Modifier
                }
            ),
        enabled = hasEvents // Only enable buttons if there are events
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = day.date.dayOfMonth.toString(),
                color = textColor,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}