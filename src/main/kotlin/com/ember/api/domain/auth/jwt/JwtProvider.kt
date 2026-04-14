package com.ember.api.domain.auth.jwt

import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.Date
import java.util.UUID
import javax.crypto.SecretKey

@Component
class JwtProvider(
    private val jwtProperties: JwtProperties,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val key: SecretKey by lazy {
        Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray())
    }

    fun generateAccessToken(userId: UUID): String = generateToken(userId, jwtProperties.accessExpiration)

    fun generateRefreshToken(userId: UUID): String = generateToken(userId, jwtProperties.refreshExpiration)

    fun getUserIdFromToken(token: String): UUID {
        val claims =
            Jwts
                .parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .payload
        return UUID.fromString(claims.subject)
    }

    fun validateToken(token: String): Boolean =
        try {
            Jwts
                .parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
            true
        } catch (e: JwtException) {
            log.warn("Invalid JWT token: {}", e.message)
            false
        }

    private fun generateToken(
        userId: UUID,
        expiration: Long,
    ): String {
        val now = Date()
        val expiryDate = Date(now.time + expiration)

        return Jwts
            .builder()
            .subject(userId.toString())
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(key)
            .compact()
    }
}
