package com.ember.api.domain.schedule.controller

import com.ember.api.domain.schedule.dto.CreateScheduleRequest
import com.ember.api.domain.schedule.dto.ScheduleEventResponse
import com.ember.api.domain.schedule.dto.ScheduleListResponse
import com.ember.api.domain.schedule.service.ScheduleService
import com.ember.api.global.response.ApiResponse
import com.ember.api.global.security.CurrentUser
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.util.UUID

@RestController
@RequestMapping("/api/schedule")
class ScheduleController(
    private val scheduleService: ScheduleService,
) {
    @GetMapping
    fun getSchedule(
        @RequestParam(required = false) year: Int?,
        @RequestParam(required = false) month: Int?,
    ): ResponseEntity<ApiResponse<ScheduleListResponse>> {
        val userId = CurrentUser.getUserId()
        val now = LocalDate.now()
        val schedule =
            scheduleService.getSchedule(
                userId = userId,
                year = year ?: now.year,
                month = month ?: now.monthValue,
            )
        return ResponseEntity.ok(ApiResponse.success(schedule))
    }

    @PostMapping
    fun createScheduleEvent(
        @Valid @RequestBody request: CreateScheduleRequest,
    ): ResponseEntity<ApiResponse<ScheduleEventResponse>> {
        val userId = CurrentUser.getUserId()
        val event = scheduleService.createScheduleEvent(userId, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(event))
    }

    @DeleteMapping("/{id}")
    fun deleteScheduleEvent(
        @PathVariable id: UUID,
    ): ResponseEntity<Unit> {
        val userId = CurrentUser.getUserId()
        scheduleService.deleteScheduleEvent(id, userId)
        return ResponseEntity.noContent().build()
    }
}
