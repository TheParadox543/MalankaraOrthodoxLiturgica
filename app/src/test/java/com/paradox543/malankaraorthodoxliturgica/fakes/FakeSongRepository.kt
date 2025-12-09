package com.paradox543.malankaraorthodoxliturgica.fakes

import android.content.Context
import com.paradox543.malankaraorthodoxliturgica.domain.repository.SongRepository

/**
 * Test fake for [SongRepository].
 */
class FakeSongRepository(
    private val networkAvailable: Boolean = true,
    private val cachedFiles: Set<String> = emptySet(),
    private val songs: Map<String, com.paradox543.malankaraorthodoxliturgica.data.model.SongResult> = emptyMap(),
) : SongRepository {
    override fun isNetworkAvailable(context: Context): Boolean = networkAvailable

    override suspend fun getSong(songFilename: String): com.paradox543.malankaraorthodoxliturgica.data.model.SongResult =
        songs[songFilename] ?: com.paradox543.malankaraorthodoxliturgica.data.model.SongResult
            .Error("Not found")

    override fun isSongCached(songFilename: String): Boolean = cachedFiles.contains(songFilename)
}
