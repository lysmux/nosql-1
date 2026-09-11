package ru.itmo.notifications.entrypoint.rest.resource.auth

import ru.itmo.notifications.domain.OperatorSession
import ru.itmo.notifications.entrypoint.api.model.OperatorSessionDto

fun OperatorSession.toDto() = OperatorSessionDto(
    expiresAt = expiresAt,
)
