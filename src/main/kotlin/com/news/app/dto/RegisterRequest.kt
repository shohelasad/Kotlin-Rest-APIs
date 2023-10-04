package com.news.app.dto

import jakarta.validation.constraints.NotBlank

data class RegisterRequest(
    @field:NotBlank(message = "Email must not be blank")
    val email: String,

    @field:NotBlank(message = "Username must not be blank")
    val username: String,

    @field:NotBlank(message = "Password must not be blank")
    val password: String
)
