package com.paradox543.malankaraorthodoxliturgica.data.core.datasource

interface ResourceTextReader {
    suspend fun readText(path: String): String
}