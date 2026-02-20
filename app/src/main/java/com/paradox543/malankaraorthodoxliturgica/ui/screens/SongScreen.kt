package com.paradox543.malankaraorthodoxliturgica.ui.screens

import androidx.annotation.OptIn
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.paradox543.malankaraorthodoxliturgica.BuildConfig
import com.paradox543.malankaraorthodoxliturgica.R
import com.paradox543.malankaraorthodoxliturgica.ui.MediaStatus
import com.paradox543.malankaraorthodoxliturgica.ui.components.BottomNavBar
import com.paradox543.malankaraorthodoxliturgica.ui.components.TopNavBar
import com.paradox543.malankaraorthodoxliturgica.ui.viewmodel.PrayerViewModel
import com.paradox543.malankaraorthodoxliturgica.ui.viewmodel.SongPlayerViewModel

@OptIn(UnstableApi::class)
@Composable
fun SongScreen(
    navController: NavController,
    songPlayerViewModel: SongPlayerViewModel = hiltViewModel(),
    prayerViewModel: PrayerViewModel = hiltViewModel(),
    songFilename: String,
) {
    val mediaStatus by songPlayerViewModel.mediaStatus.collectAsState()
    val isPlaying by songPlayerViewModel.isPlaying.collectAsState()
    val currentPosition by songPlayerViewModel.currentPosition.collectAsState()
    val duration by songPlayerViewModel.duration.collectAsState()
    val translations by prayerViewModel.translations.collectAsState()

    var title = ""
    for (part in songFilename.substringAfter("/").removeSuffix(".mp3").split("/")) {
        title +=
            if (part.contains("ragam")) {
                translations["ragam"] + " " + part.substringAfter("ragam")
            } else {
                (translations[part] ?: part) + " "
            }
    }

    LaunchedEffect(songFilename) {
        songPlayerViewModel.loadSong(songFilename)
    }

    Scaffold(
        topBar = { TopNavBar(title, navController) },
        bottomBar = { BottomNavBar(navController) },
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Orthodox Liturgical Songs")
            Spacer(Modifier.height(24.dp))

            // Show a loading indicator, the player, or an error message
            when (val status = mediaStatus) {
                is MediaStatus.Loading -> {
                    CircularProgressIndicator()
                    Text("Loading song...", modifier = Modifier.padding(top = 8.dp))
                }

                is MediaStatus.Ready -> {
                    SongPlayerUI(
                        isPlaying = isPlaying,
                        onTogglePlay = {
                            if (isPlaying) songPlayerViewModel.pause() else songPlayerViewModel.play()
                        },
                        title = title,
                        sourceMessage = status.message,
                        currentPosition = currentPosition,
                        duration = duration,
                        onSeek = { newPos -> songPlayerViewModel.seekTo(newPos) },
                    )
                }

                is MediaStatus.Error -> {
                    Text("Error: ${status.message}")
                    if (BuildConfig.DEBUG) Text("Failed to retrieve song: $songFilename")
                }
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun SongPlayerUI(
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    title: String,
    sourceMessage: String,
    currentPosition: Long,
    duration: Long,
    onSeek: (Long) -> Unit,
) {
    // Local UI state to manage slider dragging without fighting the frequent player updates
    var sliderPosition by remember { mutableFloatStateOf(0f) }
    var isUserSeeking by remember { mutableStateOf(false) }

    // Convert positions to Float ranges between 0f and 1f
    val effectiveDuration = if (duration > 0L) duration else 1L

    // Update the local sliderPosition from currentPosition when the user is NOT interacting.
    LaunchedEffect(currentPosition, isUserSeeking, effectiveDuration) {
        if (!isUserSeeking) {
            sliderPosition = (currentPosition.coerceIn(0L, effectiveDuration)).toFloat() / effectiveDuration.toFloat()
        }
    }

    // Use derivedStateOf to avoid unnecessary recompositions and animate for smooth movement
    val targetProgress =
        remember(isUserSeeking, sliderPosition) {
            derivedStateOf { sliderPosition } // sliderPosition is updated from player or user drag
        }
    val animatedProgress by animateFloatAsState(targetProgress.value)

    Text(
        text = title,
        style = MaterialTheme.typography.headlineMedium,
        textAlign = TextAlign.Center,
    )
    Text(
        text = sourceMessage,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = 24.dp),
    )

    // 2. Use a Box to overlay the player controls on top of the image
    Column(
        modifier = Modifier.fillMaxWidth(0.9f),
        verticalArrangement = Arrangement.Bottom, // Align controls to the bottom
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // The Image serves as the background of the Box
        Image(
            painter = painterResource(id = R.drawable.cheeranachan),
            contentDescription = "Song Artwork",
            modifier =
                Modifier
                    .fillMaxWidth(0.9f)
                    .aspectRatio(1f),
        )
        Slider(
            value = animatedProgress,
            onValueChange = { newFraction ->
                isUserSeeking = true
                sliderPosition = newFraction
            },
            onValueChangeFinished = {
                isUserSeeking = false
                val seekMs = (sliderPosition * effectiveDuration).toLong()
                onSeek(seekMs)
            },
            modifier =
                Modifier
                    .fillMaxWidth(0.9f)
                    .padding(bottom = 8.dp),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Play/Pause Button
            IconButton(onClick = onTogglePlay) {
                if (isPlaying) {
                    Icon(painterResource(R.drawable.pause_24px), "Pause Song")
                } else {
                    Icon(Icons.Default.PlayArrow, "Play Song")
                }
            }
        }
    }
}
