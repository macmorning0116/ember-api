package com.ember.api.domain.application.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class ApplicationStatus(
    @JsonValue val label: String,
) {
    PENDING("지원 예정"),
    CODING_TEST("코테 기간"),
    INTERVIEW("면접 기간"),
    APPLIED("지원 완료"),
    ACCEPTED("최종 합격"),
    REJECTED_DOCS("서류 탈락"),
    REJECTED_CODING("코테 탈락"),
    REJECTED_INTERVIEW("면접 탈락"),
    ;

    companion object {
        @JsonCreator
        @JvmStatic
        fun fromLabel(label: String): ApplicationStatus =
            entries.find { it.label == label }
                ?: throw IllegalArgumentException("Unknown ApplicationStatus: $label")
    }
}
