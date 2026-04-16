package com.ember.api.domain.application.controller

import com.ember.api.domain.application.dto.ApplicationResponse
import com.ember.api.domain.application.dto.CoverLetterResponse
import com.ember.api.domain.application.dto.CreateApplicationRequest
import com.ember.api.domain.application.entity.ApplicationStatus
import com.ember.api.domain.application.entity.CompanySize
import com.ember.api.domain.application.entity.CoverLetterType
import com.ember.api.domain.application.service.ApplicationService
import com.ember.api.global.exception.BusinessException
import com.ember.api.global.exception.ErrorCode
import com.ember.api.global.security.CurrentUser
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus
import java.time.LocalDateTime
import java.util.UUID

class ApplicationControllerTest {
    private lateinit var controller: ApplicationController
    private lateinit var applicationService: ApplicationService
    private val userId = UUID.randomUUID()
    private val appId = UUID.randomUUID()

    private fun sampleResponse() =
        ApplicationResponse(
            id = appId,
            companyName = "네이버",
            careerLevel = "신입",
            deadline = LocalDateTime.of(2026, 5, 1, 0, 0),
            companySize = CompanySize.LARGE,
            status = ApplicationStatus.PENDING,
            url = "https://naver.com",
            coverLetters =
                listOf(
                    CoverLetterResponse(
                        id = UUID.randomUUID(),
                        question = "지원 동기",
                        answer = "성장하고 싶습니다.",
                        type = CoverLetterType.MOTIVATION,
                    ),
                ),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        )

    @BeforeEach
    fun setUp() {
        applicationService = mockk()
        controller = ApplicationController(applicationService)
        mockkObject(CurrentUser)
        every { CurrentUser.getUserId() } returns userId
    }

    @AfterEach
    fun tearDown() {
        unmockkObject(CurrentUser)
    }

    @Test
    @DisplayName("목록 조회 성공")
    fun getApplications() {
        every { applicationService.getApplications(userId) } returns listOf(sampleResponse())

        val response = controller.getApplications()

        assertEquals(HttpStatus.OK, response.statusCode)
        assertTrue(response.body!!.success)
        assertEquals(1, response.body!!.data!!.size)
    }

    @Test
    @DisplayName("단건 조회 성공")
    fun getApplication() {
        every { applicationService.getApplication(appId, userId) } returns sampleResponse()

        val response = controller.getApplication(appId)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("네이버", response.body!!.data!!.companyName)
    }

    @Test
    @DisplayName("단건 조회 실패 - 404")
    fun getApplicationNotFound() {
        every { applicationService.getApplication(appId, userId) } throws
            BusinessException(ErrorCode.APPLICATION_NOT_FOUND)

        assertThrows<BusinessException> {
            controller.getApplication(appId)
        }
    }

    @Test
    @DisplayName("생성 성공")
    fun createApplication() {
        every { applicationService.createApplication(userId, any()) } returns sampleResponse()

        val request =
            CreateApplicationRequest(
                companyName = "네이버",
                careerLevel = "신입",
                companySize = CompanySize.LARGE,
                status = ApplicationStatus.PENDING,
            )
        val response = controller.createApplication(request)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertTrue(response.body!!.success)
    }

    @Test
    @DisplayName("삭제 성공")
    fun deleteApplication() {
        justRun { applicationService.deleteApplication(appId, userId) }

        val response = controller.deleteApplication(appId)

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
    }
}
