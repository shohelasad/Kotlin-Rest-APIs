package com.news.app.repository

import com.news.app.entity.Article
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface ArticleRepository : JpaRepository<Article, Long> {
    fun findByPublishDateBetween(startDate: LocalDate, endDate: LocalDate): List<Article>

    fun findByKeywords(keyword: String): List<Article>
}
