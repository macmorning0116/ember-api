package com.ember.api.domain.schedule.repository

import com.ember.api.domain.schedule.entity.ScheduleEvent
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime
import java.util.UUID

interface ScheduleEventRepository : JpaRepository<ScheduleEvent, UUID> {
    fun findByUserIdAndDateBetweenOrderByDateAsc(
        userId: UUID,
        start: LocalDateTime,
        end: LocalDateTime,
    ): List<ScheduleEvent>

    fun findByIdAndUserId(
        id: UUID,
        userId: UUID,
    ): ScheduleEvent?
}
