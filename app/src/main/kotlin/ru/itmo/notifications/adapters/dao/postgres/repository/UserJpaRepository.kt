package ru.itmo.notifications.adapters.dao.postgres.repository

import org.springframework.data.domain.Limit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import ru.itmo.notifications.adapters.dao.postgres.entity.UserEntity
import java.util.UUID

interface UserJpaRepository : JpaRepository<UserEntity, UUID> {
    fun findAllByOrderByName(limit: Limit): List<UserEntity>

    @Query(
        """
        select * from users
        where to_tsvector('simple', name) @@ to_tsquery('simple', :tsQuery)
        order by ts_rank(to_tsvector('simple', name), to_tsquery('simple', :tsQuery)) desc, name
        limit :limit
        """,
        nativeQuery = true,
    )
    fun searchByName(tsQuery: String, limit: Int): List<UserEntity>
}
