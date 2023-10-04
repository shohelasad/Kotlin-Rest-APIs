package com.news.app.service

import com.news.app.dto.ArticleRequest
import com.news.app.dto.ArticleResponse
import com.news.app.dto.AuthorResponse
import com.news.app.entity.Article
import com.news.app.entity.User
import com.news.app.exception.ResourceNotFoundException
import com.news.app.repository.ArticleRepository
import com.news.app.repository.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class ArticleService(
    private val articleRepository: ArticleRepository,
    private val userRepository: UserRepository
) {
    private val logger: Logger = LoggerFactory.getLogger(ArticleService::class.java)

    fun save(articleRequest: ArticleRequest): ArticleResponse {
        val user = getCurrentUser() ?: throw ResourceNotFoundException("User not found")
        val article = Article(
            header = articleRequest.header,
            shortDesc = articleRequest.shortDesc,
            text = articleRequest.text,
            authors = setOf(user),
            keywords = articleRequest.keywords,
            publishDate = LocalDate.now()
        )

        val savedArticle = articleRepository.save(article)
        return convertToArticleDto(savedArticle)
    }

    fun update(id: Long, articleRequest: ArticleRequest): ArticleResponse {
        val user = getCurrentUser() ?: throw ResourceNotFoundException("User not found")

        val existingArticle = articleRepository.findById(id)
            .orElseThrow {
                logger.info("Article not found for this id: $id")
                ResourceNotFoundException("Article not found!")
            }

        val updatedArticle = existingArticle.copy(
            header = articleRequest.header,
            shortDesc = articleRequest.shortDesc,
            text = articleRequest.text,
            authors = existingArticle.authors?.plus(user),
            keywords = articleRequest.keywords
        )

        return convertToArticleDto(articleRepository.save(updatedArticle))
    }

    fun deleteById(id: Long) {
        articleRepository.deleteById(id)
    }

    fun findById(id: Long): ArticleResponse {
        val article = articleRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Article not found with id: $id") }
        return convertToArticleDto(article)
    }

    fun findByAuthor(authorName: String): List<ArticleResponse> {
        val author = userRepository.findByUsername(authorName.lowercase())
        return author?.articles
            ?.map { convertToArticleDto(it) } ?: emptyList()
    }

    fun findByPeriod(startDate: LocalDate, endDate: LocalDate): List<ArticleResponse> {
        val articles = articleRepository.findByPublishDateBetween(startDate, endDate)
        return articles.map { convertToArticleDto(it) }
    }

    fun findByKeyword(keyword: String): List<ArticleResponse> {
        val articles = articleRepository.findByKeywords(keyword.lowercase())
        return articles.map { convertToArticleDto(it) }
    }

    private fun convertToArticleDto(article: Article): ArticleResponse {
        val authors = article.authors?.map { convertToAuthorDto(it) }?.toSet()
        return ArticleResponse(
            article.id,
            article.header,
            article.shortDesc,
            article.text,
            article.publishDate,
            authors,
            article.keywords
        )
    }

    private fun convertToAuthorDto(user: User): AuthorResponse {
        return AuthorResponse(
            id = user.id,
            username = user.username,
            email = user.email
        )
    }

    private fun getCurrentUser(): User? {
        val authentication: Authentication = SecurityContextHolder.getContext().authentication
        val user = authentication.principal as User
        return userRepository.findByUsername(user.username)
    }
}
