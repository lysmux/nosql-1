package ru.itmo.notifications.entrypoint.rest.resource.user

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import ru.itmo.notifications.entrypoint.api.UsersApi
import ru.itmo.notifications.entrypoint.api.model.CreateUserRequestDto
import ru.itmo.notifications.entrypoint.api.model.NotificationDto
import ru.itmo.notifications.entrypoint.api.model.UserDto
import ru.itmo.notifications.entrypoint.rest.resource.notification.toDto
import ru.itmo.notifications.service.NotificationService
import ru.itmo.notifications.service.UserService
import java.util.UUID

@RestController
class UserController(
    private val users: UserService,
    private val notifications: NotificationService,
) : UsersApi {
    override fun usersCreate(createUserRequestDto: CreateUserRequestDto): ResponseEntity<UserDto> =
        ResponseEntity.status(HttpStatus.CREATED).body(users.create(createUserRequestDto.name).toDto())

    override fun usersSearch(query: String?, limit: Int): ResponseEntity<List<UserDto>> =
        ResponseEntity.ok(users.search(query, limit).map { it.toDto() })

    override fun usersRead(userId: UUID): ResponseEntity<UserDto> =
        ResponseEntity.ok(users.profile(userId).toDto())

    override fun usersNotifications(userId: UUID,limit: Int): ResponseEntity<List<NotificationDto>> =
        ResponseEntity.ok(notifications.historyOf(userId, limit).map { it.toDto() })
}
