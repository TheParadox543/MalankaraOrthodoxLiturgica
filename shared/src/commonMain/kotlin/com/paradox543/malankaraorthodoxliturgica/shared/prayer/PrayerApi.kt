package com.paradox543.malankaraorthodoxliturgica.shared.prayer

class PrayerApi {
    fun getSamplePrayer(): Prayer {
        return Prayer(
            title = "Evening Prayer",
            content = "O Lord, grant us a peaceful night..."
        )
    }
}