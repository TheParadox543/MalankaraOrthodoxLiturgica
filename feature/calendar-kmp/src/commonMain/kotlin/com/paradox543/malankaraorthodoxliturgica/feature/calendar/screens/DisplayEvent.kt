package com.paradox543.malankaraorthodoxliturgica.feature.calendar.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.composables.icons.materialicons.MaterialIcons
import com.composables.icons.materialicons.rounded.Keyboard_arrow_right
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.LiturgicalEventDetails
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.feature.calendar.viewmodel.CalendarViewModel

@Composable
fun DisplayEvent(
    event: LiturgicalEventDetails,
    selectedLanguage: AppLanguage,
    modifier: Modifier = Modifier,
    calendarViewModel: CalendarViewModel,
    onPrayerNavigate: (String) -> Unit,
    onBibleNavigate: () -> Unit,
) {
    val translations by calendarViewModel.translations.collectAsState()
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
            event.bibleReadings?.let { bibleReadings ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    bibleReadings.vespersGospel?.let  { vespersGospel ->
                        val text =
                            calendarViewModel.formatGospelEntry(
                                vespersGospel,
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
                                    calendarViewModel.setSelectedBibleReference(vespersGospel)
                                    onBibleNavigate()
                                },
                            ) {
                                Text(
                                    text,
                                    style = MaterialTheme.typography.bodyLarge,
                                    textDecoration = TextDecoration.Underline,
                                )
                                Icon(
                                    MaterialIcons.Rounded.Keyboard_arrow_right,
                                    contentDescription = "Go to Bible Reading",
                                )
                            }
                        }
                    }
                    bibleReadings.matinsGospel?.let { matinsGospel ->
                        val text =
                            calendarViewModel.formatGospelEntry(
                                matinsGospel,
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
                                    calendarViewModel.setSelectedBibleReference(matinsGospel)
                                    onBibleNavigate()
                                },
                            ) {
                                Text(
                                    text,
                                    style = MaterialTheme.typography.bodyLarge,
                                    textDecoration = TextDecoration.Underline,
                                )
                                Icon(
                                    MaterialIcons.Rounded.Keyboard_arrow_right,
                                    contentDescription = "Go to Bible Reading",
                                )
                            }
                        }
                    }
                    bibleReadings.primeGospel?.let { primeGospel ->
                        val text =
                            calendarViewModel.formatGospelEntry(
                                primeGospel,
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
                                    calendarViewModel.setSelectedBibleReference(primeGospel)
                                    onBibleNavigate()
                                },
                            ) {
                                Text(
                                    text,
                                    style = MaterialTheme.typography.bodyLarge,
                                    textDecoration = TextDecoration.Underline,
                                )
                                Icon(
                                    MaterialIcons.Rounded.Keyboard_arrow_right,
                                    contentDescription = "Go to Bible Reading",
                                )
                            }
                        }
                    }
                    bibleReadings.oldTestament?.let { oldTestament ->
                        Text(
                            translations["oldTestament"] ?: "Before Holy Qurbana",
                            Modifier.padding(start = 4.dp),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        oldTestament.forEach { entry ->
                            val text = calendarViewModel.formatBibleReadingEntry(entry, selectedLanguage)
                            Row {
                                Spacer(Modifier.padding(8.dp))
                                TextButton(
                                    onClick = {
                                        calendarViewModel.setSelectedBibleReference(listOf(entry))
                                        onBibleNavigate()
                                    },
                                ) {
                                    Text(
                                        text,
                                        style = MaterialTheme.typography.bodyLarge,
                                        textDecoration = TextDecoration.Underline,
                                    )
                                    Icon(
                                        MaterialIcons.Rounded.Keyboard_arrow_right,
                                        contentDescription = "Go to Bible Reading",
                                    )
                                }
                            }
                        }
                    }
                    bibleReadings.gospel?.let { gospel ->
                        Text(
                            translations["qurbana"] ?: "Holy Qurbana",
                            Modifier.padding(start = 4.dp),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        bibleReadings.generalEpistle?.forEach { entry ->
                            val text = calendarViewModel.formatBibleReadingEntry(entry, selectedLanguage)
                            Row {
                                Spacer(Modifier.padding(8.dp))
                                TextButton(
                                    onClick = {
                                        calendarViewModel.setSelectedBibleReference(listOf(entry))
                                        onBibleNavigate()
                                    },
                                ) {
                                    Text(
                                        text,
                                        style = MaterialTheme.typography.bodyLarge,
                                        textDecoration = TextDecoration.Underline,
                                    )
                                    Icon(
                                        MaterialIcons.Rounded.Keyboard_arrow_right,
                                        contentDescription = "Go to Bible Reading",
                                    )
                                }
                            }
                        }
                        bibleReadings.paulEpistle?.forEach {  entry ->
                            val text = calendarViewModel.formatBibleReadingEntry(entry, selectedLanguage)
                            Row {
                                Spacer(Modifier.padding(8.dp))
                                TextButton(
                                    onClick = {
                                        calendarViewModel.setSelectedBibleReference(listOf(entry))
                                        onBibleNavigate()
                                    },
                                ) {
                                    Text(
                                        text,
                                        style = MaterialTheme.typography.bodyLarge,
                                        textDecoration = TextDecoration.Underline,
                                    )
                                    Icon(
                                        MaterialIcons.Rounded.Keyboard_arrow_right,
                                        contentDescription = "Go to Bible Reading",
                                    )
                                }
                            }
                        }
                        val text =
                            calendarViewModel.formatGospelEntry(
                                gospel,
                                selectedLanguage,
                            )
                        Row {
                            Spacer(Modifier.padding(8.dp))
                            TextButton(
                                onClick = {
                                    calendarViewModel.setSelectedBibleReference(gospel)
                                    onBibleNavigate()
                                },
                            ) {
                                Text(
                                    text,
                                    style = MaterialTheme.typography.bodyLarge,
                                    textDecoration = TextDecoration.Underline,
                                )
                                Icon(
                                    MaterialIcons.Rounded.Keyboard_arrow_right,
                                    contentDescription = "Go to Bible Reading",
                                )
                            }
                        }
                        event.specialSongsKey?.let { specialSongsKey ->
                            val key = specialSongsKey.removeSuffix("Songs")
                            TextButton(
                                onClick = {
                                    onPrayerNavigate("qurbanaSongs_$key")
                                },
                            ) {
                                Text(
                                    translations["specialSongs"] ?: key,
                                    Modifier.padding(start = 8.dp),
                                    style = MaterialTheme.typography.bodyLarge,
                                    textDecoration = TextDecoration.Underline,
                                )
                                Icon(
                                    MaterialIcons.Rounded.Keyboard_arrow_right,
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
