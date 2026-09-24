package ru.itmo.notifications.entrypoint.rest.resource.notification

import ru.itmo.notifications.domain.Notification
import ru.itmo.notifications.entrypoint.api.model.NotificationDto

fun Notification.toDto() = NotificationDto(
    notificationId = notificationId,
    userId = userId,
    userName = userName,
    orderId = orderId,
    text = text,
    createdAt = createdAt,
)
