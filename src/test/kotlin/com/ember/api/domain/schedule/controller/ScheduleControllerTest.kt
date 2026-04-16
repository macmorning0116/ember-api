package com.ember.api.domain.schedule.controller

import com.ember.api.domain.schedule.dto.ScheduleEventResponse
import com.ember.api.domain.schedule.dto.ScheduleListResponse
import com.ember.api.domain.schedule.service.ScheduleService
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
import org.springframework.http.HttpStatus
import java.time.LocalDateTime
import java.util.UUID

class ScheduleControllerTest {
    private lateinit var controller: ScheduleController
    private lateinit var scheduleService: ScheduleService
    private val userId = UUID.randomUUID()

    @BeforeEach
    fun setUp() {
        scheduleService = mockk()
        controller = ScheduleController(scheduleService)
        mockkObject(CurrentUser)
        every { CurrentUser.getUserId() } returns userId
    }

    @AfterEach
    fun tearDown() {
        unmockkObject(CurrentUser)
    }

    @Test
    @DisplayName("월별 일정 조회 성공")
    fun getSchedule() {
        val response =
            ScheduleListResponse(
                events =
                    listOf(
                        ScheduleEventResponse(
                            id = UUID.randomUUID().toString(),
                            title = "면접",
                            date = LocalDateTime.of(2026, 4, 15, 10, 0),
                            type = "INTERVIEW",
                            source = "manual",
                        ),
                    ),
            )
        every { scheduleService.getSchedule(userId, 2026, 4) } returns response

        val result = controller.getSchedule(2026, 4)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertTrue(result.body!!.success)
        assertEquals(
            1,
            result.body!!
                .data!!
                .events.size,
        )
    }

    @Test
    @DisplayName("기본 연도/월 사용")
    fun getScheduleDefaultParams() {
        val response = ScheduleListResponse(events = emptyList())
        every { scheduleService.getSchedule(eq(userId), any(), any()) } returns response

        val result = controller.getSchedule(null, null)

        assertEquals(HttpStatus.OK, result.statusCode)
    }

    @Test
    @DisplayName("일정 삭제 성공")
    fun deleteScheduleEvent() {
        val eventId = UUID.randomUUID()
        justRun { scheduleService.deleteScheduleEvent(eventId, userId) }

        val result = controller.deleteScheduleEvent(eventId)

        assertEquals(HttpStatus.NO_CONTENT, result.statusCode)
    }
}
