package com.paradox543.malankaraorthodoxliturgica.view

import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.paradox543.malankaraorthodoxliturgica.R
import com.paradox543.malankaraorthodoxliturgica.navigation.BottomNavBar
import com.paradox543.malankaraorthodoxliturgica.navigation.TopNavBar
import com.paradox543.malankaraorthodoxliturgica.viewmodel.MediaStatus
import com.paradox543.malankaraorthodoxliturgica.viewmodel.SongPlayerViewModel

@OptIn(UnstableApi::class)
@Composable
fun SongScreen(
    navController: NavController,
    songPlayerViewModel: SongPlayerViewModel = hiltViewModel(),
    songFilename: String = "introduction/00 Introduction.mp3",
) {
    val mediaStatus by songPlayerViewModel.mediaStatus.collectAsState()
    val context = LocalContext.current

    val exoPlayer = remember { ExoPlayer.Builder(context).build() }
    LaunchedEffect(mediaStatus) {
        if (mediaStatus is MediaStatus.Ready) {
            val readyStatus = mediaStatus as MediaStatus.Ready
            val mediaItem = MediaItem.fromUri(readyStatus.mediaUri)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        }
    }

    // 3. This effect ensures the player is released when the screen is disposed
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    LaunchedEffect(songFilename) {
        songPlayerViewModel.loadSong(songFilename)
    }

    Scaffold(
        topBar = { TopNavBar(songFilename.substringAfterLast("/").removeSuffix(".mp3"), navController) },
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
                        player = exoPlayer, // Pass the local player
                        title = songFilename.substringAfterLast('/').removeSuffix(".mp3"),
                        sourceMessage = status.message,
                    )
                }

                is MediaStatus.Error -> {
                    Text("Error: ${status.message}")
                    Text("Failed to retrieve song: $songFilename")
                }
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun SongPlayerUI(
    player: ExoPlayer,
    title: String,
    sourceMessage: String,
) {
    var isPlaying by remember { mutableStateOf(player.isPlaying) }
    var currentPosition by remember { mutableLongStateOf(player.currentPosition) }
    var totalDuration by remember { mutableLongStateOf(player.duration) }

    // Use a DisposableEffect to add and remove a listener to the player.
    DisposableEffect(player) {
        val listener =
            object : Player.Listener {
                override fun onIsPlayingChanged(isPlayingValue: Boolean) {
                    super.onIsPlayingChanged(isPlayingValue)
                    isPlaying = isPlayingValue
                }

                override fun onEvents(
                    player: Player,
                    events: Player.Events,
                ) {
                    super.onEvents(player, events)
                    // Update position and duration whenever player state changes
                    currentPosition = player.currentPosition
                    totalDuration = player.duration
                }
            }

        player.addListener(listener)

        // When the composable is disposed, remove the listener
        onDispose {
            player.removeListener(listener)
        }
    }
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Previous Button
            IconButton(onClick = { player.seekToPreviousMediaItem() }) {
                Icon(
                    Icons.Default.KeyboardArrowLeft,
                    "Previous Song",
                    modifier = Modifier.size(48.dp),
                )
            }

            // Play/Pause Button
            IconButton(onClick = {
                if (player.isPlaying) {
                    player.pause()
                } else {
                    player.play()
                }
            }) {
                if (isPlaying) {
                    Icon(painterResource(R.drawable.pause_24px), "Pause Song",)
                } else {
                    Icon(Icons.Default.PlayArrow, "Play Song")
                }
            }

            // Next Button
            IconButton(onClick = { player.seekToNextMediaItem() }) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    "Next Song",
                    modifier = Modifier.size(48.dp),
                )
            }
        }
    }
}
