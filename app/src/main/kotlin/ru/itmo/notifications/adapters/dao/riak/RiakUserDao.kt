package ru.itmo.notifications.adapters.dao.riak

import org.springframework.stereotype.Repository
import ru.itmo.notifications.adapters.riak.RiakStorage
import ru.itmo.notifications.adapters.riak.RiakTemplate
import ru.itmo.notifications.domain.User
import ru.itmo.notifications.spi.UserDao
import java.util.UUID

@Repository
@RiakStorage
class RiakUserDao(
    private val riak: RiakTemplate,
) : UserDao {
    override fun save(user: User) {
        val key = user.userId.toString()
        riak.put(BUCKET, key, user)
        riak.addToSet(INDEX_BUCKET, ALL_USERS, key)
    }

    override fun find(userId: UUID): User? = riak.get(BUCKET, userId.toString())

    override fun findAll(): List<User> =
        riak.getAll<User>(BUCKET, riak.fetchSet(INDEX_BUCKET, ALL_USERS)).sortedBy { it.name }

    private companion object {
        const val BUCKET = "users"
        const val INDEX_BUCKET = "indexes"
        const val ALL_USERS = "users"
    }
}
