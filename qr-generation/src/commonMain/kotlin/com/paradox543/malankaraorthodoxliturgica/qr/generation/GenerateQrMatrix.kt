package com.paradox543.malankaraorthodoxliturgica.qr.generation

import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter

fun generateQrMatrix(
    data: String,
    size: Int = 512,
): Array<BooleanArray> {
    val bitMatrix = MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, size, size)

    return Array(size) { x ->
        BooleanArray(size) { y ->
            bitMatrix[x, y]
        }
    }
}