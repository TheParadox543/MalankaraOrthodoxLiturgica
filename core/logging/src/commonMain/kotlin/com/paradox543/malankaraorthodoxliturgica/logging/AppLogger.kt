package com.paradox543.malankaraorthodoxliturgica.logging

enum class LogLevel { VERBOSE, DEBUG, INFO, WARN, ERROR, NONE }

expect object AppLogger {
    var level: LogLevel

    fun v(
        tag: String = "App",
        message: () -> String,
    )

    fun d(
        tag: String = "App",
        message: () -> String,
    )

    fun i(
        tag: String = "App",
        message: () -> String,
    )

    fun w(
        tag: String = "App",
        message: () -> String,
    )

    fun e(
        tag: String = "App",
        throwable: Throwable? = null,
        message: () -> String,
    )
}
