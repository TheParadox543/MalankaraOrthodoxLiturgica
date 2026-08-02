package com.paradox543.malankaraorthodoxliturgica.data.sync.data

import android.content.Context
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.LocalContentStore
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.model.DomainManifest
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.model.RootManifest
import kotlinx.serialization.json.Json
import java.io.File

class AndroidLocalContentStore(
    private val context: Context,
    private val json: Json
) : LocalContentStore {

    private val baseDir = File(context.filesDir, "content")

    init {
        if (!baseDir.exists()) baseDir.mkdirs()
    }

    override suspend fun getRootManifest(): RootManifest? {
        val file = File(baseDir, "root.json")
        return if (file.exists()) json.decodeFromString(file.readText()) else null
    }

    override suspend fun getDomainManifest(domain: String): DomainManifest? {
        val file = File(baseDir, "manifests/$domain.json")
        return if (file.exists()) json.decodeFromString(file.readText()) else null
    }

    override suspend fun saveRootManifest(manifest: RootManifest) {
        val file = File(baseDir, "root.json")
        file.writeText(json.encodeToString(RootManifest.serializer(), manifest))
    }

    override suspend fun saveDomainManifest(manifest: DomainManifest) {
        val dir = File(baseDir, "manifests")
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, "${manifest.domain}.json")
        file.writeText(json.encodeToString(DomainManifest.serializer(), manifest))
    }

    override suspend fun readFile(path: String): String? {
        val file = File(baseDir, "files/$path")
        return if (file.exists()) file.readText() else null
    }

    override suspend fun writeFile(path: String, content: String) {
        val file = File(baseDir, "files/$path")
        file.parentFile?.mkdirs()
        file.writeText(content)
    }

    override suspend fun exists(path: String): Boolean {
        return File(baseDir, "files/$path").exists()
    }
}
