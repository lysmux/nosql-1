package ru.itmo.notifications.entrypoint.rest.resource.mainpage

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import ru.itmo.notifications.entrypoint.api.MainPageApi
import ru.itmo.notifications.entrypoint.api.model.PageCounterDto
import ru.itmo.notifications.service.MainPageService

@RestController
class MainPageController(
    private val mainPage: MainPageService,
) : MainPageApi {
    override fun mainPageRegisterVisit(): ResponseEntity<PageCounterDto> =
        ResponseEntity.ok(mainPage.registerVisit().toDto())
}
