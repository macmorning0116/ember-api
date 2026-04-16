package com.ember.api.domain.application.service

import com.ember.api.domain.application.dto.ApplicationResponse
import com.ember.api.domain.application.dto.CreateApplicationRequest
import com.ember.api.domain.application.dto.UpdateApplicationRequest
import com.ember.api.domain.application.entity.Application
import com.ember.api.domain.application.entity.CoverLetter
import com.ember.api.domain.application.repository.ApplicationRepository
import com.ember.api.domain.user.repository.UserRepository
import com.ember.api.global.exception.BusinessException
import com.ember.api.global.exception.ErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class ApplicationService(
    private val applicationRepository: ApplicationRepository,
    private val userRepository: UserRepository,
) {
    fun getApplications(userId: UUID): List<ApplicationResponse> =
        applicationRepository
            .findByUserIdOrderByCreatedAtDesc(userId)
            .map { ApplicationResponse.from(it) }

    fun getApplication(
        id: UUID,
        userId: UUID,
    ): ApplicationResponse {
        val application = findApplicationByIdAndUserId(id, userId)
        return ApplicationResponse.from(application)
    }

    @Transactional
    fun createApplication(
        userId: UUID,
        request: CreateApplicationRequest,
    ): ApplicationResponse {
        val user =
            userRepository
                .findById(userId)
                .orElseThrow { BusinessException(ErrorCode.UNAUTHORIZED) }

        val application =
            Application(
                user = user,
                companyName = request.companyName,
                careerLevel = request.careerLevel,
                deadline = request.deadline,
                companySize = request.companySize,
                status = request.status,
                url = request.url,
            )

        request.coverLetters.forEach { cl ->
            application.coverLetters.add(
                CoverLetter(
                    application = application,
                    question = cl.question,
                    answer = cl.answer,
                    type = cl.type,
                ),
            )
        }

        val saved = applicationRepository.save(application)
        return ApplicationResponse.from(saved)
    }

    @Transactional
    fun updateApplication(
        id: UUID,
        userId: UUID,
        request: UpdateApplicationRequest,
    ): ApplicationResponse {
        val application = findApplicationByIdAndUserId(id, userId)

        request.companyName?.let { application.companyName = it }
        request.careerLevel?.let { application.careerLevel = it }
        request.companySize?.let { application.companySize = it }
        request.status?.let { application.status = it }
        request.url?.let { application.url = it }

        if (request.clearDeadline) {
            application.deadline = null
        } else {
            request.deadline?.let { application.deadline = it }
        }

        request.coverLetters?.let { coverLetterRequests ->
            application.coverLetters.clear()
            coverLetterRequests.forEach { cl ->
                application.coverLetters.add(
                    CoverLetter(
                        application = application,
                        question = cl.question,
                        answer = cl.answer,
                        type = cl.type,
                    ),
                )
            }
        }

        return ApplicationResponse.from(application)
    }

    @Transactional
    fun deleteApplication(
        id: UUID,
        userId: UUID,
    ) {
        val application = findApplicationByIdAndUserId(id, userId)
        application.softDelete()
    }

    private fun findApplicationByIdAndUserId(
        id: UUID,
        userId: UUID,
    ): Application =
        applicationRepository.findByIdAndUserId(id, userId)
            ?: throw BusinessException(ErrorCode.APPLICATION_NOT_FOUND)
}
