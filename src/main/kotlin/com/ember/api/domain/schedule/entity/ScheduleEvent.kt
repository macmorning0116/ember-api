package com.ember.api.domain.schedule.entity

import com.ember.api.domain.application.entity.Application
import com.ember.api.domain.user.entity.User
import com.ember.api.global.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "schedule_events")
class ScheduleEvent(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    val application: Application? = null,
    @Column(nullable = false)
    var title: String,
    @Column(nullable = false)
    var date: LocalDateTime,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var type: ScheduleEventType,
    var memo: String? = null,
) : BaseEntity()
