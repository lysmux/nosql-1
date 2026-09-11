package ru.itmo.notifications.domain

import java.time.Instant

data class OperatorSession(
    val token: String,
    val expiresAt: Instant,
)
