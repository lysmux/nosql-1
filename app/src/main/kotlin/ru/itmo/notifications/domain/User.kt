package ru.itmo.notifications.domain

import java.util.UUID

data class User(
    val userId: UUID,
    val name: String,
)
