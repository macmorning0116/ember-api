package com.ember.api.domain.schedule.dto

import com.ember.api.domain.schedule.entity.ScheduleEventType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime
import java.util.UUID

data class CreateScheduleRequest(
    @field:NotBlank(message = "일정 제목을 입력해주세요.")
    val title: String,
    @field:NotNull(message = "일정 날짜를 선택해주세요.")
    val date: LocalDateTime,
    @field:NotNull(message = "일정 유형을 선택해주세요.")
    val type: ScheduleEventType,
    val memo: String? = null,
    val applicationId: UUID? = null,
)
