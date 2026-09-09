package ru.itmo.notifications.spi

import ru.itmo.notifications.domain.Order
import java.util.UUID

interface OrderDao {
    fun save(order: Order)
    fun find(orderId: UUID): Order?
    fun findAll(): List<Order>
}
