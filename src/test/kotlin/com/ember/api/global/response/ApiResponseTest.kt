package com.ember.api.global.response

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ApiResponseTest {
    @Test
    @DisplayName("성공 응답 with data")
    fun successWithData() {
        val response = ApiResponse.success("hello")

        assertTrue(response.success)
        assertEquals("hello", response.data)
        assertNull(response.error)
    }

    @Test
    @DisplayName("성공 응답 without data")
    fun successWithoutData() {
        val response = ApiResponse.success()

        assertTrue(response.success)
        assertNull(response.error)
    }

    @Test
    @DisplayName("에러 응답")
    fun errorResponse() {
        val response = ApiResponse.error("TEST_ERROR", "테스트 에러")

        assertEquals(false, response.success)
        assertNull(response.data)
        assertEquals("TEST_ERROR", response.error?.code)
        assertEquals("테스트 에러", response.error?.message)
    }
}
