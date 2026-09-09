package ru.itmo.notifications.adapters.dao.memory

import org.springframework.stereotype.Repository
import ru.itmo.notifications.domain.OperatorSession
import ru.itmo.notifications.spi.SessionDao
import java.util.concurrent.ConcurrentHashMap

@Repository
@MemoryStorage
class InMemorySessionDao : SessionDao {
    private val sessions = ConcurrentHashMap<String, OperatorSession>()

    override fun save(session: OperatorSession) {
        sessions[session.token] = session
    }

    override fun find(token: String): OperatorSession? = sessions[token]

    override fun delete(token: String) {
        sessions.remove(token)
    }
}
