package ru.itmo.notifications.entrypoint.rest.resource.auth

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.FilterChain
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.servlet.HandlerExceptionResolver
import ru.itmo.notifications.service.SessionService

@Component
class SessionFilter(
    private val sessions: SessionService,
    @Qualifier("handlerExceptionResolver") private val exceptionResolver: HandlerExceptionResolver,
) : OncePerRequestFilter() {
    override fun shouldNotFilter(request: HttpServletRequest): Boolean =
        request.servletPath in UNPROTECTED_PATHS

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {
            sessions.touch(request.sessionToken())
        } catch (exception: Exception) {
            exceptionResolver.resolveException(request, response, null, exception)
            return
        }
        filterChain.doFilter(request, response)
    }

    private companion object {
        val UNPROTECTED_PATHS = setOf("/auth/login", "/error")
    }
}
