package com.ember.api.domain.application.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class CoverLetterType(
    @JsonValue val label: String,
) {
    MOTIVATION("지원 동기"),
    GROWTH("성장 과정"),
    COMPETENCY("직무 역량"),
    PERSONALITY("성격 장단점"),
    SUCCESS("성공 경험"),
    FAILURE("실패 경험"),
    TEAMWORK("팀워크 경험"),
    ASPIRATION("입사 후 포부"),
    OTHER("기타"),
    ;

    companion object {
        @JsonCreator
        @JvmStatic
        fun fromLabel(label: String): CoverLetterType =
            entries.find { it.label == label }
                ?: throw IllegalArgumentException("Unknown CoverLetterType: $label")
    }
}
