package com.paradox543.malankaraorthodoxliturgica.data.core.di

import android.content.Context
import com.paradox543.malankaraorthodoxliturgica.data.core.datasource.AssetJsonReader
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

        single {
            AssetJsonReader(
                context = get<Context>(),
                json = get(),
            )
        }
    }