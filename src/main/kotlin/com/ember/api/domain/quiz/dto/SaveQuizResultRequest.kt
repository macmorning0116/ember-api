package com.ember.api.domain.quiz.dto

import jakarta.validation.constraints.NotNull

data class SaveQuizResultRequest(
    @field:NotNull(message = "퀴즈 답변이 필요합니다.")
    val answers: Any,
    @field:NotNull(message = "추천 결과가 필요합니다.")
    val recommendations: Any,
)
