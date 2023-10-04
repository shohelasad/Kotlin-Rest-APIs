package com.news.app.dto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ArticleRequest(
    val header: String?,
    val shortDesc: String?,
    val text: String?,
    val keywords: Set<String>?
) {
    init {
        requireNotNull(header) { "Header must not be null" }
    }
}
