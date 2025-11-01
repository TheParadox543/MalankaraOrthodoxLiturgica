package com.paradox543.malankaraorthodoxliturgica.view

import android.net.Uri
import android.util.Log
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import com.paradox543.malankaraorthodoxliturgica.viewmodel.SongPlayerViewModel
import java.io.File

sealed interface MediaStatus {
    object Loading : MediaStatus

    data class Ready(
        val message: String,
    ) : MediaStatus

    data class Error(
        val message: String,
    ) : MediaStatus
}

@OptIn(UnstableApi::class)
@Composable
fun SongScreen(
    navController: NavController,
    songPlayerViewModel: SongPlayerViewModel = hiltViewModel(),
    songFilename: String = "introduction/00 Introduction.mp3",
) {
    val context = LocalContext.current
    // Create Exoplayer to avoid building on every recomposition
    val exoPlayer = remember { ExoPlayer.Builder(context).build() }

    // State to manage the current status: loading, playing, or error.
    var mediaStatus by remember { mutableStateOf<MediaStatus>(MediaStatus.Loading) }

//    val songFileName = "00 Introduction.mp3"
    val localFile = remember { File(context.filesDir, songFilename) }

    LaunchedEffect(key1 = localFile) {
        // Define the reference to the file in Firebase Storage
        val storageRef =
            Firebase
                .storage("gs://liturgica-3d3a4.firebasestorage.app")
                .reference
                .child("ekkaraSongs/$songFilename")

        if (localFile.exists() && localFile.length() > 0) {
            // 1. FILE EXISTS LOCALLY
            Log.d("SongScreen", "File exists locally. Playing from: ${localFile.path}")
            val localUri = localFile.toUri()
            val mediaItem = MediaItem.fromUri(localUri)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            mediaStatus = MediaStatus.Ready("Playing from local storage")
        } else {
            // 2. FILE DOES NOT EXIST, STREAM FROM URL
            Log.d("SongScreen", "File does not exist. Streaming from Firebase.")
            storageRef.downloadUrl
                .addOnSuccessListener { uri: Uri ->
                    // Set up the player to stream from the URL
                    val mediaItem = MediaItem.fromUri(uri)
                    exoPlayer.setMediaItem(mediaItem)
                    exoPlayer.prepare()
                    mediaStatus = MediaStatus.Ready("Streaming from the cloud")
                    Log.d("SongScreen", "Streaming audio from: $uri")

                    // OPTIONAL: Download the file in the background for next time
                    storageRef
                        .getFile(localFile)
                        .addOnSuccessListener {
                            Log.d(
                                "SongScreen",
                                "Background download complete. File saved to ${localFile.path}",
                            )
                        }.addOnFailureListener { e ->
                            Log.e("SongScreen", "Background download failed", e)
                            // Optional: Clean up partially downloaded file
                            if (localFile.exists()) {
                                localFile.delete()
                            }
                        }
                }.addOnFailureListener { e ->
                    // Handle failure to get the download URL
                    Log.e("SongScreen", "Failed to get download URL", e)
                    mediaStatus = MediaStatus.Error("Could not retrieve song.")
                }
        }
    }

    // Start playback once the media is ready
    if (mediaStatus is MediaStatus.Ready) {
        exoPlayer.playWhenReady = true
    }

    // Manage Exoplayer Lifecycle with DisposableEffect
    // This is crucial to release the player's resources when the screen is left.
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
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
                            this.player = exoPlayer
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