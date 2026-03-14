package com.paradox543.malankaraorthodoxliturgica.core.platform

import android.media.Image
import java.nio.ByteBuffer

interface QrService {
    fun generateQrByteArray(
        data: String,
        size: Int = 512,
    ): ByteArray

    fun createAnalyzer(onQrCodeScanned: (String) -> Unit): Analyzer

    interface Analyzer
}