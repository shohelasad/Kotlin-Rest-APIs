package com.news.app.article

import com.news.app.dto.ArticleRequest
import com.news.app.entity.Article
import com.news.app.entity.User
import com.news.app.exception.ResourceNotFoundException
import com.news.app.repository.ArticleRepository
import com.news.app.repository.UserRepository
import com.news.app.service.ArticleService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import java.util.*

@ActiveProfiles("test")
class ArticleServiceTest {
    private lateinit var articleService: ArticleService
    private lateinit var articleRepository: ArticleRepository
    private lateinit var userRepository: UserRepository

    @BeforeEach
    fun setUp() {
        articleRepository = Mockito.mock(ArticleRepository::class.java)
        userRepository = Mockito.mock(UserRepository::class.java)
        articleService = ArticleService(articleRepository, userRepository)

        val username = "testUser"
        val user = User(id = 1L, username = username, email = "test@example.com", password = "password")

        val authentication = UsernamePasswordAuthenticationToken(user, null)
        val securityContext = Mockito.mock(SecurityContext::class.java)

        SecurityContextHolder.setContext(securityContext)
        `when`(securityContext.authentication).thenReturn(authentication)
        `when`(userRepository.findByUsername(username)).thenReturn(user)
    }

    @Test
    fun testSaveArticle() {
        val user = User(id = 1L, username = "user1", email = "user1@example.com")
        val articleRequest = ArticleRequest(
            header = "Test Article",
            shortDesc = "Short description",
            text = "Sample text",
            keywords = setOf("test", "sample")
        )
        `when`(userRepository.findByUsername(user.username)).thenReturn(user)
        `when`(articleRepository.save(Mockito.any(Article::class.java))).thenAnswer {
            val savedArticle = it.arguments[0] as Article
            savedArticle.copy(id = 1L, publishDate = LocalDate.now())
        }

        val savedArticleResponse = articleService.save(articleRequest)

        assertEquals("Test Article", savedArticleResponse.header)
        assertEquals("Short description", savedArticleResponse.shortDesc)
        assertEquals("Sample text", savedArticleResponse.text)
        assertEquals(1, savedArticleResponse.authors?.size)
        assertEquals(LocalDate.now(), savedArticleResponse.publishDate)
    }

    @Test
    fun testUpdateArticle() {
        val user = User(id = 1L, username = "user1", email = "user1@example.com")
        val articleId = 1L
        val articleRequest = ArticleRequest(
            header = "Updated Article",
            shortDesc = "Updated description",
            text = "Updated text",
            keywords = setOf("updated", "text")
        )
        val existingArticle = Article(
            id = articleId,
            header = "Original Article",
            shortDesc = "Original description",
            text = "Original text",
            authors = setOf(user),
            keywords = setOf("original", "text"),
            publishDate = LocalDate.now()
        )
        `when`(userRepository.findByUsername(user.username)).thenReturn(user)
        `when`(articleRepository.findById(articleId)).thenReturn(Optional.of(existingArticle))
        `when`(articleRepository.save(Mockito.any(Article::class.java))).thenAnswer {
            val updatedArticle = it.arguments[0] as Article
            updatedArticle.copy(header = "Updated Article", shortDesc = "Updated description")
        }

        val updatedArticleResponse = articleService.update(articleId, articleRequest)

        assertEquals("Updated Article", updatedArticleResponse.header)
        assertEquals("Updated description", updatedArticleResponse.shortDesc)
        assertEquals("Updated text", updatedArticleResponse.text)
    }

    @Test
    fun testUpdateArticleNotFound() {
        val articleId = 1L
        val articleRequest = ArticleRequest(
            header = "Updated Article",
            shortDesc = "Updated description",
            text = "Updated text",
            keywords = setOf("updated", "text")
        )

        `when`(articleRepository.findById(articleId)).thenReturn(Optional.empty())

        Assertions.assertThrows(ResourceNotFoundException::class.java) {
            articleService.update(articleId, articleRequest)
        }
    }

    @Test
    fun testDeleteById() {
        val articleId = 1L
        Mockito.doNothing().`when`(articleRepository).deleteById(articleId)
        articleService.deleteById(articleId)
        Mockito.verify(articleRepository, Mockito.times(1)).deleteById(articleId)
    }

    @Test
    fun testFindById() {
        val articleId = 1L
        val article = Article(
            id = articleId,
            header = "Sample Article",
            shortDesc = "Short description",
            text = "Lorem ipsum dolor sit amet.",
            publishDate = LocalDate.now(),
            authors = setOf(),
            keywords = setOf("keyword1", "keyword2")
        )
        `when`(articleRepository.findById(articleId)).thenReturn(Optional.of(article))
        val result = articleService.findById(articleId)
        Mockito.verify(articleRepository, Mockito.times(1)).findById(articleId)

        assert(result.id == article.id)
        assert(result.header == article.header)
        assert(result.shortDesc == article.shortDesc)
    }

    @Test
    fun testFindByAuthor() {
        val authorName = "author1"
        var author = User(
            id = 1L,
            username = "author1",
            email = "author1@example.com"
        )
        val articles = setOf(
            Article(
                id = 1L,
                header = "Article 1",
                shortDesc = "Description 1",
                text = "Text 1",
                publishDate = LocalDate.now(),
                authors = setOf(User(1L, authorName, "author1@example.com")),
                keywords = emptySet()
            ),
            Article(
                id = 2L,
                header = "Article 2",
                shortDesc = "Description 2",
                text = "Text 2",
                publishDate = LocalDate.now(),
                authors = setOf(User(1L, authorName, "author1@example.com")),
                keywords = emptySet()
            )
        )
        author.articles = articles

        Mockito.`when`(userRepository.findByUsername(authorName)).thenReturn(author)

        val result = articleService.findByAuthor(authorName)
        assert(result.size == 2)
        assert(result[0].header == "Article 1")
        assert(result[1].header == "Article 2")
    }

    @Test
    fun testFindByPeriod() {
        val startDate = LocalDate.parse("2023-01-01")
        val endDate = LocalDate.parse("2023-12-31")
        val articles = listOf(
            Article(
                id = 1L,
                header = "Article 1",
                shortDesc = "Description 1",
                text = "Text 1",
                publishDate = LocalDate.now(),
                authors = setOf(),
                keywords = setOf()
            ),
            Article(
                id = 2L,
                header = "Article 2",
                shortDesc = "Description 2",
                text = "Text 2",
                publishDate = LocalDate.now(),
                authors = setOf(),
                keywords = setOf()
            )
        )
        Mockito.`when`(articleRepository.findByPublishDateBetween(startDate, endDate)).thenReturn(articles)

        val result = articleService.findByPeriod(startDate, endDate)

        Mockito.verify(articleRepository, Mockito.times(1)).findByPublishDateBetween(startDate, endDate)

        assert(result.size == 2)
    }

    @Test
    fun testFindByKeyword() {
        val keyword = "technology"
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
        `when`(articleRepository.findByKeywords(keyword.lowercase())).thenReturn(articles)

        val result = articleService.findByKeyword(keyword)

        Mockito.verify(articleRepository, Mockito.times(1)).findByKeywords(keyword.lowercase())

        assert(result.size == 2)
    }
}
