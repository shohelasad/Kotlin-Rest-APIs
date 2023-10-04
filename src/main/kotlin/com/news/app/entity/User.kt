package com.news.app.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.HashSet


@Entity
@Table(name = "users")
class User(

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    var id: Long = 0,

    @NotNull
    @Column(length = 100)
    var email: String = "",

    @NotNull
    @Column(length = 100)
    private var username: String = "",

    @NotNull
    @Column(length = 100)
    private var password: String = "",

    @Size(max = 20)
    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 20)
    var role: RoleEnum = RoleEnum.EDITOR, //Role Editor User

    @ManyToMany(mappedBy = "authors")
    var articles: Set<Article>? = HashSet()

    ) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(SimpleGrantedAuthority(this.role.name))
    }

    override fun getPassword(): String {
        return this.password
    }

    override fun getUsername(): String {
        return this.username
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

}

enum class RoleEnum {
    USER, EDITOR, ADMIN
}