package com.ember.api.domain.quiz.repository

import com.ember.api.domain.quiz.entity.UserQuizResult
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserQuizResultRepository : JpaRepository<UserQuizResult, UUID>
