package com.paradox543.malankaraorthodoxliturgica.domain.song.model

sealed interface SongResult {
    data class Success(
        val source: SongSource,
        val message: String,
    ) : SongResult

    data class Error(
        val message: String,
    ) : SongResult
}