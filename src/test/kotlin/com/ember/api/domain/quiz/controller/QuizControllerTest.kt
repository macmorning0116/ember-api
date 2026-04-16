package com.ember.api.domain.quiz.controller

import com.ember.api.domain.quiz.dto.SaveQuizResultRequest
import com.ember.api.domain.quiz.service.QuizService
import com.ember.api.global.security.CurrentUser
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.util.UUID

class QuizControllerTest {
    private lateinit var controller: QuizController
    private lateinit var quizService: QuizService
    private val userId = UUID.randomUUID()

    @BeforeEach
    fun setUp() {
        quizService = mockk()
        controller = QuizController(quizService)
        mockkObject(CurrentUser)
        every { CurrentUser.getUserId() } returns userId
    }

    @AfterEach
    fun tearDown() {
        unmockkObject(CurrentUser)
    }

    @Test
    @DisplayName("퀴즈 결과 저장 성공")
    fun saveQuizResult() {
        justRun { quizService.saveQuizResult(userId, any(), any()) }

        val request =
            SaveQuizResultRequest(
                answers = mapOf("need" to "employment"),
                recommendations = listOf(mapOf("name" to "청년 취업")),
            )
        val response = controller.saveQuizResult(request)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertTrue(response.body!!.success)
    }
}
