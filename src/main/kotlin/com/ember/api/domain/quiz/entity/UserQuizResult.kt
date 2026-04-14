package com.ember.api.domain.quiz.entity

import com.ember.api.domain.user.entity.User
import com.ember.api.global.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "user_quiz_results")
class UserQuizResult(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,
    @Column(nullable = false, columnDefinition = "TEXT")
    val answers: String,
    @Column(nullable = false, columnDefinition = "TEXT")
    val recommendations: String,
) : BaseEntity()
