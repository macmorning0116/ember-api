package com.ember.api.domain.application.repository

import com.ember.api.domain.application.entity.CoverLetter
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface CoverLetterRepository : JpaRepository<CoverLetter, UUID>
