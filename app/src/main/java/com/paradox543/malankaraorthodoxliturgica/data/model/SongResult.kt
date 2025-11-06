package com.paradox543.malankaraorthodoxliturgica.data.model

import android.net.Uri

sealed interface SongResult {
    data class Success(
        val uri: Uri,
        val message: String,
    ) : SongResult

    data class Error(
        val message: String,
    ) : SongResult
}