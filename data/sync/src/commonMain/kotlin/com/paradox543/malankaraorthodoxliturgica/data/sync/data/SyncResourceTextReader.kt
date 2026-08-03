package com.paradox543.malankaraorthodoxliturgica.data.sync.data

import com.paradox543.malankaraorthodoxliturgica.data.core.datasource.ResourceTextReader
import com.paradox543.malankaraorthodoxliturgica.data.core.exceptions.AssetReadException
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.ContentResolver

class SyncResourceTextReader(
    private val contentResolver: ContentResolver
) : ResourceTextReader {
    override suspend fun readText(path: String): String {
        return contentResolver.resolveFile(path) 
            ?: throw AssetReadException("Failed to resolve file at path: $path")
    }
}
