package com.paradox543.malankaraorthodoxliturgica.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
        val mediaUri: Uri,
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
    ) : ViewModel() {
        private val _mediaStatus = MutableStateFlow<MediaStatus>(MediaStatus.Loading)
        val mediaStatus = _mediaStatus.asStateFlow()

        private val _songFilename = MutableStateFlow<String?>(null)
        val songFilename = _songFilename.asStateFlow()

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
                        _mediaStatus.value = MediaStatus.Ready(result.message, result.uri)
                    }
                    is SongResult.Error -> {
                        _mediaStatus.value = MediaStatus.Error(result.message)
                    }
                }
            }
        }
    }
