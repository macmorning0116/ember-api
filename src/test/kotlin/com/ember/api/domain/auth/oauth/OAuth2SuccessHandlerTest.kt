package com.ember.api.domain.auth.oauth

import com.ember.api.domain.auth.jwt.JwtProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import java.util.UUID

class OAuth2SuccessHandlerTest {
    private val jwtProvider: JwtProvider = mockk()
    private val authCodeStore: AuthCodeStore = AuthCodeStore()
    private val frontendUrl = "http://localhost:3000"
    private val handler = OAuth2SuccessHandler(jwtProvider, authCodeStore, frontendUrl)

    @Test
    @DisplayName("OAuth2 인증 성공 시 일회용 코드로 프론트 리다이렉트")
    fun onAuthenticationSuccess() {
        val userId = UUID.randomUUID()
        val attributes =
            mapOf(
                "email" to "test@example.com",
                "name" to "테스트",
                "userId" to userId.toString(),
            )
        val oAuth2User = DefaultOAuth2User(emptyList(), attributes, "email")

        val authentication = mockk<Authentication>()
        every { authentication.principal } returns oAuth2User
        every { jwtProvider.generateAccessToken(userId) } returns "access-token"
        every { jwtProvider.generateRefreshToken(userId) } returns "refresh-token"

        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()

        handler.onAuthenticationSuccess(request, response, authentication)

        verify { jwtProvider.generateAccessToken(userId) }
        verify { jwtProvider.generateRefreshToken(userId) }
        assertTrue(response.redirectedUrl!!.contains("code="))
        assertTrue(!response.redirectedUrl!!.contains("accessToken"))
    }
}
