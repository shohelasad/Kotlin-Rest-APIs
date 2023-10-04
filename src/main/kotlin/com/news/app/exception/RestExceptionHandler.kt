package com.news.app.exception

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.ErrorResponse
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.client.ResourceAccessException

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
    fun handleValidationErrors(exception: MethodArgumentNotValidException): ResponseError {
        val validationErrors = exception.bindingResult.fieldErrors.map {
            ValidationError(it.field, it.defaultMessage ?: "Validation failed")
        }
        log.error(validationErrors.toString())
        val errorResponse = ErrorResponse("Validation failed", validationErrors)
        return ResponseError.BAD_REQUEST
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
