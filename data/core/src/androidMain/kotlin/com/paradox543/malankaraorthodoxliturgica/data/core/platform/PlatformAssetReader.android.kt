package com.paradox543.malankaraorthodoxliturgica.data.core.platform

import android.content.Context
import org.koin.core.context.GlobalContext

actual class PlatformAssetReader actual constructor() {
    actual fun readText(path: String): String {
        val context: Context = GlobalContext.get().get()
        return context.assets
            .open(path)
            .bufferedReader()
            .use { it.readText() }
    }
}