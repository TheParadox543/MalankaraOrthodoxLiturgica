package com.paradox543.malankaraorthodoxliturgica.domain.fakes

import com.paradox543.malankaraorthodoxliturgica.domain.song.model.SongResult
import com.paradox543.malankaraorthodoxliturgica.domain.song.repository.SongRepository

/**
 * Test fake for [SongRepository].
 */
class FakeSongRepository(
    private val networkAvailable: Boolean = true,
    private val cachedFiles: Set<String> = emptySet(),
    private val songs: Map<String, SongResult> = emptyMap(),
) : SongRepository {
    override suspend fun getSong(songFilename: String): SongResult =
        songs[songFilename] ?: SongResult
            .Error("Not found")

    override fun isSongCached(songFilename: String): Boolean = cachedFiles.contains(songFilename)
}
