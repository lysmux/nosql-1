package ru.itmo.notifications.entrypoint.rest.error

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import ru.itmo.notifications.entrypoint.api.model.ErrorCodeDto
import ru.itmo.notifications.entrypoint.api.model.ErrorResponseDto
import ru.itmo.notifications.service.exceptions.OrderCompletedException
import ru.itmo.notifications.service.exceptions.SessionExpiredException
import ru.itmo.notifications.shared.exceptions.DomainException
import ru.itmo.notifications.shared.exceptions.NotFoundException
import ru.itmo.notifications.shared.exceptions.ValidationException


@RestControllerAdvice
class DomainExceptionHandler {
    @ExceptionHandler(DomainException::class)
    fun handle(exception: DomainException): ResponseEntity<ErrorResponseDto> {
        val (status, code) = when (exception) {
            is SessionExpiredException -> HttpStatus.UNAUTHORIZED to ErrorCodeDto.SESSION_EXPIRED
            is NotFoundException -> HttpStatus.NOT_FOUND to ErrorCodeDto.NOT_FOUND
            is ValidationException -> HttpStatus.BAD_REQUEST to ErrorCodeDto.VALIDATION_FAILED
            is OrderCompletedException -> HttpStatus.CONFLICT to ErrorCodeDto.ORDER_COMPLETED
            else -> throw exception
        }
        return ResponseEntity.status(status).body(ErrorResponseDto(code, exception.message.orEmpty()))
    }
}
