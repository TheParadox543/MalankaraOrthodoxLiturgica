package com.paradox543.malankaraorthodoxliturgica.domain.repository

import com.paradox543.malankaraorthodoxliturgica.domain.model.PageNodeDomain

interface NavigationRepository {
    fun getNavigationTree(targetLanguage: String): PageNodeDomain
}