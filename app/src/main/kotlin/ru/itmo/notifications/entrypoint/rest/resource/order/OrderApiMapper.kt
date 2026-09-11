package ru.itmo.notifications.entrypoint.rest.resource.order

import ru.itmo.notifications.domain.Order
import ru.itmo.notifications.domain.OrderStatus
import ru.itmo.notifications.entrypoint.api.model.OrderDto
import ru.itmo.notifications.entrypoint.api.model.OrderStatusDto

fun Order.toDto() = OrderDto(
    orderId = orderId,
    userId = userId,
    restaurantName = restaurantName,
    totalAmount = totalAmount,
    status = status.toDto(),
    createdAt = createdAt,
)

fun OrderStatus.toDto() = when (this) {
    OrderStatus.CREATED -> OrderStatusDto.CREATED
    OrderStatus.COOKING -> OrderStatusDto.COOKING
    OrderStatus.IN_DELIVERY -> OrderStatusDto.IN_DELIVERY
    OrderStatus.DELIVERED -> OrderStatusDto.DELIVERED
}
