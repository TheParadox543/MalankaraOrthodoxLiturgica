package com.paradox543.malankaraorthodoxliturgica.domain.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.repository.CalendarRepository
import javax.inject.Inject

class GetSongKeyPriorityUseCase @Inject constructor(
    private val calendarRepository: CalendarRepository,
) {
    suspend operator fun invoke(): String {
        val weekEventItems = calendarRepository.getUpcomingWeekEventItems()
        for (item in weekEventItems) {
            if (item.specialSongsKey != null) {
                return item.specialSongsKey
            }
        }
        return "default"
    }
}
