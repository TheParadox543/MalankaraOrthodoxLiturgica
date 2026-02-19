package com.paradox543.malankaraorthodoxliturgica.data.mapping

import androidx.core.net.toUri
import com.paradox543.malankaraorthodoxliturgica.data.model.SongResultDto
import com.paradox543.malankaraorthodoxliturgica.domain.song.model.SongResult
import com.paradox543.malankaraorthodoxliturgica.domain.song.model.SongSource

fun SongResultDto.toSongResultDomain() =
    when (this) {
        is SongResultDto.Error -> {
            SongResult.Error(
                message = this.message,
            )
        }

        is SongResultDto.Success -> {
            SongResult.Success(
                source =
                    SongSource(
                        source = this.uri.toString(),
                    ),
                message = this.message,
            )
        }
    }

fun SongResult.toData() =
    when (this) {
        is SongResult.Success -> {
            SongResultDto.Success(
                uri = this.source.source.toUri(),
                message = this.message,
            )
        }

        is SongResult.Error -> {
            SongResultDto.Error(
                message = this.message,
            )
        }
    }
