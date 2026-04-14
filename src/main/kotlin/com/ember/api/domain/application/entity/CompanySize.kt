package com.ember.api.domain.application.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class CompanySize(
    @JsonValue val label: String,
) {
    LARGE("대기업"),
    MID_LARGE("중견기업"),
    MID("중소기업"),
    STARTUP("스타트업"),
    ;

    companion object {
        @JsonCreator
        @JvmStatic
        fun fromLabel(label: String): CompanySize =
            entries.find { it.label == label }
                ?: throw IllegalArgumentException("Unknown CompanySize: $label")
    }
}
