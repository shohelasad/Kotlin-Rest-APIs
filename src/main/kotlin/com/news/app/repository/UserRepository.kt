package com.news.app.repository

import com.news.app.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): User?

    fun findByUsernameOrEmail(username: String, email: String): User?
}
