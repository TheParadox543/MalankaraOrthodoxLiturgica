package com.paradox543.malankaraorthodoxliturgica.view

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
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

    LaunchedEffect(key1 = songFilename) {
        songPlayerViewModel.loadSong(songFilename)
    }

    Column(
        Modifier
            .fillMaxWidth()
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
                Text(status.message)
                Spacer(Modifier.height(8.dp))
                // The AndroidView hosts the ExoPlayer UI
                AndroidView(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
                    factory = { context ->
                        PlayerView(context).apply {
                            this.player = songPlayerViewModel.player
                            // Key improvement: Hide the video surface and only show controls
                            useController = true
                            controllerShowTimeoutMs = 0 // Show controls immediately and indefinitely
                            controllerHideOnTouch = false
                        }
                    },
                )
            }
            is MediaStatus.Error -> {
                Text("Error: ${status.message}")
            }
        }
    }
}
