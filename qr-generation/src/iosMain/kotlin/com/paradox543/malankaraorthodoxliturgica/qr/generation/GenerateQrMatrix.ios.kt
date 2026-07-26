package com.paradox543.malankaraorthodoxliturgica.qr.generation

import qrcode.QRCode

/**
 * ZXing (used on Android) is JVM-only, so iOS generates the matrix with
 * qrcode-kotlin instead, nearest-neighbor-upscaled to `size x size` pixels
 * to match ZXing's `encode(data, format, size, size)` pixel-precise output
 * (and to avoid Compose stretching a tiny ~25x25 bitmap up to the QR
 * dialog's 250.dp display size, which read as pixelated).
 *
 * `rawData` is row-major (`rawData[row][col]`) — confirmed from the
 * library's own `renderShaded`, which computes `x = col * cellSize` and
 * `y = row * cellSize`. [qrMatrixToImageBitmap] draws `matrix[x][y]`
 * treating the first index as the horizontal/column coordinate, so reading
 * `rawData[x][y]` directly (first index = row) silently transposed the
 * whole code — enough on its own to make it unscannable.
 */
actual fun generateQrMatrix(
    data: String,
    size: Int
): Array<BooleanArray> {
    val rawData = QRCode(data).rawData
    val moduleCount = rawData.size

    return Array(size) { x ->
        BooleanArray(size) { y ->
            val col = (x * moduleCount) / size
            val row = (y * moduleCount) / size
            rawData[row][col].dark
        }
    }
}
