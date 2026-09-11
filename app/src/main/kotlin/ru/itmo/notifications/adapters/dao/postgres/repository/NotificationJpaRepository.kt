package ru.itmo.notifications.adapters.dao.postgres.repository

import org.springframework.data.domain.Limit
import org.springframework.data.jpa.repository.JpaRepository
import ru.itmo.notifications.adapters.dao.postgres.entity.NotificationEntity
import java.util.UUID

interface NotificationJpaRepository : JpaRepository<NotificationEntity, UUID> {
    fun findAllByOrderByCreatedAtDesc(limit: Limit): List<NotificationEntity>

    fun findByUserUserIdOrderByCreatedAtDesc(userId: UUID, limit: Limit): List<NotificationEntity>
}
