package com.news.app.controller;

import com.news.app.dto.ArticleRequest
import com.news.app.dto.ArticleResponse
import com.news.app.service.ArticleService
import jakarta.validation.Valid
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/articles")
@Validated
class ArticleController(private val articleService: ArticleService) {
    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(ArticleController::class.java)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createArticle(@RequestBody @Valid request: ArticleRequest): ResponseEntity<ArticleResponse> {
        val articleResponse = articleService.save(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(articleResponse)
    }

    @PutMapping("/{id}")
    fun updateArticle(@PathVariable id: Long, @RequestBody @Valid request: ArticleRequest): ResponseEntity<ArticleResponse> {
        val articleResponse = articleService.update(id, request)
        return ResponseEntity.status(HttpStatus.OK).body(articleResponse)
    }

    @GetMapping("/{id}")
    fun getArticleById(@PathVariable id: Long): ResponseEntity<ArticleResponse> {
        return ResponseEntity.status(HttpStatus.OK).body(articleService.findById(id))
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteArticle(@PathVariable id: Long) {
        articleService.deleteById(id)
    }

    @GetMapping("/author/{authorName}")
    fun getArticlesByAuthor(@PathVariable authorName: String): ResponseEntity<List<ArticleResponse>> {
        return ResponseEntity.status(HttpStatus.OK).body(articleService.findByAuthor(authorName))
    }

    @GetMapping("/period")
    fun getArticlesByPeriod(
        @RequestParam(value = "startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate,
        @RequestParam(value = "endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate
    ): ResponseEntity<List<ArticleResponse>> {
        return ResponseEntity.status(HttpStatus.OK).body(articleService.findByPeriod(startDate, endDate))
    }

    @GetMapping("/search")
    fun getArticlesByKeyword(@RequestParam(value = "keyword") keyword: String): ResponseEntity<List<ArticleResponse>> {
        return ResponseEntity.status(HttpStatus.OK).body(articleService.findByKeyword(keyword))
    }
}