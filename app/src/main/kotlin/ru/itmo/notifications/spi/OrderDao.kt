package ru.itmo.notifications.spi

import ru.itmo.notifications.domain.Order
import ru.itmo.notifications.domain.Paged
import java.util.UUID

interface OrderDao {
    fun save(order: Order)
    fun findForUpdate(orderId: UUID): Order?
    fun findPage(page: Int, size: Int): Paged<Order>
}
