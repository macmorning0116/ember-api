package com.ember.api.domain.auth.jwt

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    val secret: String,
    val accessExpiration: Long = 3600000,
    val refreshExpiration: Long = 604800000,
)
