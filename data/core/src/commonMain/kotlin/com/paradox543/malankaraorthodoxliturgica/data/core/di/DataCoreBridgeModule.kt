package com.paradox543.malankaraorthodoxliturgica.data.core.di

import com.paradox543.malankaraorthodoxliturgica.data.core.datasource.AssetJsonReader
import com.paradox543.malankaraorthodoxliturgica.data.core.platform.PlatformAssetReader
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

        single { PlatformAssetReader() }

        single {
            AssetJsonReader(
                platformAssetReader = get(),
                json = get(),
            )
        }
    }