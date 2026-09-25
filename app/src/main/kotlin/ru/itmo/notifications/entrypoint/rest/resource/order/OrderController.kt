package ru.itmo.notifications.entrypoint.rest.resource.order

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import ru.itmo.notifications.entrypoint.api.OrdersApi
import ru.itmo.notifications.entrypoint.api.model.CreateOrderRequestDto
import ru.itmo.notifications.entrypoint.api.model.OrderDto
import ru.itmo.notifications.entrypoint.api.model.OrderPageDto
import ru.itmo.notifications.service.OrderService
import java.util.UUID

@RestController
class OrderController(
    private val orders: OrderService,
) : OrdersApi {
    override fun ordersList(page: Int, size: Int): ResponseEntity<OrderPageDto> =
        ResponseEntity.ok(orders.list(page, size).toDto())

    override fun ordersCreate(createOrderRequestDto: CreateOrderRequestDto): ResponseEntity<OrderDto> {
        val order = orders.create(
            userId = createOrderRequestDto.userId,
            restaurantName = createOrderRequestDto.restaurantName,
            totalAmount = createOrderRequestDto.totalAmount,
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(order.toDto())
    }

    override fun ordersAdvance(orderId: UUID):ResponseEntity<OrderDto> =
        ResponseEntity.ok(orders.advance(orderId).toDto())
}
