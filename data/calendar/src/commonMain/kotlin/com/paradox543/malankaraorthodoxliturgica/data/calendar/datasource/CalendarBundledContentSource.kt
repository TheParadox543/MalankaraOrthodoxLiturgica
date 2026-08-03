package com.paradox543.malankaraorthodoxliturgica.data.calendar.datasource

import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.DomainBundledContentSource
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.model.DomainManifest
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi
import com.paradox543.malankaraorthodoxliturgica.data.calendar.Res

class CalendarBundledContentSource(
    private val json: Json
) : DomainBundledContentSource {
    override val domain: String = "calendar"

    @OptIn(ExperimentalResourceApi::class)
    override suspend fun getManifest(): DomainManifest? {
        return try {
            val bytes = Res.readBytes("files/calendar/manifest.json")
            json.decodeFromString(bytes.decodeToString())
        } catch (e: Exception) {
            null
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    override suspend fun readFile(path: String): String? {
        if (!path.startsWith("calendar/")) return null
        return try {
            val bytes = Res.readBytes("files/$path")
            bytes.decodeToString()
        } catch (e: Exception) {
            null
        }
    }
}
