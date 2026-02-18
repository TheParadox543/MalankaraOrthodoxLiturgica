package com.paradox543.malankaraorthodoxliturgica.domain.song.repository

import com.paradox543.malankaraorthodoxliturgica.domain.song.model.SongResult

interface SongRepository {
    // Fetch a song (either local cached file Uri or remote streaming Uri wrapped in SongResult)
    suspend fun getSong(songFilename: String): SongResult

    // Check if a song file is cached locally
    fun isSongCached(songFilename: String): Boolean
}