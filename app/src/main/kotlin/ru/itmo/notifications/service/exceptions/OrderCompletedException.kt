package ru.itmo.notifications.service.exceptions

import ru.itmo.notifications.shared.exceptions.DomainException

class OrderCompletedException : DomainException("Заказ уже доставлен")
