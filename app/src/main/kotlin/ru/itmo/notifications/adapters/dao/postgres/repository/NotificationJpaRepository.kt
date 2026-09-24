package ru.itmo.notifications.adapters.dao.postgres.repository

import org.springframework.data.domain.Limit
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import ru.itmo.notifications.adapters.dao.postgres.entity.NotificationEntity
import java.util.UUID

interface NotificationJpaRepository : JpaRepository<NotificationEntity, UUID> {
    @EntityGraph(attributePaths = ["user"])
    fun findAllByOrderByCreatedAtDesc(limit: Limit): List<NotificationEntity>

    @EntityGraph(attributePaths = ["user"])
    fun findByUserUserIdOrderByCreatedAtDesc(userId: UUID, limit: Limit): List<NotificationEntity>
}
