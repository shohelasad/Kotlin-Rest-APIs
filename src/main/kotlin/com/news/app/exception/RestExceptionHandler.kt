package com.news.app.exception

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus.*
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class RestExceptionHandler {

    val log: Logger = LoggerFactory.getLogger(RestExceptionHandler::class.java.name)

    @ExceptionHandler
    @ResponseStatus(CONFLICT)
    fun handleRegisteredException(e: RegisteredException): ResponseError {
        log.error(e.message, e)
        return ResponseError.ALREADY_REGISTERED
    }

    @ExceptionHandler
    @ResponseStatus(UNAUTHORIZED)
    fun handleBadCredentialsException(e: BadCredentialsException): ResponseError {
        log.error(e.message, e)
        return ResponseError.BAD_CREDENTIALS
    }

    @ExceptionHandler
    @ResponseStatus(NOT_FOUND)
    fun handleResourceNotFoundException(e: ResourceNotFoundException): ResponseError {
        log.error(e.message, e)
        return ResponseError.NOT_FOUND
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(BAD_REQUEST)
    fun handleValidationErrors(exception: MethodArgumentNotValidException): com.news.app.exception.ErrorResponse {
        val validationErrors = exception.bindingResult.allErrors.map {
            when (it) {
                is FieldError -> ValidationError(it.field, it.defaultMessage ?: "Validation failed")
                is ObjectError -> ValidationError(it.objectName, it.defaultMessage ?: "Validation failed")
                else -> ValidationError("Validation failed", "Validation failed")
            }
        }

        val errorMessages = validationErrors.map { it.message }
        log.error(errorMessages.joinToString(", "))

        return ErrorResponse("Validation failed", validationErrors)
    }

    @ExceptionHandler
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    fun globalExceptionHandler(e: Exception): ResponseError {
        log.error(e.message, e)
        return ResponseError.INTERNAL_SERVER_ERROR
    }
}

class RegisteredException : Exception()

class ResourceNotFoundException(message: String) : RuntimeException(message)

data class ValidationError(val field: String, val message: String)

data class ErrorResponse(val message: String, val errors: List<ValidationError>)

