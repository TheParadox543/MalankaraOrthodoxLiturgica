package com.paradox543.malankaraorthodoxliturgica.data.sync.data

import android.content.Context
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.LocalContentStore
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.model.DomainManifest
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.model.RootManifest
import com.paradox543.malankaraorthodoxliturgica.logging.AppLogger
import kotlinx.serialization.json.Json
import java.io.File

class AndroidLocalContentStore(
    private val context: Context,
    private val json: Json
) : LocalContentStore {
    private val tag = "LocalStore"
    private val baseDir = File(context.filesDir, "content")

    init {
        if (!baseDir.exists()) baseDir.mkdirs()
    }

    override suspend fun getRootManifest(): RootManifest? {
        val file = File(baseDir, "manifest.json")
        return if (file.exists()) {
            try {
                val manifest = json.decodeFromString<RootManifest>(file.readText())
                AppLogger.d(tag) { "Loaded local root manifest: schema v${manifest.schemaVersion}" }
                manifest
            } catch (e: Exception) {
                AppLogger.e(tag, e) { "Failed to decode local root manifest" }
                null
            }
        } else {
            AppLogger.d(tag) { "No local root manifest found." }
            null
        }
    }

    override suspend fun getDomainManifest(domain: String): DomainManifest? {
        val file = File(baseDir, "manifests/$domain.json")
        return if (file.exists()) {
            try {
                val manifest = json.decodeFromString<DomainManifest>(file.readText())
                AppLogger.v(tag) { "Loaded local domain manifest for $domain: v${manifest.contentVersion}" }
                manifest
            } catch (e: Exception) {
                AppLogger.e(tag, e) { "Failed to decode local domain manifest for $domain" }
                null
            }
        } else {
            AppLogger.v(tag) { "No local domain manifest found for $domain." }
            null
        }
    }

    override suspend fun saveRootManifest(manifest: RootManifest) {
        AppLogger.i(tag) { "Saving local root manifest: schema v${manifest.schemaVersion}" }
        val file = File(baseDir, "manifest.json")
        file.writeText(json.encodeToString(RootManifest.serializer(), manifest))
    }

    override suspend fun saveDomainManifest(manifest: DomainManifest) {
        AppLogger.i(tag) { "Saving local domain manifest for ${manifest.domain}: v${manifest.contentVersion}" }
        val dir = File(baseDir, "manifests")
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, "${manifest.domain}.json")
        file.writeText(json.encodeToString(DomainManifest.serializer(), manifest))
    }

    override suspend fun readFile(path: String): String? {
        val file = File(baseDir, "files/$path")
        return if (file.exists()) {
            AppLogger.v(tag) { "Read file from local store: $path" }
            file.readText()
        } else null
    }

    override suspend fun writeFile(path: String, content: String) {
        AppLogger.d(tag) { "Writing file to local store: $path" }
        val file = File(baseDir, "files/$path")
        file.parentFile?.mkdirs()
        file.writeText(content)
    }

    override suspend fun exists(path: String): Boolean {
        return File(baseDir, "files/$path").exists()
    }

    override suspend fun getLastSyncTime(): Long {
        val file = File(baseDir, "last_sync.txt")
        return if (file.exists()) file.readText().toLongOrNull() ?: 0L else 0L
    }

    override suspend fun saveLastSyncTime(time: Long) {
        val file = File(baseDir, "last_sync.txt")
        file.writeText(time.toString())
    }
}
