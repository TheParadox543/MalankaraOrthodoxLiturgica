package com.paradox543.malankaraorthodoxliturgica.shared

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform