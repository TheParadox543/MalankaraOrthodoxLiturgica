package com.paradox543.malankaraorthodoxliturgica.qr.generation

import qrcode.QRCode

/**
 * ZXing (used on Android) is JVM-only, so iOS generates the matrix with
 * qrcode-kotlin instead. `size` is unused here: `rawData` is the QR's native
 * module grid (e.g. 25x25), not scaled to a target pixel size like ZXing's
 * `encode(data, format, size, size)` — [QrDialog] displays the resulting
 * bitmap at a fixed `250.dp` regardless of its native pixel dimensions, so
 * there's no layout reason to force a specific resolution.
 */
actual fun generateQrMatrix(
    data: String,
    size: Int
): Array<BooleanArray> {
    val rawData = QRCode(data).rawData
    return Array(rawData.size) { x ->
        BooleanArray(rawData[x].size) { y -> rawData[x][y].dark }
    }
}