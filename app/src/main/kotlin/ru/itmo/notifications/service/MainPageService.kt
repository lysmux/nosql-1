package ru.itmo.notifications.service

import org.springframework.stereotype.Service
import ru.itmo.notifications.domain.PageCounter
import ru.itmo.notifications.spi.VisitCounterDao

@Service
class MainPageService(
    private val counter: VisitCounterDao,
) {
    fun registerVisit(): PageCounter = PageCounter(MAIN_PAGE, counter.increment(MAIN_PAGE))

    private companion object {
        const val MAIN_PAGE = "main-page"
    }
}
