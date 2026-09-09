package ru.itmo.notifications.adapters.dao.memory

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import ru.itmo.notifications.domain.User
import ru.itmo.notifications.spi.UserProfileCacheDao
import java.time.Duration
import java.time.Instant
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Repository
@MemoryStorage
class InMemoryUserProfileCacheDao(
    @Value("\${app.cache.user-profile.ttl}") private val ttl: Duration,
) : UserProfileCacheDao {
    private data class Entry(val user: User, val cachedAt: Instant)

    private val entries = ConcurrentHashMap<UUID, Entry>()

    override fun get(userId: UUID): User? {
        val entry = entries[userId] ?: return null
        if (entry.cachedAt.plus(ttl).isBefore(Instant.now())) {
            entries.remove(userId)
            return null
        }
        return entry.user
    }

    override fun put(user: User) {
        entries[user.userId] = Entry(user, Instant.now())
    }
}
