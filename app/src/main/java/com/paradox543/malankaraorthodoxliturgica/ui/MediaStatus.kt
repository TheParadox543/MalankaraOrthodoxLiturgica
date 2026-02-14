package com.paradox543.malankaraorthodoxliturgica.ui

import android.net.Uri

sealed interface MediaStatus {
    object Loading : MediaStatus

    data class Ready(
        val message: String,
        val mediaUri: Uri,
    ) : MediaStatus

    data class Error(
        val message: String,
    ) : MediaStatus
}