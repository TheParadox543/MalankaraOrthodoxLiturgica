package com.paradox543.malankaraorthodoxliturgica.data.model

import android.net.Uri

sealed interface SongResultDto {
    data class Success(
        val uri: Uri,
        val message: String,
    ) : SongResultDto

    data class Error(
        val message: String,
    ) : SongResultDto
}