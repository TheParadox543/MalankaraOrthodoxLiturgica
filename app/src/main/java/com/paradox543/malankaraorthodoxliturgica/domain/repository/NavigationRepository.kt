package com.paradox543.malankaraorthodoxliturgica.domain.repository

import com.paradox543.malankaraorthodoxliturgica.data.model.PageNode

interface NavigationRepository {
    fun getNavigationTree(targetLanguage: String): PageNode
}