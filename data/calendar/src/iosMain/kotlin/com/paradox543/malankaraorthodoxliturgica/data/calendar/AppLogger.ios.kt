package com.paradox543.malankaraorthodoxliturgica.data.calendar

actual object AppLogger {
    actual var level: com.paradox543.malankaraorthodoxliturgica.data.calendar.LogLevel
        get() = TODO("Not yet implemented")
        set(value) {}

    actual fun v(
        tag: String,
        message: () -> String,
    ) {
    }

    actual fun d(
        tag: String,
        message: () -> String,
    ) {
    }

    actual fun i(
        tag: String,
        message: () -> String,
    ) {
    }

    actual fun w(
        tag: String,
        message: () -> String,
    ) {
    }

    actual fun e(
        tag: String,
        message: () -> String,
        throwable: Throwable?,
    ) {
    }
}