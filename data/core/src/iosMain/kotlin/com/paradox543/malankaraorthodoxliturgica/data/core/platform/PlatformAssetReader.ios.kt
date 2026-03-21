package com.paradox543.malankaraorthodoxliturgica.data.core.platform

import platform.Foundation.NSBundle
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.stringWithContentsOfFile

actual class PlatformAssetReader actual constructor() {
    actual fun readText(path: String): String {
        val resourcePath =
            NSBundle.mainBundle.resourcePath
                ?: error("NSBundle resource path is unavailable.")
        val fullPath = "$resourcePath/$path"

        return NSString
            .stringWithContentsOfFile(
                path = fullPath,
                encoding = NSUTF8StringEncoding,
                error = null,
            )?.toString() ?: error("Failed to read asset at path: $path")
    }
}