package ru.itmo.notifications.entrypoint.rest.resource.auth

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import ru.itmo.notifications.entrypoint.api.AuthApi
import ru.itmo.notifications.entrypoint.api.model.OperatorSessionDto
import ru.itmo.notifications.service.SessionService

@RestController
class AuthController(
    private val sessions: SessionService,
    private val request: HttpServletRequest,
) : AuthApi {
    override fun authLogin(): ResponseEntity<OperatorSessionDto> {
        val session = sessions.login()
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, sessionCookie(session.token).toString())
            .body(session.toDto())
    }

    override fun authLogout(): ResponseEntity<Unit> {
        sessions.logout(request.sessionToken())
        return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE, expiredSessionCookie().toString()).build()
    }
}
