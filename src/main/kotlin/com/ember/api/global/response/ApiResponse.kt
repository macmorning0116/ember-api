package com.ember.api.global.response

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: ErrorDetail? = null,
) {
    companion object {
        fun <T> success(data: T): ApiResponse<T> = ApiResponse(success = true, data = data)

        fun success(): ApiResponse<Unit> = ApiResponse(success = true)

        fun error(
            code: String,
            message: String,
        ): ApiResponse<Nothing> = ApiResponse(success = false, error = ErrorDetail(code, message))
    }
}

data class ErrorDetail(
    val code: String,
    val message: String,
)
