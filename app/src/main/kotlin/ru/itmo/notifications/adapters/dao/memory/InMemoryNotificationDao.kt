package ru.itmo.notifications.adapters.dao.memory

import org.springframework.stereotype.Repository
import ru.itmo.notifications.domain.Notification
import ru.itmo.notifications.spi.NotificationDao
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Repository
@MemoryStorage
class InMemoryNotificationDao : NotificationDao {
    private val notifications = ConcurrentHashMap<UUID, Notification>()

    override fun save(notification: Notification) {
        notifications[notification.notificationId] = notification
    }

    override fun findLatest(limit: Int): List<Notification> = latest().take(limit)

    override fun findByUser(userId: UUID, limit: Int): List<Notification> =
        latest().filter { it.userId == userId }.take(limit)

    private fun latest() = notifications.values.sortedByDescending { it.createdAt }
}
