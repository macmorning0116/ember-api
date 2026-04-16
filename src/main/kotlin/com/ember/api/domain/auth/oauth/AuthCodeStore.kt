package com.ember.api.domain.auth.oauth

import org.springframework.stereotype.Component
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Component
class AuthCodeStore {
    private val store = ConcurrentHashMap<String, TokenPair>()

    fun createCode(
        accessToken: String,
        refreshToken: String,
    ): String {
        val code = UUID.randomUUID().toString()
        store[code] = TokenPair(accessToken, refreshToken)
        return code
    }

    fun consumeCode(code: String): TokenPair? = store.remove(code)

    data class TokenPair(
        val accessToken: String,
        val refreshToken: String,
    )
}
