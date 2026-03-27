package com.paradox543.malankaraorthodoxliturgica.qr.generation

import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint

// fun qrMatrixToImageBitmap(matrix: Array<BooleanArray>): ImageBitmap {
//    val size = matrix.size
//    require(size > 0) { "QR matrix cannot be empty." }
//
//    val image = ImageBitmap(width = size, height = size)
//    val canvas = Canvas(image)
//    val paint = Paint()
//
//    for (x in 0 until size) {
//        for (y in 0 until size) {
//            paint.color = if (matrix[x][y]) Color.Black else Color.White
//            canvas.drawRect(
//                left = x.toFloat(),
//                top = y.toFloat(),
//                right = x.toFloat() + 1f,
//                bottom = y.toFloat() + 1f,
//                paint = paint,
//            )
//        }
//    }
//
//    return image
// }

expect fun qrMatrixToImageBitmap(matrix: Array<BooleanArray>): ImageBitmap