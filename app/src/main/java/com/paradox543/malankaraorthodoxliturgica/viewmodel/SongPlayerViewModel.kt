package com.paradox543.malankaraorthodoxliturgica.viewmodel

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.paradox543.malankaraorthodoxliturgica.data.model.AudioSong
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SongPlayerViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle, // Saved state helps prevent weird behaviour of the app
    val player: Player,
) : ViewModel() {
    // Implementation of player
    private val videoUris = savedStateHandle.getStateFlow("videoUris", emptyList<Uri>())
    val videoItems =
        videoUris
            .map { uris ->
                uris.map { uri ->
                    AudioSong(
                        contentUri = uri,
                        mediaItem = MediaItem.fromUri(uri),
                        title = "No name",
                        filePath = "filesDir",
                    )
                }
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        player.prepare()
    }

    fun addSongUri(uri: Uri) {
        savedStateHandle["videoUris"] = videoUris.value + uri
        player.addMediaItem(MediaItem.fromUri(uri))
    }

    fun playSong(uri: Uri) {
        player.setMediaItem(
            videoItems.value
                .find {
                    it.contentUri == uri
                }?.mediaItem ?: return,
        )
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}