package ru.itmo.notifications.spi

import ru.itmo.notifications.domain.Notification
import java.util.UUID

interface NotificationDao {
    fun save(notification: Notification)
    fun findLatest(limit: Int): List<Notification>
    fun findByUser(userId: UUID, limit: Int): List<Notification>
}
