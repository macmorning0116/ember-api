package com.ember.api.global.security

import com.ember.api.global.exception.BusinessException
import com.ember.api.global.exception.ErrorCode
import org.springframework.security.core.context.SecurityContextHolder
import java.util.UUID

object CurrentUser {
    fun getUserId(): UUID {
        val authentication =
            SecurityContextHolder.getContext().authentication
                ?: throw BusinessException(ErrorCode.UNAUTHORIZED)

        val principal = authentication.principal
        if (principal !is UUID) {
            throw BusinessException(ErrorCode.UNAUTHORIZED)
        }
        return principal
    }
}
