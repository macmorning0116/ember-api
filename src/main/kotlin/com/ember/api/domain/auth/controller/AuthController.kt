package com.ember.api.domain.auth.controller

import com.ember.api.domain.auth.oauth.AuthCodeStore
import com.ember.api.global.exception.BusinessException
import com.ember.api.global.exception.ErrorCode
import com.ember.api.global.response.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authCodeStore: AuthCodeStore,
) {
    @PostMapping("/token")
    fun exchangeToken(
        @RequestBody request: TokenExchangeRequest,
    ): ResponseEntity<ApiResponse<TokenResponse>> {
        val tokenPair =
            authCodeStore.consumeCode(request.code)
                ?: throw BusinessException(ErrorCode.UNAUTHORIZED)

        return ResponseEntity.ok(
            ApiResponse.success(
                TokenResponse(
                    accessToken = tokenPair.accessToken,
                    refreshToken = tokenPair.refreshToken,
                ),
            ),
        )
    }
}

data class TokenExchangeRequest(
    val code: String,
)

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
)
