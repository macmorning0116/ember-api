package com.ember.api.domain.application.entity

import com.ember.api.global.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "cover_letters")
class CoverLetter(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    val application: Application,
    @Column(nullable = false)
    var question: String,
    @Column(nullable = false, columnDefinition = "TEXT")
    var answer: String,
    @Enumerated(EnumType.STRING)
    var type: CoverLetterType? = null,
) : BaseEntity()
