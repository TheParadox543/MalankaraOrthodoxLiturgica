package com.paradox543.malankaraorthodoxliturgica.view

import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
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
        topBar = { TopNavBar(songFilename.substringAfterLast("/"), navController) },
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
    // 1. Display the song metadata first
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
    Box(
        modifier =
            Modifier
                .fillMaxWidth(0.9f)
                .aspectRatio(1f),
        contentAlignment = Alignment.BottomCenter, // Align controls to the bottom
    ) {
        // The Image serves as the background of the Box
        val transparent = android.R.color.transparent
        Image(
            painter = painterResource(id = R.drawable.cheeranachan),
            contentDescription = "Song Artwork",
            modifier = Modifier.fillMaxSize(), // Make the image fill the entire Box
        )

        // The AndroidView hosts the player controls and is placed on top of the Image
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { context ->
                PlayerView(context).apply {
                    this.player = player
                    useController = true
                    controllerHideOnTouch = false
                    controllerShowTimeoutMs = 0 // Show controls indefinitely

                    setShowNextButton(false)
                    setShowPreviousButton(false)
                    setBackgroundColor(transparent)
                }
            },
        )
    }
}
