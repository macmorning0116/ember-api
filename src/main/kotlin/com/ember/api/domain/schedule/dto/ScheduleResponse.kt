package com.ember.api.domain.schedule.dto

import com.ember.api.domain.application.entity.Application
import com.ember.api.domain.schedule.entity.ScheduleEvent
import java.time.LocalDateTime

data class ScheduleEventResponse(
    val id: String,
    val title: String,
    val date: LocalDateTime,
    val type: String,
    val source: String,
    val memo: String? = null,
    val status: String? = null,
) {
    companion object {
        fun fromManual(event: ScheduleEvent): ScheduleEventResponse =
            ScheduleEventResponse(
                id = event.id.toString(),
                title = event.title,
                date = event.date,
                type = event.type.name,
                source = "manual",
                memo = event.memo,
            )

        fun fromDeadline(application: Application): ScheduleEventResponse =
            ScheduleEventResponse(
                id = "app-${application.id}",
                title = "${application.companyName} 마감",
                date = application.deadline!!,
                type = "DEADLINE",
                source = "auto",
                status = application.status.label,
            )
    }
}

data class ScheduleListResponse(
    val events: List<ScheduleEventResponse>,
)
