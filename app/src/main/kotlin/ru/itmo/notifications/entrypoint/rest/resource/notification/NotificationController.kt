package ru.itmo.notifications.entrypoint.rest.resource.notification

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import ru.itmo.notifications.entrypoint.api.NotificationsApi
import ru.itmo.notifications.entrypoint.api.model.NotificationDto
import ru.itmo.notifications.service.NotificationService

@RestController
class NotificationController(
    private val notifications: NotificationService,
) : NotificationsApi {
    override fun notificationsFeed(limit: Int): ResponseEntity<List<NotificationDto>> =
        ResponseEntity.ok(notifications.feed(limit).map { it.toDto() })
}
