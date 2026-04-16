package com.ember.api.domain.schedule.service

import com.ember.api.domain.application.repository.ApplicationRepository
import com.ember.api.domain.schedule.dto.CreateScheduleRequest
import com.ember.api.domain.schedule.dto.ScheduleEventResponse
import com.ember.api.domain.schedule.dto.ScheduleListResponse
import com.ember.api.domain.schedule.entity.ScheduleEvent
import com.ember.api.domain.schedule.repository.ScheduleEventRepository
import com.ember.api.domain.user.repository.UserRepository
import com.ember.api.global.exception.BusinessException
import com.ember.api.global.exception.ErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalTime
import java.time.YearMonth
import java.util.UUID

@Service
@Transactional(readOnly = true)
class ScheduleService(
    private val scheduleEventRepository: ScheduleEventRepository,
    private val applicationRepository: ApplicationRepository,
    private val userRepository: UserRepository,
) {
    fun getSchedule(
        userId: UUID,
        year: Int,
        month: Int,
    ): ScheduleListResponse {
        val yearMonth = YearMonth.of(year, month)
        val startDate = yearMonth.atDay(1).atStartOfDay()
        val endDate = yearMonth.atEndOfMonth().atTime(LocalTime.MAX)

        val manualEvents =
            scheduleEventRepository
                .findByUserIdAndDateBetweenOrderByDateAsc(userId, startDate, endDate)
                .map { ScheduleEventResponse.fromManual(it) }

        val deadlineEvents =
            applicationRepository
                .findByUserIdAndDeletedAtIsNullAndDeadlineBetween(userId, startDate, endDate)
                .map { ScheduleEventResponse.fromDeadline(it) }

        val allEvents = (manualEvents + deadlineEvents).sortedBy { it.date }

        return ScheduleListResponse(events = allEvents)
    }

    @Transactional
    fun createScheduleEvent(
        userId: UUID,
        request: CreateScheduleRequest,
    ): ScheduleEventResponse {
        val user =
            userRepository
                .findById(userId)
                .orElseThrow { BusinessException(ErrorCode.UNAUTHORIZED) }

        val application =
            request.applicationId?.let { appId ->
                applicationRepository.findByIdAndUserId(appId, userId)
            }

        val event =
            scheduleEventRepository.save(
                ScheduleEvent(
                    user = user,
                    application = application,
                    title = request.title,
                    date = request.date,
                    type = request.type,
                    memo = request.memo,
                ),
            )

        return ScheduleEventResponse.fromManual(event)
    }

    @Transactional
    fun deleteScheduleEvent(
        id: UUID,
        userId: UUID,
    ) {
        val event =
            scheduleEventRepository.findByIdAndUserId(id, userId)
                ?: throw BusinessException(ErrorCode.SCHEDULE_NOT_FOUND)

        scheduleEventRepository.delete(event)
    }
}
