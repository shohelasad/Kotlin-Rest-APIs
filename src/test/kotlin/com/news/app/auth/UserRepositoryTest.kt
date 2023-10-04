package com.news.app.auth

import com.news.app.entity.User
import com.news.app.repository.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@DataJpaTest
class UserRepositoryTest {
    @Mock
    private lateinit var userRepository: UserRepository

    @BeforeEach
    fun setUp() {
        val username = "testuser"
        val email = "test@example.com"
        val user = User(id = 1, username = username, email = email)

        `when`(userRepository.findByUsername(username))
            .thenReturn(user)

        `when`(userRepository.findByUsernameOrEmail(username, email))
            .thenReturn(user)
    }

    @Test
    fun testFindByUsername() {
        val username = "testuser"
        val user = User(id = 1, username = username, email = "test@example.com")

        `when`(userRepository.findByUsername(username)).thenReturn(user)

        val foundUser = userRepository.findByUsername(username)

        assert(foundUser != null)
        assert(foundUser?.username == username)
    }

    @Test
    fun testFindByUsernameOrEmail() {
        val username = "testuser"
        val email = "test@example.com"
        val user = User(id = 1, username = username, email = email)

        `when`(userRepository.findByUsernameOrEmail(username, email)).thenReturn(user)

        val foundUser = userRepository.findByUsernameOrEmail(username, email)

        assert(foundUser != null)
        assert(foundUser?.username == username)
        assert(foundUser?.email == email)
    }
}