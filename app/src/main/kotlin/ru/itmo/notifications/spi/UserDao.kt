package ru.itmo.notifications.spi

import ru.itmo.notifications.domain.User
import java.util.UUID

interface UserDao {
    fun save(user: User)
    fun find(userId: UUID): User?
    fun findAll(): List<User>
}
