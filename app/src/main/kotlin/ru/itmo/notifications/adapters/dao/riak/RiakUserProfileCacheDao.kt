package ru.itmo.notifications.adapters.dao.riak

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import ru.itmo.notifications.adapters.riak.RiakStorage
import ru.itmo.notifications.adapters.riak.RiakTemplate
import ru.itmo.notifications.domain.User
import ru.itmo.notifications.spi.UserProfileCacheDao
import java.time.Duration
import java.time.Instant
import java.util.UUID

@Repository
@RiakStorage
class RiakUserProfileCacheDao(
    private val riak: RiakTemplate,
    @Value("\${app.cache.user-profile.ttl}") private val ttl: Duration,
) : UserProfileCacheDao {
    private data class CachedProfile(val userId: UUID, val name: String, val cachedAt: Instant)

    override fun get(userId: UUID): User? {
        val key = userId.toString()
        val entry = riak.get<CachedProfile>(BUCKET, key) ?: return null
        if (entry.cachedAt.plus(ttl).isBefore(Instant.now())) {
            riak.delete(BUCKET, key)
            return null
        }
        return User(entry.userId, entry.name)
    }

    override fun put(user: User) =
        riak.put(BUCKET, user.userId.toString(), CachedProfile(user.userId, user.name, Instant.now()))

    private companion object {
        const val BUCKET = "user_cache"
    }
}
