package com.ember.api.global.exception

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class GlobalExceptionHandlerTest {
    private val handler = GlobalExceptionHandler()

    @Test
    @DisplayName("BusinessException 처리")
    fun handleBusinessException() {
        val exception = BusinessException(ErrorCode.APPLICATION_NOT_FOUND)

        val response = handler.handleBusinessException(exception)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertFalse(response.body!!.success)
        assertEquals("APPLICATION_NOT_FOUND", response.body!!.error!!.code)
    }

    @Test
    @DisplayName("일반 Exception 처리")
    fun handleException() {
        val exception = RuntimeException("unexpected error")

        val response = handler.handleException(exception)

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertFalse(response.body!!.success)
        assertEquals("INTERNAL_ERROR", response.body!!.error!!.code)
    }
}
