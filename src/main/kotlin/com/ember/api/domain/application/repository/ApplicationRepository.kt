package com.ember.api.domain.application.repository

import com.ember.api.domain.application.entity.Application
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime
import java.util.UUID

interface ApplicationRepository : JpaRepository<Application, UUID> {
    fun findByUserIdOrderByCreatedAtDesc(userId: UUID): List<Application>

    fun findByIdAndUserId(
        id: UUID,
        userId: UUID,
    ): Application?

    fun findByUserIdAndDeletedAtIsNullAndDeadlineBetween(
        userId: UUID,
        start: LocalDateTime,
        end: LocalDateTime,
    ): List<Application>
}
