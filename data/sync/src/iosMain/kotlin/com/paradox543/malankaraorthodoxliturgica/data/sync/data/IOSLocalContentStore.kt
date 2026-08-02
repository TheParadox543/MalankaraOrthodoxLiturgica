package com.paradox543.malankaraorthodoxliturgica.data.sync.data

import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.LocalContentStore
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.model.DomainManifest
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.model.RootManifest
import com.paradox543.malankaraorthodoxliturgica.logging.AppLogger
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.serialization.json.Json
import platform.Foundation.*

@OptIn(ExperimentalForeignApi::class)
class IOSLocalContentStore(
    private val json: Json
) : LocalContentStore {
    private val tag = "LocalStore"
    private val fileManager = NSFileManager.defaultManager

    private val baseDir: String by lazy {
        val paths = fileManager.URLsForDirectory(NSApplicationSupportDirectory, NSUserDomainMask)
        val supportURL = paths.first() as NSURL
        val supportDir = supportURL.path!!
        val contentDir = "$supportDir/content"
        
        if (!fileManager.fileExistsAtPath(contentDir)) {
            fileManager.createDirectoryAtPath(contentDir, true, null, null)
        }
        contentDir
    }

    override suspend fun getRootManifest(): RootManifest? {
        val path = "$baseDir/manifest.json"
        if (!fileManager.fileExistsAtPath(path)) {
            AppLogger.d(tag) { "No local root manifest found at $path" }
            return null
        }
        return try {
            val content = NSString.stringWithContentsOfFile(path, NSUTF8StringEncoding, null) ?: return null
            val manifest = json.decodeFromString<RootManifest>(content)
            AppLogger.d(tag) { "Loaded local root manifest: schema v${manifest.schemaVersion}" }
            manifest
        } catch (e: Exception) {
            AppLogger.e(tag, e) { "Failed to decode local root manifest" }
            null
        }
    }

    override suspend fun getDomainManifest(domain: String): DomainManifest? {
        val path = "$baseDir/manifests/$domain.json"
        if (!fileManager.fileExistsAtPath(path)) {
            AppLogger.v(tag) { "No local domain manifest found for $domain." }
            return null
        }
        return try {
            val content = NSString.stringWithContentsOfFile(path, NSUTF8StringEncoding, null) ?: return null
            val manifest = json.decodeFromString<DomainManifest>(content)
            AppLogger.v(tag) { "Loaded local domain manifest for $domain: v${manifest.contentVersion}" }
            manifest
        } catch (e: Exception) {
            AppLogger.e(tag, e) { "Failed to decode local domain manifest for $domain" }
            null
        }
    }

    override suspend fun saveRootManifest(manifest: RootManifest) {
        val path = "$baseDir/manifest.json"
        val content = json.encodeToString(RootManifest.serializer(), manifest)
        saveToFile(path, content)
        AppLogger.i(tag) { "Saved local root manifest." }
    }

    override suspend fun saveDomainManifest(manifest: DomainManifest) {
        val dir = "$baseDir/manifests"
        if (!fileManager.fileExistsAtPath(dir)) {
            fileManager.createDirectoryAtPath(dir, true, null, null)
        }
        val path = "$dir/${manifest.domain}.json"
        val content = json.encodeToString(DomainManifest.serializer(), manifest)
        saveToFile(path, content)
        AppLogger.i(tag) { "Saved local domain manifest for ${manifest.domain}." }
    }

    override suspend fun readFile(path: String): String? {
        val fullPath = "$baseDir/files/$path"
        if (!fileManager.fileExistsAtPath(fullPath)) return null
        AppLogger.v(tag) { "Read file from local store: $path" }
        return NSString.stringWithContentsOfFile(fullPath, NSUTF8StringEncoding, null)
    }

    override suspend fun writeFile(path: String, content: String) {
        val fullPath = "$baseDir/files/$path"
        val dir = fullPath.substringBeforeLast("/")
        if (!fileManager.fileExistsAtPath(dir)) {
            fileManager.createDirectoryAtPath(dir, true, null, null)
        }
        saveToFile(fullPath, content)
        AppLogger.d(tag) { "Wrote file to local store: $path" }
    }

    override suspend fun exists(path: String): Boolean {
        return fileManager.fileExistsAtPath("$baseDir/files/$path")
    }

    override suspend fun getLastSyncTime(): Long {
        val path = "$baseDir/last_sync.txt"
        if (!fileManager.fileExistsAtPath(path)) return 0L
        val content = NSString.stringWithContentsOfFile(path, NSUTF8StringEncoding, null) ?: return 0L
        return content.toLongOrNull() ?: 0L
    }

    override suspend fun saveLastSyncTime(time: Long) {
        val path = "$baseDir/last_sync.txt"
        saveToFile(path, time.toString())
    }

    private fun saveToFile(path: String, content: String) {
        @Suppress("CAST_NEVER_SUCCEEDS")
        (content as NSString).writeToFile(path, true, NSUTF8StringEncoding, null)
    }
}
