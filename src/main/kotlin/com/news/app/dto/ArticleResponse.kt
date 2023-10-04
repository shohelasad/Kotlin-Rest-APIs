package com.news.app.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDate

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ArticleResponse(
    val id: Long?,
    val header: String?,
    val shortDesc: String?,
    val text: String?,
    @JsonFormat(pattern = "yyyy-MM-dd")
    val publishDate: LocalDate? = null,
    val authors: Set<AuthorResponse>?,
    val keywords: Set<String>?
) {
    init {
        requireNotNull(header) { "Header must not be null" }
    }
}