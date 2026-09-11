package ru.itmo.notifications.adapters.dao.postgres

import org.springframework.data.domain.Limit
import org.springframework.stereotype.Repository
import ru.itmo.notifications.adapters.dao.postgres.entity.NotificationEntity
import ru.itmo.notifications.adapters.dao.postgres.repository.NotificationJpaRepository
import ru.itmo.notifications.adapters.dao.postgres.repository.OrderJpaRepository
import ru.itmo.notifications.adapters.dao.postgres.repository.UserJpaRepository
import ru.itmo.notifications.domain.Notification
import ru.itmo.notifications.spi.NotificationDao
import java.util.UUID

@Repository
class PostgresNotificationDao(
    private val notifications: NotificationJpaRepository,
    private val users: UserJpaRepository,
    private val orders: OrderJpaRepository,
) : NotificationDao {
    override fun save(notification: Notification) {
        notifications.save(
            NotificationEntity(
                notificationId = notification.notificationId,
                user = users.getReferenceById(notification.userId),
                order = orders.getReferenceById(notification.orderId),
                text = notification.text,
                createdAt = notification.createdAt,
            ),
        )
    }

    override fun findLatest(limit: Int): List<Notification> =
        notifications.findAllByOrderByCreatedAtDesc(Limit.of(limit)).map { it.toDomain() }

    override fun findByUser(userId: UUID, limit: Int): List<Notification> =
        notifications.findByUserUserIdOrderByCreatedAtDesc(userId, Limit.of(limit)).map { it.toDomain() }

    private fun NotificationEntity.toDomain() = Notification(
        notificationId = notificationId,
        userId = user.userId,
        orderId = order.orderId,
        text = text,
        createdAt = createdAt,
    )
}
