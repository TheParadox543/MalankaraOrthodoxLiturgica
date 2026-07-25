package com.paradox543.malankaraorthodoxliturgica.qr.generation

expect fun generateQrMatrix(
    data: String,
    size: Int = 512,
): Array<BooleanArray>
