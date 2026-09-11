package ru.itmo.notifications.adapters.dao.postgres.entity

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.springframework.data.domain.Persistable
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "notifications")
class NotificationEntity(
    @Id
    var notificationId: UUID,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    var user: UserEntity,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id")
    var order: OrderEntity,
    var text: String,
    var createdAt: Instant,
) : Persistable<UUID> {
    override fun getId(): UUID = notificationId

    // Notifications are append-only, so save() can persist directly instead of merge's lookup by id
    override fun isNew(): Boolean = true
}
