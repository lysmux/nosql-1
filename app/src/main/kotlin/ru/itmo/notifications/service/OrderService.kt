package ru.itmo.notifications.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.itmo.notifications.domain.Order
import ru.itmo.notifications.domain.OrderStatus
import ru.itmo.notifications.service.exceptions.OrderCompletedException
import ru.itmo.notifications.shared.exceptions.NotFoundException
import ru.itmo.notifications.shared.exceptions.ValidationException
import ru.itmo.notifications.spi.OrderDao
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Service
class OrderService(
    private val orders: OrderDao,
    private val users: UserService,
    private val notifications: NotificationService,
) {
    fun list(): List<Order> = orders.findAll()

    @Transactional
    fun create(userId: UUID, restaurantName: String, totalAmount: BigDecimal): Order {
        users.byId(userId)
        val order = Order(
            orderId = UUID.randomUUID(),
            userId = userId,
            restaurantName = checkRestaurantName(restaurantName),
            totalAmount = checkTotalAmount(totalAmount),
            status = OrderStatus.CREATED,
            createdAt = Instant.now(),
        )
        orders.save(order)
        notifications.createFor(order)
        return order
    }

    @Transactional
    fun advance(orderId: UUID): Order {
        val order = orders.findForUpdate(orderId) ?: throw NotFoundException("Заказ $orderId не найден")
        val next = order.status.next() ?: throw OrderCompletedException()
        val advanced = order.copy(status = next)
        orders.save(advanced)
        notifications.createFor(advanced)
        return advanced
    }

    private fun checkRestaurantName(restaurantName: String): String {
        val trimmed = restaurantName.trim()
        if (trimmed.isEmpty() || trimmed.length > MAX_RESTAURANT_NAME_LENGTH) {
            throw ValidationException(
                "Название ресторана должно быть от 1 до $MAX_RESTAURANT_NAME_LENGTH символов",
            )
        }
        return trimmed
    }

    private fun checkTotalAmount(totalAmount: BigDecimal): BigDecimal {
        if (totalAmount < BigDecimal.ZERO) {
            throw ValidationException("Сумма заказа не может быть отрицательной")
        }
        return totalAmount
    }

    private companion object {
        const val MAX_RESTAURANT_NAME_LENGTH = 200
    }
}
