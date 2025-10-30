package com.paradox543.malankaraorthodoxliturgica.data.model

import android.net.Uri
import androidx.media3.common.MediaItem

data class AudioSong(
    val title: String,
    val filePath: String,
    val mediaItem: MediaItem? = null,
    val contentUri: Uri? = null,
)
