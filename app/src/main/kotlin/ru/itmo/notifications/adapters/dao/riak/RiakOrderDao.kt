package ru.itmo.notifications.adapters.dao.riak

import org.springframework.stereotype.Repository
import ru.itmo.notifications.adapters.riak.RiakStorage
import ru.itmo.notifications.adapters.riak.RiakTemplate
import ru.itmo.notifications.domain.Order
import ru.itmo.notifications.spi.OrderDao
import java.util.UUID

@Repository
@RiakStorage
class RiakOrderDao(
    private val riak: RiakTemplate,
) : OrderDao {
    override fun save(order: Order) {
        val key = order.orderId.toString()
        riak.put(BUCKET, key, order)
        riak.addToSet(INDEX_BUCKET, ALL_ORDERS, key)
    }

    override fun find(orderId: UUID): Order? = riak.get(BUCKET, orderId.toString())

    override fun findAll(): List<Order> =
        riak.getAll<Order>(BUCKET, riak.fetchSet(INDEX_BUCKET, ALL_ORDERS)).sortedByDescending { it.createdAt }

    private companion object {
        const val BUCKET = "orders"
        const val INDEX_BUCKET = "indexes"
        const val ALL_ORDERS = "orders"
    }
}
