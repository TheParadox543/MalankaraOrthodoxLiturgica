package com.paradox543.malankaraorthodoxliturgica.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.paradox543.malankaraorthodoxliturgica.data.model.SongResult
import com.paradox543.malankaraorthodoxliturgica.data.repository.SongRepositoryImpl
import com.paradox543.malankaraorthodoxliturgica.domain.model.MediaStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongPlayerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val songRepositoryImpl: SongRepositoryImpl,
) : ViewModel() {
    // ExoPlayer managed by ViewModel (uses application context to avoid leaking Activity)
    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()

    // Playback state flows for Compose to observe
    private val _isPlaying = MutableStateFlow(exoPlayer.isPlaying)
    val isPlaying = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(exoPlayer.currentPosition)
    val currentPosition = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(exoPlayer.duration)
    val duration = _duration.asStateFlow()

    private val _mediaStatus = MutableStateFlow<MediaStatus>(MediaStatus.Loading)
    val mediaStatus = _mediaStatus.asStateFlow()

    private val _songFilename = MutableStateFlow<String?>(null)
    val songFilename = _songFilename.asStateFlow()

    private val listener =
        object : Player.Listener {
            override fun onIsPlayingChanged(isPlayingValue: Boolean) {
                _isPlaying.value = isPlayingValue
            }

            override fun onEvents(
                player: Player,
                events: Player.Events,
            ) {
                _currentPosition.value = player.currentPosition
                _duration.value = player.duration
            }
        }

    init {
        exoPlayer.addListener(listener)

        viewModelScope.launch {
            while (true) {
                if (_isPlaying.value) {
                    _currentPosition.value = exoPlayer.currentPosition
                }
                delay(500) // update every half second
            }
        }
    }

    fun loadSong(songFilename: String) {
        // Prevent re-loading if already loaded or loading
        if (_mediaStatus.value !is MediaStatus.Loading && _mediaStatus.value !is MediaStatus.Error) {
            if (_songFilename.value != songFilename) {
                _songFilename.value = songFilename
            } else {
                return
            }
        }

        _mediaStatus.value = MediaStatus.Loading
        viewModelScope.launch {
            when (val result = songRepositoryImpl.getSong(songFilename)) {
                is SongResult.Success -> {
                    _songFilename.value = songFilename
                    _mediaStatus.value = MediaStatus.Ready(result.message, result.uri)
                    // Prepare and play the media
                    prepareAndPlayUri(result.uri)
                }

                is SongResult.Error -> {
                    _mediaStatus.value = MediaStatus.Error(result.message)
                }
            }
        }
    }

    private fun prepareAndPlayUri(uri: Uri) {
        exoPlayer.setMediaItem(MediaItem.fromUri(uri))
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    fun play() {
        exoPlayer.play()
    }

    fun pause() {
        exoPlayer.pause()
    }

    // Seek to a new position (in milliseconds)
    fun seekTo(positionMs: Long) {
        exoPlayer.seekTo(positionMs)
        // update the currentPosition flow immediately for snappier UI feedback
        _currentPosition.value = positionMs
    }

    // Expose the player reference if UI needs it (stable reference)
    fun getPlayer(): ExoPlayer = exoPlayer

    override fun onCleared() {
        exoPlayer.removeListener(listener)
        exoPlayer.release()
        super.onCleared()
    }
}
