package ru.itmo.notifications.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.itmo.notifications.domain.User
import ru.itmo.notifications.shared.exceptions.NotFoundException
import ru.itmo.notifications.shared.exceptions.ValidationException
import ru.itmo.notifications.shared.metrics.MetricsAdapter
import ru.itmo.notifications.spi.UserDao
import ru.itmo.notifications.spi.UserProfileCacheDao
import java.util.UUID

@Service
class UserService(
    private val users: UserDao,
    private val cache: UserProfileCacheDao,
    @Value("\${app.cache.user-profile.enabled}") private val cacheEnabled: Boolean,
    private val metrics: MetricsAdapter,
) {
    fun create(name: String): User {
        val trimmed = name.trim()
        if (trimmed.isEmpty() || trimmed.length > MAX_NAME_LENGTH) {
            throw ValidationException("Имя клиента должно быть от 1 до $MAX_NAME_LENGTH символов")
        }
        return User(UUID.randomUUID(), trimmed).also(users::save)
    }

    fun list(): List<User> = users.findAll()

    fun profile(userId: UUID): User =
        metrics.timed(PROFILE_LOOKUP_METRIC, { lookup -> mapOf("cache" to lookup.cacheOutcome.tag) }) {
            lookupProfile(userId)
        }.user

    fun byId(userId: UUID): User =
        users.find(userId) ?: throw NotFoundException("Клиент $userId не найден")

    private fun lookupProfile(userId: UUID): ProfileLookup {
        if (!cacheEnabled) return ProfileLookup(byId(userId), CacheOutcome.DISABLED)

        cache.get(userId)?.let { return ProfileLookup(it, CacheOutcome.HIT) }
        return ProfileLookup(byId(userId).also(cache::put), CacheOutcome.MISS)
    }

    private class ProfileLookup(val user: User, val cacheOutcome: CacheOutcome)

    private enum class CacheOutcome(val tag: String) {
        HIT("hit"),
        MISS("miss"),
        DISABLED("disabled"),
    }

    private companion object {
        const val MAX_NAME_LENGTH = 120
        const val PROFILE_LOOKUP_METRIC = "user.profile.lookup"
    }
}
