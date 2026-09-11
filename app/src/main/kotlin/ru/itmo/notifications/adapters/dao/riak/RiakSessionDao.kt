package ru.itmo.notifications.adapters.dao.riak

import org.springframework.stereotype.Repository
import ru.itmo.notifications.adapters.riak.RiakTemplate
import ru.itmo.notifications.domain.OperatorSession
import ru.itmo.notifications.spi.SessionDao
import java.time.Instant

@Repository
class RiakSessionDao(
    private val riak: RiakTemplate,
) : SessionDao {
    private data class StoredSession(val expiresAt: Instant)

    override fun save(session: OperatorSession) =
        riak.put(BUCKET, session.token, StoredSession(session.expiresAt))

    override fun find(token: String): OperatorSession? =
        riak.get<StoredSession>(BUCKET, token)?.let { OperatorSession(token, it.expiresAt) }

    override fun delete(token: String) = riak.delete(BUCKET, token)

    private companion object {
        const val BUCKET = "sessions"
    }
}
