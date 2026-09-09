package ru.itmo.notifications.adapters.dao.memory

import org.springframework.stereotype.Repository
import ru.itmo.notifications.domain.User
import ru.itmo.notifications.spi.UserDao
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Repository
@MemoryStorage
class InMemoryUserDao : UserDao {
    private val users = ConcurrentHashMap<UUID, User>()

    override fun save(user: User) {
        users[user.userId] = user
    }

    override fun find(userId: UUID): User? = users[userId]

    override fun findAll(): List<User> = users.values.sortedBy { it.name }
}
