package ru.itmo.notifications.entrypoint.rest.resource.auth

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseCookie
import ru.itmo.notifications.service.exceptions.SessionExpiredException
import java.time.Duration

const val SESSION_COOKIE = "session_token"

fun HttpServletRequest.sessionToken(): String =
    cookies?.firstOrNull { it.name == SESSION_COOKIE }?.value ?: throw SessionExpiredException()

fun sessionCookie(token: String): ResponseCookie = sessionCookieBuilder(token).build()

fun expiredSessionCookie(): ResponseCookie = sessionCookieBuilder("").maxAge(Duration.ZERO).build()

private fun sessionCookieBuilder(value: String) =
    ResponseCookie.from(SESSION_COOKIE, value)
        .path("/")
        .httpOnly(true)
