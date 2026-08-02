package com.paradox543.malankaraorthodoxliturgica.data.sync.data

import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.NativeRemoteContentSource
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.RemoteContentSource
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.model.DomainManifest
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.model.RootManifest
import com.paradox543.malankaraorthodoxliturgica.logging.AppLogger
import kotlinx.serialization.json.Json

class IOSRemoteContentSource(
    private val nativeSource: NativeRemoteContentSource,
    private val json: Json
) : RemoteContentSource {
    private val tag = "RemoteSource"

    override suspend fun fetchRootManifest(): RootManifest {
        AppLogger.d(tag) { "Fetching remote root manifest (via Native)..." }
        val content = nativeSource.fetchRootManifest()
        return json.decodeFromString(content)
    }

    override suspend fun fetchDomainManifest(path: String): DomainManifest {
        AppLogger.d(tag) { "Fetching remote domain manifest: $path (via Native)" }
        val content = nativeSource.fetchDomainManifest(path)
        return json.decodeFromString(content)
    }

    override suspend fun downloadFile(path: String): String {
        AppLogger.v(tag) { "Downloading remote file: $path (via Native)" }
        return nativeSource.downloadFile(path)
    }
}
