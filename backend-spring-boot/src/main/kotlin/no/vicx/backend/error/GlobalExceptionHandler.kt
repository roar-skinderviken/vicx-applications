package no.vicx.backend.error

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationException(
        exception: MethodArgumentNotValidException,
        request: HttpServletRequest,
    ) = ApiError(
        status = HttpStatus.BAD_REQUEST.value(),
        message = VALIDATION_ERROR_MSG,
        url = request.servletPath,
        validationErrors =
            exception.bindingResult.fieldErrors
                .associate { it.field to (it.defaultMessage ?: "Unknown error") },
    )

    @ExceptionHandler(ConstraintViolationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleConstraintViolationException(
        exception: ConstraintViolationException,
        request: HttpServletRequest,
    ) = ApiError(
        status = HttpStatus.BAD_REQUEST.value(),
        message = VALIDATION_ERROR_MSG,
        url = request.servletPath,
        validationErrors =
            exception.constraintViolations
                .associate { (it.propertyPath.last().name ?: FALLBACK_PROPERTY_PATH) to it.message },
    )

    companion object {
        const val VALIDATION_ERROR_MSG = "validation error"
        const val FALLBACK_PROPERTY_PATH = "ROOT"
    }
}
