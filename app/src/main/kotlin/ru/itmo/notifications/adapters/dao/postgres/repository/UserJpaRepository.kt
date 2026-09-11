package ru.itmo.notifications.adapters.dao.postgres.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.itmo.notifications.adapters.dao.postgres.entity.UserEntity
import java.util.UUID

interface UserJpaRepository : JpaRepository<UserEntity, UUID> {
    fun findAllByOrderByName(): List<UserEntity>
}
