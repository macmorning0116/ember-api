package com.ember.api.domain.quiz.controller

import com.ember.api.domain.quiz.dto.SaveQuizResultRequest
import com.ember.api.domain.quiz.service.QuizService
import com.ember.api.global.response.ApiResponse
import com.ember.api.global.security.CurrentUser
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/quiz")
class QuizController(
    private val quizService: QuizService,
) {
    @PostMapping("/result")
    fun saveQuizResult(
        @Valid @RequestBody request: SaveQuizResultRequest,
    ): ResponseEntity<ApiResponse<Map<String, Boolean>>> {
        val userId = CurrentUser.getUserId()
        quizService.saveQuizResult(userId, request.answers, request.recommendations)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success(mapOf("success" to true)))
    }
}
