package com.paradox543.malankaraorthodoxliturgica.domain.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.calendar.repository.CalendarRepository
import javax.inject.Inject

class GetSongKeyPriorityUseCase @Inject constructor(
    private val calendarRepository: CalendarRepository,
) {
    suspend operator fun invoke(): String {
        val weekEventItems = calendarRepository.getUpcomingWeekEventItems()
        for (item in weekEventItems) {
            item.specialSongsKey?.let {
                return it
            }
        }
        return "default"
    }
}
