package com.ember.api.domain.application.dto

import com.ember.api.domain.application.entity.ApplicationStatus
import com.ember.api.domain.application.entity.CompanySize
import com.ember.api.domain.application.entity.CoverLetterType
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

data class CreateApplicationRequest(
    @field:NotBlank(message = "회사명을 입력해주세요.")
    val companyName: String,
    @field:NotBlank(message = "경력을 입력해주세요.")
    val careerLevel: String,
    @field:NotNull(message = "기업 규모를 선택해주세요.")
    val companySize: CompanySize,
    @field:NotNull(message = "상태를 선택해주세요.")
    val status: ApplicationStatus,
    val deadline: LocalDateTime? = null,
    val url: String? = null,
    @field:Valid
    val coverLetters: List<CoverLetterRequest> = emptyList(),
)

data class UpdateApplicationRequest(
    val companyName: String? = null,
    val careerLevel: String? = null,
    val companySize: CompanySize? = null,
    val status: ApplicationStatus? = null,
    val deadline: LocalDateTime? = null,
    val clearDeadline: Boolean = false,
    val url: String? = null,
    @field:Valid
    val coverLetters: List<CoverLetterRequest>? = null,
)

data class CoverLetterRequest(
    @field:NotBlank(message = "자기소개서 항목에 질문을 입력해주세요.")
    val question: String,
    val answer: String = "",
    val type: CoverLetterType? = null,
)
