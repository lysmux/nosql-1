package ru.itmo.notifications.entrypoint.rest.resource.user

import ru.itmo.notifications.domain.User
import ru.itmo.notifications.entrypoint.api.model.UserDto

fun User.toDto() = UserDto(
    userId = userId,
    name = name,
)
