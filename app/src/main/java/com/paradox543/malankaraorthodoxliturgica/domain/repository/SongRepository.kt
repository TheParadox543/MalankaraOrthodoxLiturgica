package com.paradox543.malankaraorthodoxliturgica.domain.repository

import android.content.Context
import com.paradox543.malankaraorthodoxliturgica.domain.model.SongResult

interface SongRepository {
    // Check whether network is available (kept same signature as implementation)
    fun isNetworkAvailable(context: Context): Boolean

    // Fetch a song (either local cached file Uri or remote streaming Uri wrapped in SongResult)
    suspend fun getSong(songFilename: String): SongResult

    // Check if a song file is cached locally
    fun isSongCached(songFilename: String): Boolean
}