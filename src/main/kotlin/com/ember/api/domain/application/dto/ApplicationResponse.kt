package com.ember.api.domain.application.dto

import com.ember.api.domain.application.entity.Application
import com.ember.api.domain.application.entity.ApplicationStatus
import com.ember.api.domain.application.entity.CompanySize
import com.ember.api.domain.application.entity.CoverLetter
import com.ember.api.domain.application.entity.CoverLetterType
import java.time.LocalDateTime
import java.util.UUID

data class ApplicationResponse(
    val id: UUID,
    val companyName: String,
    val careerLevel: String,
    val deadline: LocalDateTime?,
    val companySize: CompanySize,
    val status: ApplicationStatus,
    val url: String?,
    val coverLetters: List<CoverLetterResponse>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun from(application: Application): ApplicationResponse =
            ApplicationResponse(
                id = application.id!!,
                companyName = application.companyName,
                careerLevel = application.careerLevel,
                deadline = application.deadline,
                companySize = application.companySize,
                status = application.status,
                url = application.url,
                coverLetters = application.coverLetters.map { CoverLetterResponse.from(it) },
                createdAt = application.createdAt,
                updatedAt = application.updatedAt,
            )
    }
}

data class CoverLetterResponse(
    val id: UUID,
    val question: String,
    val answer: String,
    val type: CoverLetterType?,
) {
    companion object {
        fun from(coverLetter: CoverLetter): CoverLetterResponse =
            CoverLetterResponse(
                id = coverLetter.id!!,
                question = coverLetter.question,
                answer = coverLetter.answer,
                type = coverLetter.type,
            )
    }
}
