package com.paradox543.malankaraorthodoxliturgica.data.core.platform

expect class PlatformAssetReader() {
    fun readText(path: String): String
}