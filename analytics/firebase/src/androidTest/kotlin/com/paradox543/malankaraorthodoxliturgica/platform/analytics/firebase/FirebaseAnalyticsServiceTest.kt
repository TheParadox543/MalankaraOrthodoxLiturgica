package com.paradox543.malankaraorthodoxliturgica.platform.analytics.firebase

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.paradox543.malankaraorthodoxliturgica.core.analytics.AnalyticsEvent
import com.paradox543.malankaraorthodoxliturgica.info.AppInfoProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.slot
import io.mockk.verify
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class FirebaseAnalyticsServiceTest {

    private val firebaseAnalytics: FirebaseAnalytics = mockk(relaxed = true)
    private val appInfoProvider: AppInfoProvider = mockk()
    private lateinit var service: FirebaseAnalyticsService

    @BeforeTest
    fun setup() {
        service = FirebaseAnalyticsService(firebaseAnalytics, appInfoProvider)
        every { appInfoProvider.versionName } returns "1.2.3"
        
        mockkConstructor(Bundle::class)
        every { anyConstructed<Bundle>().putString(any(), any()) } returns Unit
        every { anyConstructed<Bundle>().putInt(any(), any()) } returns Unit
        every { anyConstructed<Bundle>().putBoolean(any(), any()) } returns Unit
        every { anyConstructed<Bundle>().putFloat(any(), any()) } returns Unit
        every { anyConstructed<Bundle>().putDouble(any(), any()) } returns Unit
    }

    @Test
    fun `logEvent for Error event should concatenate details and include app version`() {
        // Given
        val errorEvent = AnalyticsEvent.Error(
            description = "Some error",
            location = "Some location"
        )
        
        // When
        service.logEvent(errorEvent)

        // Then
        verify {
            anyConstructed<Bundle>().putString("error_description", "Some error @ Some location (v1.2.3)")
            firebaseAnalytics.logEvent("app_error", any())
        }
    }
    
    @Test
    fun `logEvent for non-Error event should use original params`() {
        // Given
        val event = AnalyticsEvent.LanguageSelected("English")
        
        // When
        service.logEvent(event)
        
        // Then
        verify {
            anyConstructed<Bundle>().putString("language", "English")
            firebaseAnalytics.logEvent("language_selected", any())
        }
    }
}
