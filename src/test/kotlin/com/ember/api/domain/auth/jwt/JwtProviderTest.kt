package com.ember.api.domain.auth.jwt

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.UUID

class JwtProviderTest {
    private lateinit var jwtProvider: JwtProvider

    @BeforeEach
    fun setUp() {
        val properties =
            JwtProperties(
                secret = "test-secret-key-for-jwt-provider-unit-test-must-be-long-enough",
                accessExpiration = 3600000,
                refreshExpiration = 604800000,
            )
        jwtProvider = JwtProvider(properties)
    }

    @Test
    @DisplayName("Access Token 생성 및 userId 추출")
    fun generateAccessTokenAndExtractUserId() {
        val userId = UUID.randomUUID()

        val token = jwtProvider.generateAccessToken(userId)
        val extracted = jwtProvider.getUserIdFromToken(token)

        assertEquals(userId, extracted)
    }

    @Test
    @DisplayName("Refresh Token 생성 및 userId 추출")
    fun generateRefreshTokenAndExtractUserId() {
        val userId = UUID.randomUUID()

        val token = jwtProvider.generateRefreshToken(userId)
        val extracted = jwtProvider.getUserIdFromToken(token)

        assertEquals(userId, extracted)
    }

    @Test
    @DisplayName("유효한 토큰 검증 성공")
    fun validateValidToken() {
        val userId = UUID.randomUUID()
        val token = jwtProvider.generateAccessToken(userId)

        assertTrue(jwtProvider.validateToken(token))
    }

    @Test
    @DisplayName("잘못된 토큰 검증 실패")
    fun validateInvalidToken() {
        assertFalse(jwtProvider.validateToken("invalid.token.here"))
    }

    @Test
    @DisplayName("만료된 토큰 검증 실패")
    fun validateExpiredToken() {
        val properties =
            JwtProperties(
                secret = "test-secret-key-for-jwt-provider-unit-test-must-be-long-enough",
                accessExpiration = 0,
                refreshExpiration = 0,
            )
        val shortLivedProvider = JwtProvider(properties)
        val token = shortLivedProvider.generateAccessToken(UUID.randomUUID())

        assertFalse(shortLivedProvider.validateToken(token))
    }
}
