package com.ember.api.domain.quiz.service

import com.ember.api.domain.quiz.entity.UserQuizResult
import com.ember.api.domain.quiz.repository.UserQuizResultRepository
import com.ember.api.domain.user.repository.UserRepository
import com.ember.api.global.exception.BusinessException
import com.ember.api.global.exception.ErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tools.jackson.databind.ObjectMapper
import java.util.UUID

@Service
class QuizService(
    private val userQuizResultRepository: UserQuizResultRepository,
    private val userRepository: UserRepository,
    private val objectMapper: ObjectMapper,
) {
    @Transactional
    fun saveQuizResult(
        userId: UUID,
        answers: Any,
        recommendations: Any,
    ) {
        val user =
            userRepository
                .findById(userId)
                .orElseThrow { BusinessException(ErrorCode.UNAUTHORIZED) }

        userQuizResultRepository.save(
            UserQuizResult(
                user = user,
                answers = objectMapper.writeValueAsString(answers),
                recommendations = objectMapper.writeValueAsString(recommendations),
            ),
        )
    }
}
