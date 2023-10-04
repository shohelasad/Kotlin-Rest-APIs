package com.news.app.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.news.app.dto.AuthRequest
import com.news.app.dto.AuthResponse
import com.news.app.dto.RegisterRequest
import com.news.app.service.UserService
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var userService: UserService

    @Test
    fun `test register endpoint`() {
        val token: String = getJwtToken("username")
        val authResponse = AuthResponse(
            token
        )
        val registerRequest = RegisterRequest("username@test.com", "username", "password")
        Mockito.`when`(userService.register(registerRequest)).thenReturn(authResponse)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
        ).andExpect(status().isCreated)
    }

    @Test
    fun `test authenticate endpoint`() {
        val token: String = getJwtToken("username")
        val authResponse = AuthResponse(
            token
        )
        val authRequest = AuthRequest("username", "password")
        Mockito.`when`(userService.authenticate(authRequest)).thenReturn(authResponse)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest))
        ).andExpect(status().isOk)
    }

    @Test
    fun `should register a user`() {
        val registerRequest = RegisterRequest("username@test.com", "username", "password")

        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/api/v1/auth/register")
                .content(objectMapper.writeValueAsString(registerRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated)

        Mockito.verify(userService).register(registerRequest)
    }

    @Test
    fun `should authenticate a user`() {
        val authRequest = AuthRequest(username = "testuser", password = "testpassword")

        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/api/v1/auth/authenticate")
                .content(objectMapper.writeValueAsString(authRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)

        Mockito.verify(userService).authenticate(authRequest)
    }

    @Test
    fun `should handle exceptions during registration`() {
        val registerRequest = RegisterRequest(username = "testuser", email="testuser@email.com", password = "testpassword")
        Mockito.`when`(userService.register(registerRequest)).thenThrow(RuntimeException("Registration failed"))

        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/api/v1/auth/register")
                .content(objectMapper.writeValueAsString(registerRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isInternalServerError)
    }

    private fun getJwtToken(subject: String): String {
        val secretKey = "77397A24432646294A404E635266556A586E3272357538782F4125442A472D4B"
        val expirationTime = System.currentTimeMillis() + 3600000

        return Jwts.builder()
            .setSubject(subject)
            .setExpiration(Date(expirationTime))
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact()
    }
}
