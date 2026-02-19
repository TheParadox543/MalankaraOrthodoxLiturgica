package com.paradox543.malankaraorthodoxliturgica.data.repositoryImpl

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.storage.FirebaseStorage
import com.paradox543.malankaraorthodoxliturgica.data.bible.mapping.toDomain
import com.paradox543.malankaraorthodoxliturgica.data.mapping.toSongResultDomain
import com.paradox543.malankaraorthodoxliturgica.data.model.SongResultDto
import com.paradox543.malankaraorthodoxliturgica.domain.song.model.SongResult
import com.paradox543.malankaraorthodoxliturgica.domain.song.repository.SongRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.io.File
import javax.inject.Inject

class
SongRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val storage: FirebaseStorage,
) : SongRepository {
    fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override suspend fun getSong(songFilename: String): SongResult =
        withContext(Dispatchers.IO) {
            val localFile = File(context.filesDir, songFilename)

            if (localFile.exists() && localFile.length() > 0) {
                Log.d("SongRepository", "Playing from local storage: ${localFile.path}")
                return@withContext SongResultDto.Success(localFile.toUri(), "Playing from local storage").toSongResultDomain()
            }

            if (!isNetworkAvailable(context)) {
                return@withContext SongResultDto.Error("No internet connection. Please try again later.").toSongResultDomain()
            }

            Log.d("SongRepository", "Streaming from Firebase for $songFilename")
            try {
                val ref = storage.reference.child(songFilename)
                val uri = withTimeout(10000) { ref.downloadUrl.await() }

                // Trigger background download (non-blocking)
                downloadSongInBackground(songFilename, localFile)

                SongResultDto.Success(uri, "Streaming from Firebase").toSongResultDomain()
            } catch (e: TimeoutCancellationException) {
                Log.e("SongRepository", "Firebase request timed out", e)
                SongResultDto.Error("Request timed out. Please check your internet connection.").toSongResultDomain()
            } catch (e: Exception) {
                Log.e("SongRepository", "Failed to fetch song: $songFilename", e)
                SongResultDto.Error("Could not retrieve song: ${e.localizedMessage}").toSongResultDomain()
            }
        }

    private suspend fun downloadSongInBackground(
        songFilename: String,
        localFile: File,
    ) {
        // Ensure that directories exist within the device.
        localFile.parentFile?.mkdirs()

        val ref = storage.reference.child(songFilename)
        if (!isNetworkAvailable(context)) {
            Log.w("SongRepository", "No internet. Skipping background download")
            return
        }
        try {
            withTimeout(10000) {
                ref
                    .getFile(localFile)
                    .addOnSuccessListener {
                        Log.d("SongRepository", "Cached song for offline use: ${localFile.path}")
                    }.addOnFailureListener { e ->
                        Log.e("SongRepository", "Background download failed for $songFilename", e)
                        if (localFile.exists()) localFile.delete()
                    }
            }
        } catch (e: TimeoutCancellationException) {
            Log.e("SongRepository", "Download timed out for $songFilename", e)
            if (localFile.exists()) localFile.delete()
        } catch (e: Exception) {
            Log.e("SongRepository", "Background download failed", e)
            if (localFile.exists()) localFile.delete()
        }
    }

    /**
     * Utility to check whether a song is already cached.
     * @param songFilename The name of the song file.
     */
    override fun isSongCached(songFilename: String): Boolean {
        val localFile = File(context.filesDir, songFilename)
        return localFile.exists() && localFile.length() > 0
    }
}
