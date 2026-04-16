package com.ember.api.domain.quiz.service

import com.ember.api.domain.quiz.entity.UserQuizResult
import com.ember.api.domain.quiz.repository.UserQuizResultRepository
import com.ember.api.domain.user.entity.User
import com.ember.api.domain.user.repository.UserRepository
import com.ember.api.global.exception.BusinessException
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import tools.jackson.databind.json.JsonMapper
import java.util.Optional
import java.util.UUID

class QuizServiceTest {
    private lateinit var quizService: QuizService
    private lateinit var userQuizResultRepository: UserQuizResultRepository
    private lateinit var userRepository: UserRepository

    private val userId = UUID.randomUUID()
    private lateinit var user: User

    @BeforeEach
    fun setUp() {
        userQuizResultRepository = mockk()
        userRepository = mockk()
        val objectMapper = JsonMapper.builder().build()
        quizService = QuizService(userQuizResultRepository, userRepository, objectMapper)

        user =
            User(
                email = "test@example.com",
                name = "테스트",
                provider = "google",
                providerId = "12345",
            )
    }

    @Test
    @DisplayName("퀴즈 결과 저장 성공")
    fun saveQuizResult() {
        val answers = mapOf("need" to "employment", "status" to "job_seeking")
        val recommendations = listOf(mapOf("name" to "청년 취업 지원", "score" to 5))

        every { userRepository.findById(userId) } returns Optional.of(user)

        val savedSlot = slot<UserQuizResult>()
        every { userQuizResultRepository.save(capture(savedSlot)) } answers { firstArg() }

        quizService.saveQuizResult(userId, answers, recommendations)

        verify { userQuizResultRepository.save(any()) }
        assertTrue(savedSlot.captured.answers.contains("employment"))
        assertTrue(savedSlot.captured.recommendations.contains("청년 취업 지원"))
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 퀴즈 저장 시 예외 발생")
    fun saveQuizResultWithInvalidUser() {
        every { userRepository.findById(userId) } returns Optional.empty()

        assertThrows<BusinessException> {
            quizService.saveQuizResult(userId, mapOf<String, String>(), listOf<String>())
        }
    }
}
