package com.paradox543.malankaraorthodoxliturgica.data.core.di

import kotlinx.serialization.json.Json
import org.koin.dsl.module

val dataCoreBridgeModule =
    module {
        single {
            Json {
                ignoreUnknownKeys = true
                isLenient = true
            }
        }
    }