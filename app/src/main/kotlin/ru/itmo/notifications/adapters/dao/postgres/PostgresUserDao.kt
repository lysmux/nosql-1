package ru.itmo.notifications.adapters.dao.postgres

import org.springframework.data.domain.Limit
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

    override fun exists(userId: UUID): Boolean = users.existsById(userId)

    override fun search(query: String, limit: Int): List<User> {
        val tsQuery = query.toPrefixTsQuery()
        val found = if (tsQuery.isEmpty()) {
            users.findAllByOrderByName(Limit.of(limit))
        } else {
            users.searchByName(tsQuery, limit)
        }
        return found.map { it.toDomain() }
    }

    private fun String.toPrefixTsQuery() =
        split(NON_WORD).filter { it.isNotEmpty() }.joinToString(" & ") { "$it:*" }

    private fun UserEntity.toDomain() = User(userId, name)

    private companion object {
        val NON_WORD = Regex("[^\\p{L}\\p{N}]+")
    }
}
