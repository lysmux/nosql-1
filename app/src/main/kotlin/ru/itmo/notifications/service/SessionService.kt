package ru.itmo.notifications.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.itmo.notifications.domain.OperatorSession
import ru.itmo.notifications.service.exceptions.SessionExpiredException
import ru.itmo.notifications.spi.SessionDao
import java.time.Duration
import java.time.Instant
import java.util.UUID

@Service
class SessionService(
    private val sessions: SessionDao,
    @Value("\${app.session.ttl}") private val ttl: Duration,
) {
    fun login(): OperatorSession =
        OperatorSession(UUID.randomUUID().toString(), Instant.now().plus(ttl)).also(sessions::save)

    fun touch(token: String) {
        val session = sessions.find(token) ?: throw SessionExpiredException()
        if (session.expiresAt.isBefore(Instant.now())) {
            sessions.delete(token)
            throw SessionExpiredException()
        }
        sessions.save(session.copy(expiresAt = Instant.now().plus(ttl)))
    }

    fun logout(token: String) = sessions.delete(token)
}
