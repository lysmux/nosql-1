package ru.itmo.notifications.service

import org.springframework.stereotype.Service
import ru.itmo.notifications.domain.User
import ru.itmo.notifications.shared.exceptions.NotFoundException
import ru.itmo.notifications.shared.exceptions.ValidationException
import ru.itmo.notifications.spi.UserDao
import ru.itmo.notifications.spi.UserProfileCacheDao
import java.util.UUID

@Service
class UserService(
    private val users: UserDao,
    private val cache: UserProfileCacheDao,
) {
    fun create(name: String): User {
        val trimmed = name.trim()
        if (trimmed.isEmpty() || trimmed.length > MAX_NAME_LENGTH) {
            throw ValidationException("Имя клиента должно быть от 1 до $MAX_NAME_LENGTH символов")
        }
        return User(UUID.randomUUID(), trimmed).also(users::save)
    }

    fun list(): List<User> = users.findAll()

    fun profile(userId: UUID): User = cache.get(userId) ?: byId(userId).also(cache::put)

    fun byId(userId: UUID): User =
        users.find(userId) ?: throw NotFoundException("Клиент $userId не найден")

    private companion object {
        const val MAX_NAME_LENGTH = 120
    }
}
