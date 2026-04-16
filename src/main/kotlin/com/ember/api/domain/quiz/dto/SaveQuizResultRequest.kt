package com.ember.api.domain.quiz.dto

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class SaveQuizResultRequest(
    @field:NotNull(message = "퀴즈 답변이 필요합니다.")
    @field:Size(max = 50, message = "답변 항목이 너무 많습니다.")
    val answers: Map<String, Any>,
    @field:NotNull(message = "추천 결과가 필요합니다.")
    @field:Size(max = 20, message = "추천 결과가 너무 많습니다.")
    val recommendations: List<Map<String, Any>>,
)
