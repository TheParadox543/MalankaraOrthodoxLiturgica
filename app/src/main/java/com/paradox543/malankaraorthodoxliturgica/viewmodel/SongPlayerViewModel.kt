package com.paradox543.malankaraorthodoxliturgica.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.paradox543.malankaraorthodoxliturgica.data.model.SongResult
import com.paradox543.malankaraorthodoxliturgica.data.repository.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface MediaStatus {
    object Loading : MediaStatus

    data class Ready(
        val message: String,
    ) : MediaStatus

    data class Error(
        val message: String,
    ) : MediaStatus
}

@HiltViewModel
class SongPlayerViewModel
    @Inject
    constructor(
        private val songRepository: SongRepository,
        val player: Player,
    ) : ViewModel() {
        private val _mediaStatus = MutableStateFlow<MediaStatus>(MediaStatus.Loading)
        val mediaStatus = _mediaStatus.asStateFlow()

        init {
            player.prepare()
            player.playWhenReady = true
        }

        fun loadSong(songFilename: String) {
            viewModelScope.launch {
                _mediaStatus.value = MediaStatus.Loading
                when (val result = songRepository.getSong(songFilename)) {
                    is SongResult.Success -> {
                        player.setMediaItem(MediaItem.fromUri(result.uri))
                        _mediaStatus.value = MediaStatus.Ready(result.message)
                    }
                    is SongResult.Error -> {
                        _mediaStatus.value = MediaStatus.Error(result.message)
                    }
                }
            }
        }

        override fun onCleared() {
            super.onCleared()
            player.release()
        }
    }
