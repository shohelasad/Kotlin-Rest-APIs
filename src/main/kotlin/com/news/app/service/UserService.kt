package com.news.app.service

import com.news.app.dto.AuthRequest
import com.news.app.dto.AuthResponse
import com.news.app.dto.RegisterRequest
import com.news.app.entity.User
import com.news.app.exception.RegisteredException
import com.news.app.repository.UserRepository
import com.news.app.security.JwtService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager
) {
    fun register(registerRequest: RegisterRequest): AuthResponse {
        if (userRepository.findByUsernameOrEmail(registerRequest.username, registerRequest.email) != null) {
            throw RegisteredException()
        }
        val user = User(
            email = registerRequest.email,
            username = registerRequest.username,
            password = passwordEncoder.encode(registerRequest.password)
        )
        userRepository.save(user)
        val token = jwtService.generateToken(user)

        return AuthResponse(token)
    }

    fun authenticate(authRequest: AuthRequest): AuthResponse {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                authRequest.username,
                authRequest.password
            )
        )
        val user = userRepository.findByUsername(authRequest.username)

        return AuthResponse(jwtService.generateToken(user!!))
    }
}
