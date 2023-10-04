package com.news.app.article

import com.news.app.entity.Article
import com.news.app.repository.ArticleRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate

@ActiveProfiles("test")
@DataJpaTest
class ArticleRepositoryTest {

    @MockBean
    private lateinit var articleRepository: ArticleRepository

    private val startDate = LocalDate.parse("2023-01-01")
    private val endDate = LocalDate.parse("2023-12-31")
    private val keyword = "technology"

    @BeforeEach
    fun setUp() {
        val articles = listOf(
            Article(
                id = 1L,
                header = "Article 1",
                shortDesc = "Description 1",
                text = "Text 1",
                publishDate = LocalDate.now(),
                authors = setOf(),
                keywords = setOf("technology", "science")
            ),
            Article(
                id = 2L,
                header = "Article 2",
                shortDesc = "Description 2",
                text = "Text 2",
                publishDate = LocalDate.now(),
                authors = setOf(),
                keywords = setOf("technology", "math")
            )
        )

        Mockito.`when`(articleRepository.findByPublishDateBetween(startDate, endDate))
            .thenReturn(articles.filter { it.publishDate in startDate..endDate })

        Mockito.`when`(articleRepository.findByKeywords(keyword.lowercase()))
            .thenReturn(articles.filter { article -> article.keywords?.contains(keyword.lowercase()) ?: false })
    }

    @Test
    fun testFindByPublishDateBetween() {
        val result = articleRepository.findByPublishDateBetween(startDate, endDate)
        assert(result.size == 2)
    }

    @Test
    fun testFindByKeywords() {
        val result = articleRepository.findByKeywords(keyword.lowercase())
        assert(result.size == 2)
    }
}
