package com.paradox543.malankaraorthodoxliturgica.domain.model

data class BibleReference(
    val bookNumber: Int,
    val ranges: List<ReferenceRange>,
)