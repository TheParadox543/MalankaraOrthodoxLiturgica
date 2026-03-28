package com.paradox543.malankaraorthodoxliturgica.feature.song.viewmodel

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.settings.repository.SettingsRepository
import com.paradox543.malankaraorthodoxliturgica.domain.song.model.SongResult
import com.paradox543.malankaraorthodoxliturgica.domain.song.repository.SongRepository
import com.paradox543.malankaraorthodoxliturgica.domain.translations.repository.TranslationsRepository
import com.paradox543.malankaraorthodoxliturgica.domain.translations.repository.loadTranslations
import com.paradox543.malankaraorthodoxliturgica.feature.song.model.MediaStatus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SongPlayerViewModel(
    private val context: Context,
    private val songRepository: SongRepository,
    private val settingsRepository: SettingsRepository,
    private val translationsRepository: TranslationsRepository,
    private val backgroundDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : ViewModel() {
    val selectedLanguage: StateFlow<AppLanguage> =
        settingsRepository.language.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5000),
            initialValue = AppLanguage.MALAYALAM,
        )

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

    private val _translations = MutableStateFlow<Map<String, String>>(emptyMap())
    val translations: StateFlow<Map<String, String>> = _translations.asStateFlow()

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
            while (isActive) {
                if (_isPlaying.value) {
                    _currentPosition.value = exoPlayer.currentPosition
                }
                delay(500) // update every half second
            }
        }
        viewModelScope.launch {
            selectedLanguage.collectLatest { language ->
                // When the language changes (from DataStore), load translations
                loadTranslations(language)
            }
        }
    }

    private suspend fun loadTranslations(language: AppLanguage) {
        val loadedTranslations = translationsRepository.loadTranslations(language, backgroundDispatcher)
        _translations.update { loadedTranslations }
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
            when (val result = songRepository.getSong(songFilename)) {
                is SongResult.Success -> {
                    _songFilename.value = songFilename
                    _mediaStatus.value = MediaStatus.Ready(result.message, result.source.source.toUri())
                    // Prepare and play the media
                    prepareAndPlayUri(result.source.source.toUri())
                }

                is SongResult.Error -> {
                    _mediaStatus.value = MediaStatus.Error(result.message)
                }
            }
        }
    }

    private suspend fun prepareAndPlayUri(uri: Uri) =
        withContext(Dispatchers.Main) {
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