package com.paradox543.malankaraorthodoxliturgica.logging

import android.util.Log

actual object AppLogger {
    actual var level: LogLevel = LogLevel.DEBUG

    actual fun initialize(debugMode: Boolean) {
        level = if (debugMode) LogLevel.DEBUG else LogLevel.INFO
        d("AppLogger") { "AppLogger initialized in $level mode." }
    }

    private fun enabled(min: LogLevel) = level <= min

    actual fun v(
        tag: String,
        message: () -> String,
    ) {
        if (enabled(LogLevel.VERBOSE)) Log.v(tag, message())
    }

    actual fun d(
        tag: String,
        message: () -> String,
    ) {
        if (enabled(LogLevel.DEBUG)) Log.d(tag, message())
    }

    actual fun i(
        tag: String,
        message: () -> String,
    ) {
        if (enabled(LogLevel.INFO)) Log.i(tag, message())
    }

    actual fun w(
        tag: String,
        message: () -> String,
    ) {
        if (enabled(LogLevel.WARN)) Log.w(tag, message())
    }

    actual fun e(
        tag: String,
        throwable: Throwable?,
        message: () -> String,
    ) {
        if (enabled(LogLevel.ERROR)) Log.e(tag, message(), throwable)
    }
}
