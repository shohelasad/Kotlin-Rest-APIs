package com.news.app.entity

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

@Entity
@Table(name = "article")
data class Article(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    val id: Long = 0,

    @NotNull
    @Column(length = 255)
    val header: String? = null,

    @Column(length = 255)
    val shortDesc: String? = null,

    val text: String? = null,

    @Column(name = "publish_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    val publishDate: LocalDate? = null,

    @ManyToMany
    @JoinTable(
        name = "article_users",
        joinColumns = [JoinColumn(name = "article_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id")]
    )
    val authors: Set<User>? = HashSet(),

    @ElementCollection
    @Column(length = 50)
    val keywords: Set<String>? = HashSet()
)