package com.ember.api.domain.policy.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "youth_policies")
@EntityListeners(AuditingEntityListener::class)
class YouthPolicy(
    @Id
    @Column(length = 50)
    val plcyNo: String,
    @Column(nullable = false)
    var name: String,
    @Column(nullable = false)
    var agency: String,
    @Column(nullable = false)
    var mainCategory: String,
    @Column(nullable = false)
    var category: String,
    @Column(nullable = false, columnDefinition = "TEXT")
    var description: String,
    @Column(columnDefinition = "TEXT")
    var supportContent: String? = null,
    var applicationUrl: String? = null,
    @Column(nullable = false)
    var viewCount: Int = 0,
    var region: String? = null,
    var zipCodes: String? = null,
    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @LastModifiedDate
    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),
)
