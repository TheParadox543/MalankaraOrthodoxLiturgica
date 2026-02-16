package com.paradox543.malankaraorthodoxliturgica.domain.bible.model

import java.io.IOException

class BookNotFoundException(
    message: String,
) : IOException(message)