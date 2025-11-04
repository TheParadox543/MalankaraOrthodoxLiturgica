package com.paradox543.malankaraorthodoxliturgica.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import java.io.File
import javax.inject.Inject

class SongRepository
@Inject
constructor(
    @ApplicationContext private val context: Context,
) {
    suspend fun getSong(songFilename: String): SongResult {
        val localFile = File(context.filesDir, songFilename)
        val storageRef =
            Firebase
                .storage("gs://liturgica-3d3a4.firebasestorage.app")
                .reference
                .child("ekkaraSongs/$songFilename")

        return if (localFile.exists() && localFile.length() > 0) {
            Log.d("SongRepository", "File exists locally. Playing from: ${localFile.path}")
            SongResult.Success(localFile.toUri(), "Playing from local storage")
        } else {
            Log.d("SongRepository", "File does not exist. Streaming from Firebase.")
            try {
                val uri = storageRef.downloadUrl.await()
                // Download the file in the background for next time
                storageRef
                    .getFile(localFile)
                    .addOnSuccessListener {
                        Log.d(
                            "SongScreen",
                            "Background download complete. File saved to ${localFile.path}",
                        )
                    }.addOnFailureListener { e ->
                        Log.e("SongScreen", "Background download failed", e)
                        // Optional: Clean up partially downloaded file
                        if (localFile.exists()) {
                            localFile.delete()
                        }
                    }
                SongResult.Success(uri, "Streaming from the cloud")
            } catch (e: Exception) {
                Log.e("SongRepository", "Failed to get download URL", e)
                SongResult.Error("Could not retrieve song.")
            }
        }
    }
}

sealed interface SongResult {
    data class Success(
        val uri: Uri,
        val message: String,
    ) : SongResult

    data class Error(
        val message: String,
    ) : SongResult
}
