package com.news.app.controller

import com.news.app.dto.AuthRequest
import com.news.app.dto.RegisterRequest
import com.news.app.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
@Validated
class AuthController(
    private val userService: UserService,
) {
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@Valid @RequestBody registerRequest: RegisterRequest) {
        userService.register(registerRequest)
    }

    @PostMapping("/authenticate")
    @ResponseStatus(HttpStatus.OK)
    fun authenticate(@Valid @RequestBody authRequest: AuthRequest) {
        userService.authenticate(authRequest)
    }
}
