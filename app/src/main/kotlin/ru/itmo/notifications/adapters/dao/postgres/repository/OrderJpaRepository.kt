package ru.itmo.notifications.adapters.dao.postgres.repository

import jakarta.persistence.LockModeType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import ru.itmo.notifications.adapters.dao.postgres.entity.OrderEntity
import java.util.UUID

interface OrderJpaRepository : JpaRepository<OrderEntity, UUID> {
    @EntityGraph(attributePaths = ["user"])
    fun findAllByOrderByCreatedAtDescOrderIdDesc(pageable: Pageable): Page<OrderEntity>

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findForUpdateByOrderId(orderId: UUID): OrderEntity?
}
