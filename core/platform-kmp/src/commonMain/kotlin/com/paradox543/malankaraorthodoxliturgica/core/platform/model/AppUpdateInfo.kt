package com.paradox543.malankaraorthodoxliturgica.core.platform.model

data class AppUpdateInfo(
    val isUpdateAvailable: Boolean,
    val isForceUpdate: Boolean,
    val availableVersion: String,
)