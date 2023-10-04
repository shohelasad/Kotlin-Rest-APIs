package com.news.app.auth

import com.news.app.dto.AuthRequest
import com.news.app.dto.RegisterRequest
import com.news.app.entity.User
import com.news.app.exception.RegisteredException
import com.news.app.repository.UserRepository
import com.news.app.security.JwtService
import com.news.app.service.UserService
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.security.authentication.AuthenticationManager

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import java.util.*

@ActiveProfiles("test")
class UserServiceTest {

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    @Mock
    private lateinit var jwtService: JwtService

    @Mock
    private lateinit var authenticationManager: AuthenticationManager

    @InjectMocks
    private lateinit var userService: UserService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun testRegisterWithUser() {
        val registerRequest = RegisterRequest("test@example.com", "testuser", "password")

        `when`(userRepository.findByUsernameOrEmail(registerRequest.username, registerRequest.email)).thenReturn(User())

        assertThrows(RegisteredException::class.java) {
            userService.register(registerRequest)
        }
    }

    @Test
    fun testAuthenticate() {
        val authRequest = AuthRequest("testuser", "password")
        val user = User(
            email = "test@example.com",
            username = "testuser",
            password = "encoded_password"
        )

        `when`(userRepository.findByUsername(authRequest.username)).thenReturn(user)
        `when`(jwtService.generateToken(user)).thenReturn("test_token")

        val authResponse = userService.authenticate(authRequest)

        assertEquals("test_token", authResponse.token)
    }

    @Test
    fun testAuthenticateWithInvalidUser() {
        val authRequest = AuthRequest("testuser", "password")

        `when`(userRepository.findByUsername(authRequest.username)).thenReturn(null)

        val exception = assertThrows<NullPointerException> {
            userService.authenticate(authRequest)
        }
    }
}