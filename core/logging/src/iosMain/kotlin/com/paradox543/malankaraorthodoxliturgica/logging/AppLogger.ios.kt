package com.paradox543.malankaraorthodoxliturgica.logging

actual object AppLogger {
    actual var level: LogLevel = LogLevel.INFO

    private fun enabled(min: LogLevel) = level <= min

    actual fun v(
        tag: String,
        message: () -> String,
    ) {
        if (enabled(LogLevel.VERBOSE)) println("VERBOSE: [$tag] ${message()}")
    }

    actual fun d(
        tag: String,
        message: () -> String,
    ) {
        if (enabled(LogLevel.DEBUG)) println("DEBUG: [$tag] ${message()}")
    }

    actual fun i(
        tag: String,
        message: () -> String,
    ) {
        if (enabled(LogLevel.INFO)) println("INFO: [$tag] ${message()}")
    }

    actual fun w(
        tag: String,
        message: () -> String,
    ) {
        if (enabled(LogLevel.WARN)) println("WARN: [$tag] ${message()}")
    }

    actual fun e(
        tag: String,
        throwable: Throwable?,
        message: () -> String,
    ) {
        if (enabled(LogLevel.ERROR)) {
            println("ERROR: [$tag] ${message()}")
            throwable?.printStackTrace()
        }
    }
}
