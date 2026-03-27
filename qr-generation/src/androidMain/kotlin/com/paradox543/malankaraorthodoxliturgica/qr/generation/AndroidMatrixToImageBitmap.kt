package com.paradox543.malankaraorthodoxliturgica.qr.generation

import android.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.createBitmap

actual fun qrMatrixToImageBitmap(matrix: Array<BooleanArray>): ImageBitmap {
    val size = matrix.size
    require(size > 0) { "QR matrix cannot be empty." }

    val pixels = IntArray(size * size)
    var index = 0

    // Fill row-major pixel buffer, then do one bulk write for performance.
    for (y in 0 until size) {
        for (x in 0 until size) {
            pixels[index++] = if (matrix[x][y]) Color.BLACK else Color.WHITE
        }
    }

    val bitmap = createBitmap(size, size)
    bitmap.setPixels(pixels, 0, size, 0, 0, size, size)
    return bitmap.asImageBitmap()
}