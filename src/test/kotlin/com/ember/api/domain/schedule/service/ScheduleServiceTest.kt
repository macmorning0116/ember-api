package com.ember.api.domain.schedule.service

import com.ember.api.domain.application.entity.Application
import com.ember.api.domain.application.entity.ApplicationStatus
import com.ember.api.domain.application.entity.CompanySize
import com.ember.api.domain.application.repository.ApplicationRepository
import com.ember.api.domain.schedule.dto.CreateScheduleRequest
import com.ember.api.domain.schedule.entity.ScheduleEvent
import com.ember.api.domain.schedule.entity.ScheduleEventType
import com.ember.api.domain.schedule.repository.ScheduleEventRepository
import com.ember.api.domain.user.entity.User
import com.ember.api.domain.user.repository.UserRepository
import com.ember.api.global.exception.BusinessException
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import java.util.Optional
import java.util.UUID

class ScheduleServiceTest {
    private lateinit var scheduleService: ScheduleService
    private lateinit var scheduleEventRepository: ScheduleEventRepository
    private lateinit var applicationRepository: ApplicationRepository
    private lateinit var userRepository: UserRepository

    private val userId = UUID.randomUUID()
    private lateinit var user: User

    @BeforeEach
    fun setUp() {
        scheduleEventRepository = mockk()
        applicationRepository = mockk()
        userRepository = mockk()
        scheduleService = ScheduleService(scheduleEventRepository, applicationRepository, userRepository)

        user =
            User(
                email = "test@example.com",
                name = "테스트",
                provider = "google",
                providerId = "12345",
            )
    }

    @Test
    @DisplayName("월별 일정 조회 - 수동 + 자동 이벤트 병합")
    fun getScheduleWithMergedEvents() {
        val manualEvent =
            ScheduleEvent(
                user = user,
                title = "면접 준비",
                date = LocalDateTime.of(2026, 4, 15, 10, 0),
                type = ScheduleEventType.INTERVIEW,
            ).apply {
                val idField = com.ember.api.global.entity.BaseEntity::class.java.getDeclaredField("id")
                idField.isAccessible = true
                idField.set(this, UUID.randomUUID())
            }

        val appWithDeadline =
            Application(
                user = user,
                companyName = "카카오",
                careerLevel = "신입",
                companySize = CompanySize.LARGE,
                status = ApplicationStatus.PENDING,
                deadline = LocalDateTime.of(2026, 4, 20, 23, 59),
            ).apply {
                val idField = com.ember.api.global.entity.BaseEntity::class.java.getDeclaredField("id")
                idField.isAccessible = true
                idField.set(this, UUID.randomUUID())
            }

        every {
            scheduleEventRepository.findByUserIdAndDateBetweenOrderByDateAsc(userId, any(), any())
        } returns listOf(manualEvent)

        every {
            applicationRepository.findByUserIdAndDeletedAtIsNullAndDeadlineBetween(userId, any(), any())
        } returns listOf(appWithDeadline)

        val result = scheduleService.getSchedule(userId, 2026, 4)

        assertEquals(2, result.events.size)
        assertEquals("manual", result.events[0].source)
        assertEquals("auto", result.events[1].source)
        assertEquals("카카오 마감", result.events[1].title)
    }

    @Test
    @DisplayName("일정 생성 성공")
    fun createScheduleEvent() {
        val request =
            CreateScheduleRequest(
                title = "코딩테스트",
                date = LocalDateTime.of(2026, 5, 1, 14, 0),
                type = ScheduleEventType.CODING_TEST,
            )

        every { userRepository.findById(userId) } returns Optional.of(user)
        every { scheduleEventRepository.save(any()) } answers {
            val event = firstArg<ScheduleEvent>()
            val idField = com.ember.api.global.entity.BaseEntity::class.java.getDeclaredField("id")
            idField.isAccessible = true
            idField.set(event, UUID.randomUUID())
            event
        }

        val result = scheduleService.createScheduleEvent(userId, request)

        assertEquals("코딩테스트", result.title)
        assertEquals("manual", result.source)
        verify { scheduleEventRepository.save(any()) }
    }

    @Test
    @DisplayName("존재하지 않는 일정 삭제 시 예외 발생")
    fun deleteNonExistentEvent() {
        val eventId = UUID.randomUUID()
        every { scheduleEventRepository.findByIdAndUserId(eventId, userId) } returns null

        assertThrows<BusinessException> {
            scheduleService.deleteScheduleEvent(eventId, userId)
        }
    }
}
