package ru.itmo.notifications.spi

import ru.itmo.notifications.domain.User
import java.util.UUID

interface UserProfileCacheDao {
    fun get(userId: UUID): User?
    fun put(user: User)
}
