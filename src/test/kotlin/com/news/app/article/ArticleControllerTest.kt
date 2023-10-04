package com.news.app.article

import com.fasterxml.jackson.databind.ObjectMapper
import com.news.app.controller.ArticleController
import com.news.app.dto.ArticleRequest
import com.news.app.dto.ArticleResponse
import com.news.app.dto.AuthorResponse
import com.news.app.entity.User
import com.news.app.repository.UserRepository
import com.news.app.security.JwtService
import com.news.app.service.ArticleService
import org.hamcrest.CoreMatchers.hasItem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.ComponentScan
import org.springframework.http.MediaType
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate

@ActiveProfiles("test")
@ComponentScan(basePackages = ["com.news.app.security"])
@WebMvcTest(ArticleController::class)
class ArticleControllerTest {
    private val apiUrl = "/api/v1/articles/"

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var jwtService: JwtService

    @MockBean
    private lateinit var articleService: ArticleService

    @MockBean
    private lateinit var userDetailsService: UserDetailsService

    @MockBean
    private lateinit var userRepository: UserRepository

    private lateinit var token: String
    private lateinit var articleRequest: ArticleRequest
    private lateinit var articleResponse: ArticleResponse

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        val keywords = setOf("technology", "science")
        val author1 = AuthorResponse(1L, "author1", "author1@example.com")
        val author2 = AuthorResponse(2L, "author2", "author2@example.com")
        val authors = setOf(author1, author2)

        articleRequest = ArticleRequest(
            header = "Sample Article",
            shortDesc = "Short description",
            text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
            keywords = keywords
        )

        articleResponse = ArticleResponse(
            id = 1L,
            header = "Sample Article",
            shortDesc = "Short description",
            text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
            publishDate = LocalDate.parse("2023-10-01"),
            authors = authors,
            keywords = keywords
        )

        val user = User(
            email = "username@test.com",
            username = "username",
            password = passwordEncoder.encode("123456")
        )

        Mockito.`when`(articleService.findById(1L)).thenReturn(articleResponse)
        Mockito.`when`(articleService.save(articleRequest)).thenReturn(articleResponse)
        Mockito.`when`(articleService.update(1L, articleRequest)).thenReturn(articleResponse)
        Mockito.`when`(userDetailsService.loadUserByUsername("username")).thenReturn(user)

        token = jwtService.generateToken(user)
    }

    @Test
    fun testCreateArticle() {
        performCreateArticleRequest("/api/v1/articles", articleRequest, token)
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.header").value(articleRequest.header))
            .andExpect(jsonPath("$.shortDesc").value(articleRequest.shortDesc))
    }

    @Test
    fun testUpdateArticle() {
        val id = 1L
        performUpdateArticleRequest(apiUrl, id, articleRequest, token)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.header").value(articleRequest.header))
            .andExpect(jsonPath("$.shortDesc").value(articleRequest.shortDesc))
    }

    @Test
    fun testGetArticleById() {
        val id = 1L
        performGetArticleRequest(apiUrl, id, articleRequest, token)
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.header").value(articleResponse.header))
            .andExpect(jsonPath("$.shortDesc").value(articleResponse.shortDesc))
    }

    @Test
    fun testDeleteArticle() {
        val id = 1L
        performDeleteArticleRequest(apiUrl, id, articleRequest, token)
            .andExpect(MockMvcResultMatchers.status().isNoContent())
    }

    @Test
    fun testGetArticlesByAuthor() {
        val authorName = "author1"
        val author = AuthorResponse(1L, "author1", "author1@test.com")
        val articleResponseList = listOf(
            ArticleResponse(1L, "Header 1", "Desc 1", "Text 1", LocalDate.now(), setOf(author), emptySet()),
            ArticleResponse(2L, "Header 2", "Desc 2", "Text 2", LocalDate.now(), setOf(author), emptySet())
        )
        val user = User(
            id = 1L,
            email = "author1@test.com",
            username = authorName,
            password = passwordEncoder.encode("123456")
        )

        Mockito.`when`(userRepository.findByUsername(authorName)).thenReturn(user)
        Mockito.`when`(articleService.findByAuthor(authorName)).thenReturn(articleResponseList)

        mockMvc.perform(get("/api/v1/articles/author/{authorId}", authorName)
            .header("Authorization", "Bearer $token"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].header").value("Header 1"))
            .andExpect(jsonPath("$[0].authors[0].username").value(authorName))
            .andExpect(jsonPath("$[0].authors[0].email").value("author1@test.com"))
    }


    @Test
    fun testGetArticlesByPeriod() {
        val startDate = LocalDate.parse("2023-01-01")
        val endDate = LocalDate.parse("2023-12-31")
        val articleResponseList = listOf(
            ArticleResponse(1L, "Header 1", "Desc 1", "Text 1", LocalDate.now(), emptySet(), emptySet()),
            ArticleResponse(2L, "Header 2", "Desc 2", "Text 2", LocalDate.now(), emptySet(), emptySet())
        )

        Mockito.`when`(articleService.findByPeriod(startDate, endDate)).thenReturn(articleResponseList)

        mockMvc.perform(get("/api/v1/articles/period")
            .param("startDate", startDate.toString())
            .param("endDate", endDate.toString())
            .header("Authorization", "Bearer $token"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].header").value("Header 1"))
            .andExpect(jsonPath("$[0].publishDate").value(LocalDate.now().toString()))
    }

    @Test
    fun testGetArticlesByKeyword() {
        val keywords = setOf("technology", "science")
        val keyword = "technology"
        val articleResponseList = listOf(
            ArticleResponse(1L, "Header 1", "Desc 1", "Text 1", LocalDate.now(), emptySet(), keywords),
            ArticleResponse(2L, "Header 2", "Desc 2", "Text 2", LocalDate.now(), emptySet(), keywords)
        )

        Mockito.`when`(articleService.findByKeyword(keyword)).thenReturn(articleResponseList)

        mockMvc.perform(get("/api/v1/articles/search?keyword=$keyword")
            .header("Authorization", "Bearer $token"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].header").value("Header 1"))
            .andExpect(jsonPath("$[0].keywords").isArray)
            .andExpect(jsonPath("$[0].keywords", hasItem("technology")))
            .andExpect(jsonPath("$[0].keywords", hasItem("science")))
    }

    private fun performCreateArticleRequest(url: String, articleRequest: ArticleRequest, token: String): ResultActions {
        return mockMvc.perform(
            post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(articleRequest))
                .header("Authorization", "Bearer $token")
        )
    }

    private fun performUpdateArticleRequest(url: String, id: Long, articleRequest: ArticleRequest, token: String): ResultActions {
        return mockMvc.perform(
            put(url + "$id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(articleRequest))
                .header("Authorization", "Bearer $token")
        )
    }

    private fun performGetArticleRequest(url: String, id: Long, articleRequest: ArticleRequest, token: String): ResultActions {
        return mockMvc.perform(
            get(url + "$id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(articleRequest))
                .header("Authorization", "Bearer $token")
        )
    }

    private fun performDeleteArticleRequest(url: String, id: Long, articleRequest: ArticleRequest, token: String): ResultActions {
        return mockMvc.perform(
            delete(url + "$id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(articleRequest))
                .header("Authorization", "Bearer $token")
        )
    }
}
