package com.paradox543.malankaraorthodoxliturgica.data.repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationRepository @Inject constructor(
    @ApplicationContext private val context: Context
)