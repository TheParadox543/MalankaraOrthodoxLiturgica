package com.paradox543.malankaraorthodoxliturgica.data.sync.data

import com.google.firebase.storage.FirebaseStorage
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.RemoteContentSource
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.model.DomainManifest
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.model.RootManifest
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.Json

class FirebaseRemoteContentSource(
    private val storage: FirebaseStorage,
    private val json: Json
) : RemoteContentSource {

    override suspend fun fetchRootManifest(): RootManifest {
        val bytes = storage.reference.child("content/root.json").getBytes(Long.MAX_VALUE).await()
        return json.decodeFromString(bytes.decodeToString())
    }

    override suspend fun fetchDomainManifest(path: String): DomainManifest {
        val bytes = storage.reference.child(path).getBytes(Long.MAX_VALUE).await()
        return json.decodeFromString(bytes.decodeToString())
    }

    override suspend fun downloadFile(path: String): String {
        val bytes = storage.reference.child("content/files/$path").getBytes(Long.MAX_VALUE).await()
        return bytes.decodeToString()
    }
}
