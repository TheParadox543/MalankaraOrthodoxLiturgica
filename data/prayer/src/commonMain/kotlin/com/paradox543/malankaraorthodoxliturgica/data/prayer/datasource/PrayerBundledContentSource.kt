package com.paradox543.malankaraorthodoxliturgica.data.prayer.datasource

import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.DomainBundledContentSource
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.model.DomainManifest
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi
import com.paradox543.malankaraorthodoxliturgica.data.prayer.Res

class PrayerBundledContentSource(
    private val json: Json
) : DomainBundledContentSource {
    override val domain: String = "prayers"

    @OptIn(ExperimentalResourceApi::class)
    override suspend fun getManifest(): DomainManifest? {
        return try {
            val bytes = Res.readBytes("files/prayers/manifest.json")
            json.decodeFromString(bytes.decodeToString())
        } catch (e: Exception) {
            null
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    override suspend fun readFile(path: String): String? {
        if (!path.startsWith("prayers/")) return null
        return try {
            val bytes = Res.readBytes("files/$path")
            bytes.decodeToString()
        } catch (e: Exception) {
            null
        }
    }
}
