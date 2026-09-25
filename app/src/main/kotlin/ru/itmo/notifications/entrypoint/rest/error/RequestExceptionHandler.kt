package ru.itmo.notifications.entrypoint.rest.error

import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import ru.itmo.notifications.entrypoint.api.model.ErrorCodeDto
import ru.itmo.notifications.entrypoint.api.model.ErrorResponseDto


@RestControllerAdvice
class RequestExceptionHandler {
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleUnreadableBody(): ResponseEntity<ErrorResponseDto> =
        badRequest("Тело запроса не соответствует контракту")

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleParameterMismatch(
        exception: MethodArgumentTypeMismatchException,
    ): ResponseEntity<ErrorResponseDto> =
        badRequest("Параметр ${exception.name} имеет недопустимое значение")

    private fun badRequest(message: String) =
        ResponseEntity.badRequest().body(ErrorResponseDto(ErrorCodeDto.VALIDATION_FAILED, message))
}
