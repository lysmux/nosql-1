package ru.itmo.notifications.domain

import java.time.Instant
import java.util.UUID

data class Notification(
    val notificationId: UUID,
    val userId: UUID,
    val orderId: UUID,
    val text: String,
    val createdAt: Instant,
)
