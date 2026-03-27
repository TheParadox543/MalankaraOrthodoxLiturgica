package com.paradox543.malankaraorthodoxliturgica.qr.generation

import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.Color
import androidx.core.graphics.set

fun qrMatrixToBitmap(matrix: Array<BooleanArray>): Bitmap {
    val size = matrix.size
    val bitmap = createBitmap(size, size, Bitmap.Config.RGB_565)

    for (x in 0 until size) {
        for (y in 0 until size) {
            bitmap[x, y] = if (matrix[x][y]) Color.BLACK else Color.WHITE
        }
    }

    return bitmap
}