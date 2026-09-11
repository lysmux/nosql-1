package ru.itmo.notifications.adapters.dao.postgres.entity

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import ru.itmo.notifications.domain.OrderStatus
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "orders")
class OrderEntity(
    @Id
    var orderId: UUID,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    var user: UserEntity,
    var restaurantName: String,
    var totalAmount: BigDecimal,
    @Enumerated(EnumType.STRING)
    var status: OrderStatus,
    var createdAt: Instant,
)
