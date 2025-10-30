package com.paradox543.malankaraorthodoxliturgica.view

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import java.io.File

@Composable
fun SongScreen() {
    val context = LocalContext.current
    // Create Exoplayer to avoid building on every recomposition
    val exoPlayer = remember { ExoPlayer.Builder(context).build() }

    Column(Modifier.padding(16.dp)) {
        Text("Testing adding songs with firebase")
        // Alternatively, explicitly specify the bucket name URL.
        val storage = Firebase.storage("gs://liturgica-3d3a4.firebasestorage.app")

        var storageRef = storage.reference

        var ekkaraRef = storageRef.child("ekkaraSongs")

        var introductionRef = ekkaraRef.child("introduction/00 Introduction.mp3")

        var rootRef = introductionRef.root
        var parentRef = introductionRef.parent

        Text(storageRef.name)
        Text(ekkaraRef.name)
        Text(introductionRef.name)
        Text(rootRef.name)
        parentRef?.name?.let { Text(it) }

        val fakeFileRef = storageRef.child("fakeFile.txt")
        Text(fakeFileRef.name)
        Log.d("SongScreen", "SongScreen: ${fakeFileRef.name}")
        Log.d("SongScreen", "Path to file: ${fakeFileRef.path}")
        Log.d("SongScreen", "Path to introduction: ${introductionRef.path}")

        Text("Downloading a file")

        val location = File(context.filesDir, "introduction.mp3")
        introductionRef
            .getFile(location)
            .addOnSuccessListener {
                Log.d("SongScreen", "File downloaded to ${location.path}")
                // Data for "images/island.jpg" is returned, use this as needed
            }.addOnFailureListener { e ->
                Log.d("SongScreen", "File download failed", e)
                // Handle any errors
            }

        introductionRef.downloadUrl
            .addOnSuccessListener { uri: Uri ->
                // Create a MediaItem from the obtained URL
                val mediaItem = MediaItem.fromUri(uri)
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
                exoPlayer.playWhenReady = true // It's better to use playWhenReady
                Log.d("SongScreen", "Streaming audio from: $uri")
            }.addOnFailureListener { e ->
                Log.e("SongScreen", "Failed to get download URL", e)
                // Handle the error, e.g., show a message to the user
            }

        // Manage Exoplayer Lifecycle
        DisposableEffect(Unit) {
            onDispose {
                exoPlayer.release()
            }
        }

        AndroidView(
            modifier = Modifier.fillMaxWidth().aspectRatio(16 / 9f),
            factory = {
                PlayerView(it).apply {
                    player = exoPlayer
                }
            },
        )
    }
}