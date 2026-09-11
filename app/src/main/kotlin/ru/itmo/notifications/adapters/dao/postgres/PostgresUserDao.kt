package ru.itmo.notifications.adapters.dao.postgres

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import ru.itmo.notifications.adapters.dao.postgres.entity.UserEntity
import ru.itmo.notifications.adapters.dao.postgres.repository.UserJpaRepository
import ru.itmo.notifications.domain.User
import ru.itmo.notifications.spi.UserDao
import java.util.UUID

@Repository
class PostgresUserDao(
    private val users: UserJpaRepository,
) : UserDao {
    override fun save(user: User) {
        users.save(UserEntity(user.userId, user.name))
    }

    override fun find(userId: UUID): User? = users.findByIdOrNull(userId)?.toDomain()

    override fun findAll(): List<User> = users.findAllByOrderByName().map { it.toDomain() }

    private fun UserEntity.toDomain() = User(userId, name)
}
