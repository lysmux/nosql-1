package ru.itmo.notifications.domain

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID


data class Order(
    val orderId: UUID,
    val userId: UUID,
    val restaurantName: String,
    val totalAmount: BigDecimal,
    val status: OrderStatus,
    val createdAt: Instant,
)

enum class OrderStatus {
    CREATED,
    COOKING,
    IN_DELIVERY,
    DELIVERED;

    fun next(): OrderStatus? = entries.getOrNull(ordinal + 1)
}