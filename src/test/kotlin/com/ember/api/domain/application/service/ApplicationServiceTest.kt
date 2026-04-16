package com.ember.api.domain.application.service

import com.ember.api.domain.application.dto.CoverLetterRequest
import com.ember.api.domain.application.dto.CreateApplicationRequest
import com.ember.api.domain.application.dto.UpdateApplicationRequest
import com.ember.api.domain.application.entity.Application
import com.ember.api.domain.application.entity.ApplicationStatus
import com.ember.api.domain.application.entity.CompanySize
import com.ember.api.domain.application.entity.CoverLetterType
import com.ember.api.domain.application.repository.ApplicationRepository
import com.ember.api.domain.user.entity.User
import com.ember.api.domain.user.repository.UserRepository
import com.ember.api.global.exception.BusinessException
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.Optional
import java.util.UUID

class ApplicationServiceTest {
    private lateinit var applicationService: ApplicationService
    private lateinit var applicationRepository: ApplicationRepository
    private lateinit var userRepository: UserRepository

    private val userId = UUID.randomUUID()
    private val appId = UUID.randomUUID()
    private lateinit var user: User
    private lateinit var application: Application

    @BeforeEach
    fun setUp() {
        applicationRepository = mockk()
        userRepository = mockk()
        applicationService = ApplicationService(applicationRepository, userRepository)

        user =
            User(
                email = "test@example.com",
                name = "테스트",
                provider = "google",
                providerId = "12345",
            )

        application =
            Application(
                user = user,
                companyName = "테스트기업",
                careerLevel = "신입",
                companySize = CompanySize.LARGE,
                status = ApplicationStatus.PENDING,
            ).apply {
                val idField = com.ember.api.global.entity.BaseEntity::class.java.getDeclaredField("id")
                idField.isAccessible = true
                idField.set(this, appId)
            }
    }

    @Test
    @DisplayName("지원서 목록 조회 성공")
    fun getApplications() {
        every { applicationRepository.findByUserIdOrderByCreatedAtDesc(userId) } returns listOf(application)

        val result = applicationService.getApplications(userId)

        assertEquals(1, result.size)
        assertEquals("테스트기업", result[0].companyName)
    }

    @Test
    @DisplayName("지원서 단건 조회 성공")
    fun getApplication() {
        every { applicationRepository.findByIdAndUserId(appId, userId) } returns application

        val result = applicationService.getApplication(appId, userId)

        assertEquals("테스트기업", result.companyName)
        assertEquals(CompanySize.LARGE, result.companySize)
    }

    @Test
    @DisplayName("존재하지 않는 지원서 조회 시 예외 발생")
    fun getApplicationNotFound() {
        every { applicationRepository.findByIdAndUserId(appId, userId) } returns null

        assertThrows<BusinessException> {
            applicationService.getApplication(appId, userId)
        }
    }

    @Test
    @DisplayName("지원서 생성 성공")
    fun createApplication() {
        val request =
            CreateApplicationRequest(
                companyName = "네이버",
                careerLevel = "신입",
                companySize = CompanySize.LARGE,
                status = ApplicationStatus.PENDING,
                coverLetters =
                    listOf(
                        CoverLetterRequest(
                            question = "지원 동기",
                            answer = "성장하고 싶습니다.",
                            type = CoverLetterType.MOTIVATION,
                        ),
                    ),
            )

        every { userRepository.findById(userId) } returns Optional.of(user)
        every { applicationRepository.save(any()) } answers {
            val app = firstArg<Application>()
            val idField = com.ember.api.global.entity.BaseEntity::class.java.getDeclaredField("id")
            idField.isAccessible = true
            idField.set(app, UUID.randomUUID())
            app.coverLetters.forEach { cl ->
                idField.set(cl, UUID.randomUUID())
            }
            app
        }

        val result = applicationService.createApplication(userId, request)

        assertEquals("네이버", result.companyName)
        verify { applicationRepository.save(any()) }
    }

    @Test
    @DisplayName("지원서 상태 수정 성공")
    fun updateApplicationStatus() {
        val request =
            UpdateApplicationRequest(
                status = ApplicationStatus.APPLIED,
            )

        every { applicationRepository.findByIdAndUserId(appId, userId) } returns application

        val result = applicationService.updateApplication(appId, userId, request)

        assertEquals(ApplicationStatus.APPLIED, result.status)
    }

    @Test
    @DisplayName("지원서 소프트 삭제 성공")
    fun deleteApplication() {
        every { applicationRepository.findByIdAndUserId(appId, userId) } returns application

        applicationService.deleteApplication(appId, userId)

        assertNotNull(application.deletedAt)
    }
}
