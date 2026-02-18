package com.paradox543.malankaraorthodoxliturgica.domain.settings.model

import org.junit.Assert.assertEquals
import org.junit.Test

class AppFontScaleTest {
    @Test
    fun `next increments scale from VerySmall to Small`() {
        assertEquals(AppFontScale.Small, AppFontScale.VerySmall.next())
    }

    @Test
    fun `next increments scale from Small to Medium`() {
        assertEquals(AppFontScale.Medium, AppFontScale.Small.next())
    }

    @Test
    fun `next increments scale from Medium to Large`() {
        assertEquals(AppFontScale.Large, AppFontScale.Medium.next())
    }

    @Test
    fun `next increments scale from Large to VeryLarge`() {
        assertEquals(AppFontScale.VeryLarge, AppFontScale.Large.next())
    }

    @Test
    fun `next stays at VeryLarge when already at max`() {
        assertEquals(AppFontScale.VeryLarge, AppFontScale.VeryLarge.next())
    }

    @Test
    fun `prev decrements scale from VeryLarge to Large`() {
        assertEquals(AppFontScale.Large, AppFontScale.VeryLarge.prev())
    }

    @Test
    fun `prev decrements scale from Large to Medium`() {
        assertEquals(AppFontScale.Medium, AppFontScale.Large.prev())
    }

    @Test
    fun `prev decrements scale from Medium to Small`() {
        assertEquals(AppFontScale.Small, AppFontScale.Medium.prev())
    }

    @Test
    fun `prev decrements scale from Small to VerySmall`() {
        assertEquals(AppFontScale.VerySmall, AppFontScale.Small.prev())
    }

    @Test
    fun `prev stays at VerySmall when already at min`() {
        assertEquals(AppFontScale.VerySmall, AppFontScale.VerySmall.prev())
    }

    @Test
    fun `fromScale returns exact match for VerySmall`() {
        assertEquals(AppFontScale.VerySmall, AppFontScale.fromScale(0.7f))
    }

    @Test
    fun `fromScale returns exact match for Small`() {
        assertEquals(AppFontScale.Small, AppFontScale.fromScale(0.8f))
    }

    @Test
    fun `fromScale returns exact match for Medium`() {
        assertEquals(AppFontScale.Medium, AppFontScale.fromScale(1.0f))
    }

    @Test
    fun `fromScale returns exact match for Large`() {
        assertEquals(AppFontScale.Large, AppFontScale.fromScale(1.2f))
    }

    @Test
    fun `fromScale returns exact match for VeryLarge`() {
        assertEquals(AppFontScale.VeryLarge, AppFontScale.fromScale(1.4f))
    }

    @Test
    fun `fromScale returns closest match for intermediate value between VerySmall and Small`() {
        assertEquals(AppFontScale.VerySmall, AppFontScale.fromScale(0.72f))
        assertEquals(AppFontScale.Small, AppFontScale.fromScale(0.78f))
    }

    @Test
    fun `fromScale returns closest match for intermediate value between Small and Medium`() {
        assertEquals(AppFontScale.Small, AppFontScale.fromScale(0.85f))
        assertEquals(AppFontScale.Medium, AppFontScale.fromScale(0.95f))
    }

    @Test
    fun `fromScale returns closest match for intermediate value between Medium and Large`() {
        assertEquals(AppFontScale.Medium, AppFontScale.fromScale(1.05f))
        assertEquals(AppFontScale.Large, AppFontScale.fromScale(1.15f))
    }

    @Test
    fun `fromScale returns closest match for intermediate value between Large and VeryLarge`() {
        assertEquals(AppFontScale.Large, AppFontScale.fromScale(1.25f))
        assertEquals(AppFontScale.VeryLarge, AppFontScale.fromScale(1.35f))
    }

    @Test
    fun `fromScale returns VerySmall for very small values`() {
        assertEquals(AppFontScale.VerySmall, AppFontScale.fromScale(0.1f))
        assertEquals(AppFontScale.VerySmall, AppFontScale.fromScale(0.5f))
    }

    @Test
    fun `fromScale returns VeryLarge for very large values`() {
        assertEquals(AppFontScale.VeryLarge, AppFontScale.fromScale(2.0f))
        assertEquals(AppFontScale.VeryLarge, AppFontScale.fromScale(5.0f))
    }
}
