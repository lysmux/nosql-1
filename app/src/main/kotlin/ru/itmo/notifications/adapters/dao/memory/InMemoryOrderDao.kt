package ru.itmo.notifications.adapters.dao.memory

import org.springframework.stereotype.Repository
import ru.itmo.notifications.domain.Order
import ru.itmo.notifications.spi.OrderDao
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Repository
@MemoryStorage
class InMemoryOrderDao : OrderDao {
    private val orders = ConcurrentHashMap<UUID, Order>()

    override fun save(order: Order) {
        orders[order.orderId] = order
    }

    override fun find(orderId: UUID): Order? = orders[orderId]

    override fun findAll(): List<Order> = orders.values.sortedByDescending { it.createdAt }
}
