package ru.itmo.notifications.service.exceptions

import ru.itmo.notifications.shared.exceptions.DomainException

class SessionExpiredException : DomainException("Сессия отсутствует или истекла")
