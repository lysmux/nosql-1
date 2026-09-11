package ru.itmo.notifications.adapters.dao.riak

import org.springframework.stereotype.Repository
import ru.itmo.notifications.adapters.riak.RiakStorage
import ru.itmo.notifications.adapters.riak.RiakTemplate
import ru.itmo.notifications.domain.Notification
import ru.itmo.notifications.spi.NotificationDao
import java.util.UUID

@Repository
@RiakStorage
class RiakNotificationDao(
    private val riak: RiakTemplate,
) : NotificationDao {
    override fun save(notification: Notification) {
        val key = notification.notificationId.toString()
        riak.put(BUCKET, key, notification)
        riak.addToSet(INDEX_BUCKET, ALL_NOTIFICATIONS, key)
        riak.addToSet(USER_INDEX_BUCKET, notification.userId.toString(), key)
    }

    override fun findLatest(limit: Int): List<Notification> =
        latest(riak.fetchSet(INDEX_BUCKET, ALL_NOTIFICATIONS), limit)

    override fun findByUser(userId: UUID, limit: Int): List<Notification> =
        latest(riak.fetchSet(USER_INDEX_BUCKET, userId.toString()), limit)

    private fun latest(keys: Set<String>, limit: Int) =
        riak.getAll<Notification>(BUCKET, keys).sortedByDescending { it.createdAt }.take(limit)

    private companion object {
        const val BUCKET = "notifications"
        const val INDEX_BUCKET = "indexes"
        const val ALL_NOTIFICATIONS = "notifications"
        const val USER_INDEX_BUCKET = "user_notifications"
    }
}
