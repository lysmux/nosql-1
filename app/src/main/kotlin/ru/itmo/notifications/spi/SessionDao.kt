package ru.itmo.notifications.spi

import ru.itmo.notifications.domain.OperatorSession

interface SessionDao {
    fun save(session: OperatorSession)
    fun find(token: String): OperatorSession?
    fun delete(token: String)
}
