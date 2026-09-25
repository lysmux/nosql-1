package ru.itmo.notifications.service

import org.springframework.stereotype.Service
import ru.itmo.notifications.domain.Notification
import ru.itmo.notifications.domain.Order
import ru.itmo.notifications.domain.OrderStatus
import ru.itmo.notifications.shared.exceptions.ValidationException
import ru.itmo.notifications.spi.NotificationDao
import java.time.Instant
import java.util.UUID

@Service
class NotificationService(
    private val notifications: NotificationDao,
    private val users: UserService,
) {
    fun feed(limit: Int): List<Notification> = notifications.findLatest(checkLimit(limit))

    fun historyOf(userId: UUID, limit: Int): List<Notification> {
        users.requireExists(userId)
        return notifications.findByUser(userId, checkLimit(limit))
    }

    fun createFor(order: Order) {
        val notification = Notification(
            notificationId = UUID.randomUUID(),
            userId = order.userId,
            userName = order.userName,
            orderId = order.orderId,
            text = textFor(order),
            createdAt = Instant.now(),
        )
        notifications.save(notification)
    }

    private fun textFor(order: Order): String {
        val restaurant = "«${order.restaurantName}»"
        return when (order.status) {
            OrderStatus.CREATED -> "Ваш заказ из $restaurant принят"
            OrderStatus.COOKING -> "Ваш заказ из $restaurant готовится"
            OrderStatus.IN_DELIVERY -> "Ваш заказ из $restaurant передан курьеру"
            OrderStatus.DELIVERED -> "Ваш заказ из $restaurant доставлен"
        }
    }

    private fun checkLimit(limit: Int): Int =
        if (limit in MIN_LIMIT..MAX_LIMIT) limit
        else throw ValidationException("limit должен быть в диапазоне $MIN_LIMIT..$MAX_LIMIT")

    private companion object {
        const val MIN_LIMIT = 1
        const val MAX_LIMIT = 200
    }
}
