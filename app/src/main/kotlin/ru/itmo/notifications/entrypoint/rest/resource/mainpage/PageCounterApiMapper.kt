package ru.itmo.notifications.entrypoint.rest.resource.mainpage

import ru.itmo.notifications.domain.PageCounter
import ru.itmo.notifications.entrypoint.api.model.PageCounterDto

fun PageCounter.toDto() = PageCounterDto(
    pageKey = pageKey,
    value = value,
)
