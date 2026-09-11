package ru.itmo.notifications.adapters.dao.postgres

import org.springframework.stereotype.Repository
import ru.itmo.notifications.adapters.dao.postgres.entity.OrderEntity
import ru.itmo.notifications.adapters.dao.postgres.repository.OrderJpaRepository
import ru.itmo.notifications.adapters.dao.postgres.repository.UserJpaRepository
import ru.itmo.notifications.domain.Order
import ru.itmo.notifications.spi.OrderDao
import java.util.UUID

@Repository
class PostgresOrderDao(
    private val orders: OrderJpaRepository,
    private val users: UserJpaRepository,
) : OrderDao {
    override fun save(order: Order) {
        orders.save(
            OrderEntity(
                orderId = order.orderId,
                user = users.getReferenceById(order.userId),
                restaurantName = order.restaurantName,
                totalAmount = order.totalAmount,
                status = order.status,
                createdAt = order.createdAt,
            ),
        )
    }

    override fun findForUpdate(orderId: UUID): Order? = orders.findForUpdateByOrderId(orderId)?.toDomain()

    override fun findAll(): List<Order> = orders.findAllByOrderByCreatedAtDesc().map { it.toDomain() }

    private fun OrderEntity.toDomain() = Order(
        orderId = orderId,
        userId = user.userId,
        restaurantName = restaurantName,
        totalAmount = totalAmount,
        status = status,
        createdAt = createdAt,
    )
}
