package ru.itmo.notifications.adapters.dao.postgres.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "users")
class UserEntity(
    @Id
    var userId: UUID,
    var name: String,
)
