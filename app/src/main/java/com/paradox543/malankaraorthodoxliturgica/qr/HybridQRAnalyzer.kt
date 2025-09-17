package com.paradox543.malankaraorthodoxliturgica.qr

import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

class HybridQRAnalyzer (
    private val onQrCodeScanned: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private val mlKitAnalyzer = MLKitQRCodeAnalyzer { qr ->
        if (qr.isNotEmpty()) {
            onQrCodeScanned(qr)
        }
    }
    private val zxingAnalyzer = ZxingQrCodeAnalyzer { qr ->
        if (qr.isNotEmpty()) {
            onQrCodeScanned(qr)
        }
    }

    override fun analyze(image: ImageProxy) {
        Log.d("HybridQRAnalyzer", "Analyzing image with HybridQRAnalyzer")

        // Try MLKit first
        mlKitAnalyzer.analyze(image)

        // If MLKit didn’t trigger, run ZXing as fallback
        zxingAnalyzer.analyze(image)
    }
}