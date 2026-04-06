package com.paradox543.malankaraorthodoxliturgica.qr.generation

import androidx.compose.ui.graphics.ImageBitmap

expect fun qrMatrixToImageBitmap(matrix: Array<BooleanArray>): ImageBitmap