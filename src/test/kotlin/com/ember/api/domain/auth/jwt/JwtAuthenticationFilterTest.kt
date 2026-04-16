package com.ember.api.domain.auth.jwt

import io.mockk.every
import io.mockk.mockk
import jakarta.servlet.FilterChain
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import java.util.UUID

class JwtAuthenticationFilterTest {
    private lateinit var jwtProvider: JwtProvider
    private lateinit var filter: JwtAuthenticationFilter

    @BeforeEach
    fun setUp() {
        jwtProvider = mockk()
        filter = JwtAuthenticationFilter(jwtProvider)
        SecurityContextHolder.clearContext()
    }

    @Test
    @DisplayName("유효한 Bearer 토큰이 있으면 SecurityContext에 인증 설정")
    fun validBearerToken() {
        val userId = UUID.randomUUID()
        val token = "valid-token"

        every { jwtProvider.validateToken(token) } returns true
        every { jwtProvider.getUserIdFromToken(token) } returns userId

        val request = MockHttpServletRequest()
        request.addHeader("Authorization", "Bearer $token")
        val response = MockHttpServletResponse()
        val chain = mockk<FilterChain>(relaxed = true)

        filter.doFilter(request, response, chain)

        val auth = SecurityContextHolder.getContext().authentication
        assertEquals(userId, auth!!.principal)
    }

    @Test
    @DisplayName("Authorization 헤더가 없으면 인증 설정 안 함")
    fun noAuthorizationHeader() {
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()
        val chain = mockk<FilterChain>(relaxed = true)

        filter.doFilter(request, response, chain)

        assertNull(SecurityContextHolder.getContext().authentication)
    }

    @Test
    @DisplayName("잘못된 토큰이면 인증 설정 안 함")
    fun invalidToken() {
        every { jwtProvider.validateToken("bad-token") } returns false

        val request = MockHttpServletRequest()
        request.addHeader("Authorization", "Bearer bad-token")
        val response = MockHttpServletResponse()
        val chain = mockk<FilterChain>(relaxed = true)

        filter.doFilter(request, response, chain)

        assertNull(SecurityContextHolder.getContext().authentication)
    }

    @Test
    @DisplayName("Bearer 접두사가 아닌 토큰은 무시")
    fun nonBearerToken() {
        val request = MockHttpServletRequest()
        request.addHeader("Authorization", "Basic some-token")
        val response = MockHttpServletResponse()
        val chain = mockk<FilterChain>(relaxed = true)

        filter.doFilter(request, response, chain)

        assertNull(SecurityContextHolder.getContext().authentication)
    }
}
