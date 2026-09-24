package ru.itmo.notifications.entrypoint.rest.resource.order

import ru.itmo.notifications.domain.Order
import ru.itmo.notifications.domain.OrderStatus
import ru.itmo.notifications.domain.Paged
import ru.itmo.notifications.entrypoint.api.model.OrderDto
import ru.itmo.notifications.entrypoint.api.model.OrderPageDto
import ru.itmo.notifications.entrypoint.api.model.OrderStatusDto

fun Order.toDto() = OrderDto(
    orderId = orderId,
    userId = userId,
    userName = userName,
    restaurantName = restaurantName,
    totalAmount = totalAmount,
    status = status.toDto(),
    createdAt = createdAt,
)

fun Paged<Order>.toDto() = OrderPageDto(
    items = items.map { it.toDto() },
    page = page,
    pageSize = size,
    totalItems = totalItems,
    totalPages = totalPages,
)

fun OrderStatus.toDto() = when (this) {
    OrderStatus.CREATED -> OrderStatusDto.CREATED
    OrderStatus.COOKING -> OrderStatusDto.COOKING
    OrderStatus.IN_DELIVERY -> OrderStatusDto.IN_DELIVERY
    OrderStatus.DELIVERED -> OrderStatusDto.DELIVERED
}
