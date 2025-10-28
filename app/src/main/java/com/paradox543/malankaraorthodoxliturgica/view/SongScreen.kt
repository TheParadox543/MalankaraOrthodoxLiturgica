package com.paradox543.malankaraorthodoxliturgica.view

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import java.io.File

@Composable
fun SongScreen() {
    val context = LocalContext.current
    Column(Modifier.padding(16.dp)) {
        Text("Testing adding songs with firebase")
        // Alternatively, explicitly specify the bucket name URL.
        val storage = Firebase.storage("gs://liturgica-3d3a4.firebasestorage.app")

        var storageRef = storage.reference

        var ekkaraRef = storageRef.child("ekkaraSongs")

        var introductionRef = ekkaraRef.child("introduction/00 Introduction.mp3")

        var rootRef = introductionRef.root
        var parentRef = introductionRef.parent

        Text(storageRef?.name ?: "No Storage ref received")
        Text("${ekkaraRef?.name}")
        Text("${introductionRef?.name}")
        rootRef?.name?.let { Text(it) }
        parentRef?.name?.let { Text(it) }

        val fakeFileRef = storageRef.child("fakeFile.txt")
        Text(fakeFileRef.name)
        Log.d("SongScreen", "SongScreen: ${fakeFileRef.name}")
        Log.d("SongScreen", "Path to file: ${fakeFileRef.path}")
        Log.d("SongScreen", "Path to introduction: ${introductionRef.path}")

        Text("Downloading a file")

        val ONE_MEGABYTE: Long = 1024 * 1024
        val location = File(context.filesDir, "introduction.mp3")
        introductionRef.getFile(location).addOnSuccessListener {
            Log.d("SongScreen", "File downloaded to ${location.path}")
            // Data for "images/island.jpg" is returned, use this as needed
        }.addOnFailureListener { e ->
            Log.d("SongScreen", "File download failed", e)
            // Handle any errors
        }
    }
}