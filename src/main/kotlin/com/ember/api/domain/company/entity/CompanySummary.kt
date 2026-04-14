package com.ember.api.domain.company.entity

import com.ember.api.global.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "company_summaries")
class CompanySummary(
    @Column(nullable = false, unique = true)
    val companyName: String,
    @Column(nullable = false, columnDefinition = "TEXT")
    var overview: String,
    @Column(nullable = false, columnDefinition = "TEXT")
    var mainBusiness: String,
    @Column(nullable = false, columnDefinition = "TEXT")
    var recentNews: String,
    @Column(nullable = false, columnDefinition = "TEXT")
    var motivationHints: String,
    @Column(nullable = false)
    var crawledAt: LocalDateTime,
) : BaseEntity()
