package com.ember.api.domain.auth.oauth

import com.ember.api.domain.auth.jwt.JwtProvider
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class OAuth2SuccessHandler(
    private val jwtProvider: JwtProvider,
    @Value("\${app.frontend-url}") private val frontendUrl: String,
) : AuthenticationSuccessHandler {
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication,
    ) {
        val oAuth2User = authentication.principal as OAuth2User
        val userId = UUID.fromString(oAuth2User.attributes["userId"] as String)

        val accessToken = jwtProvider.generateAccessToken(userId)
        val refreshToken = jwtProvider.generateRefreshToken(userId)

        val redirectUrl =
            "$frontendUrl/auth/callback?accessToken=$accessToken&refreshToken=$refreshToken"
        response.sendRedirect(redirectUrl)
    }
}
